package io.cloudsoft.ravello.brooklyn;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.location.NoMachinesAvailableException;
import brooklyn.location.basic.SshMachineLocation;
import brooklyn.location.cloud.AbstractCloudMachineProvisioningLocation;
import brooklyn.util.text.Identifiers;
import io.cloudsoft.ravello.api.RavelloApi;
import io.cloudsoft.ravello.client.RavelloApiImpl;
import io.cloudsoft.ravello.dto.ApplicationDto;

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
        String apiEndpoint = "https://cloud.ravellosystems.com/services";
        String username = (String) checkNotNull(properties.get("username"), "username");
        String password = (String) checkNotNull(properties.get("password"), "password");
        ravello = new RavelloApiImpl(apiEndpoint, username, password);
    }

    @Override
    public RavelloSshLocation obtain(Map<?, ?> flags) throws NoMachinesAvailableException {
        checkNotNull(ravello, "Ravello API has not been configured");

        synchronized (lock) {
            if (applicationModel == null) {
                applicationModel = createAndPublishEmptyApplication();
            }
            ravello.getApplicationApi().publishUpdates(applicationModel.getId());
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public void release(SshMachineLocation machine) {
        checkNotNull(ravello, "Ravello API has not been configured");
        checkArgument(machine instanceof RavelloSshLocation,
                "release() given instance of "+machine.getClass().getName()+", expected instance of "+RavelloSshLocation.class.getName());
        RavelloSshLocation ravelloMachine = RavelloSshLocation.class.cast(machine);
        //ravello.getApplicationApi().delete(ravelloMachine.getVm().getId());
        throw new UnsupportedOperationException();
    }

    private ApplicationDto createAndPublishEmptyApplication() {
        LOG.info("Creating empty application for " + this);
        ApplicationDto toCreate = new ApplicationDto(null, nameFor("app"), "Brooklyn application", false);
        ApplicationDto created = ravello.getApplicationApi().create(toCreate);
        checkState(created != null, "Failed to create empty Brooklyn application!");

        LOG.info("Publishing application for " + this);
        ravello.getApplicationApi().publish(created.getId(), "AMAZON", "Virginia");

        LOG.info("Created new application for " + this);
        return ravello.getApplicationApi().get(created.getId());
    }

    private String nameFor(String type) {
        return type + "-" + Identifiers.makeRandomId(8);
    }

}
