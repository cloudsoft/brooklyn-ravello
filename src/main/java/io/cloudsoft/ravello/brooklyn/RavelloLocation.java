package io.cloudsoft.ravello.brooklyn;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.location.NoMachinesAvailableException;
import brooklyn.location.basic.SshMachineLocation;
import brooklyn.location.cloud.AbstractCloudMachineProvisioningLocation;
import brooklyn.util.text.Identifiers;
import io.cloudsoft.ravello.api.RavelloApi;
import io.cloudsoft.ravello.client.RavelloApiImpl;
import io.cloudsoft.ravello.client.RavelloApiLocalImpl;
import io.cloudsoft.ravello.dto.ApplicationDto;
import io.cloudsoft.ravello.dto.SuppliedServiceDto;
import io.cloudsoft.ravello.dto.VmDto;

public class RavelloLocation extends AbstractCloudMachineProvisioningLocation {

    private static final Logger LOG = LoggerFactory.getLogger(RavelloLocation.class);

    private RavelloApi ravello = null;
    private ApplicationDto applicationModel = null;
    private final Object lock = new Object();

    /**
     * TODO:
     *  - get keypair for SSH from property
     *  - thread safety of applicationModel object
     *  - handle exceptions properly
     *  - stopping applications
     *  - config keys
     */

    public RavelloLocation() {
    }

    @Override
    public void configure(Map properties) {
        super.configure(properties);
        //String apiEndpoint = "https://cloud.ravellosystems.com/services";
        //String username = (String) checkNotNull(properties.get("username"), "username");
        //String password = (String) checkNotNull(properties.get("password"), "password");
        //ravello = new RavelloApiImpl(apiEndpoint, username, password);
        ravello = new RavelloApiLocalImpl();
    }

    @Override
    public RavelloSshLocation obtain(Map<?, ?> flags) throws NoMachinesAvailableException {
        checkNotNull(ravello, "Ravello API has not been configured");

        // Add a new VM with an SSH service
        VmDto created;
        final VmDto newMachine = VmDto.builder()
                .name(nameFor("vm"))
                .suppliedServices(SuppliedServiceDto.SSH_SERVICE)
                .build();

        synchronized (lock) {
            // Create the app if necessary
            if (applicationModel == null) {
                applicationModel = createEmptyApplication();
            }

            LOG.info("Updating app");
            // Update the app
            ApplicationDto forUpdate = applicationModel.toBuilder()
                    .incrementVersion()
                    .addVm(newMachine)
                    .build();
            ravello.getApplicationApi().update(applicationModel.getId(), forUpdate);
            LOG.info("Update done.");

            // If the app has not been published before, publish the app to chosen cloud.
            // Otherwise, publish updates.
            if (!applicationModel.isPublished()) {
                LOG.info("Publishing application for " + this);
                ravello.getApplicationApi().publish(forUpdate.getId(), "AMAZON", "Virginia");
            } else {
                ravello.getApplicationApi().publishUpdates(forUpdate.getId());
            }

            // Fetch the latest model of the app and get the VM with name matching the one created
            applicationModel = ravello.getApplicationApi().get(forUpdate.getId());
            created = Iterables.find(applicationModel.getVMs(), new Predicate<VmDto>() {
                @Override public boolean apply(VmDto input) {
                    return input.getName().equals(newMachine.getName());
                }
            });
        }

        LOG.info("Created new VM: " + created);
        return new RavelloSshLocation(created);
    }

    @Override
    public void release(SshMachineLocation machine) {
        checkNotNull(ravello, "Ravello API has not been configured");
        checkArgument(machine instanceof RavelloSshLocation,
                "release() given instance of "+machine.getClass().getName()+", expected instance of "+RavelloSshLocation.class.getName());
        RavelloSshLocation ravelloMachine = RavelloSshLocation.class.cast(machine);

        synchronized (lock) {
            //applicationModel.removeVm(ravelloMachine);
            //ravello.getApplicationApi().delete(ravelloMachine.getVm().getId());
            //ravello.getApplicationApi().publishUpdates(applicationModel.getId());
        }
        throw new UnsupportedOperationException();
    }

    private ApplicationDto createEmptyApplication() {
        LOG.info("Creating empty application for " + this);
        ApplicationDto toCreate = ApplicationDto.builder()
            .name(nameFor("app"))
            .description("Brooklyn application")
            .build();
        ApplicationDto created = ravello.getApplicationApi().create(toCreate);
        checkState(created != null, "Failed to create empty Brooklyn application!");
        return created;
    }

    private String nameFor(String type) {
        return type + "-" + Identifiers.makeRandomId(8);
    }

}
