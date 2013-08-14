package io.cloudsoft.ravello.brooklyn;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import brooklyn.location.LocationSpec;
import brooklyn.location.NoMachinesAvailableException;
import brooklyn.location.basic.SshMachineLocation;
import brooklyn.location.cloud.AbstractCloudMachineProvisioningLocation;
import brooklyn.util.collections.MutableMap;
import brooklyn.util.flags.SetFromFlag;
import brooklyn.util.text.Identifiers;
import brooklyn.util.time.Time;
import io.cloudsoft.ravello.api.RavelloApi;
import io.cloudsoft.ravello.client.RavelloApiImpl;
import io.cloudsoft.ravello.dto.ApplicationDto;
import io.cloudsoft.ravello.dto.HardDriveDto;
import io.cloudsoft.ravello.dto.IpConfigDto;
import io.cloudsoft.ravello.dto.NetworkConnectionDto;
import io.cloudsoft.ravello.dto.NetworkDeviceDto;
import io.cloudsoft.ravello.dto.SizeDto;
import io.cloudsoft.ravello.dto.SuppliedServiceDto;
import io.cloudsoft.ravello.dto.VmDto;

public class RavelloLocation extends AbstractCloudMachineProvisioningLocation {

    private static final Logger LOG = LoggerFactory.getLogger(RavelloLocation.class);

    private RavelloApi ravello = null;
    private ApplicationDto applicationModel = null;
    private final Object lock = new Object();

    private final String sshUsername = "ravello";
    @SetFromFlag private String privateKeyFile;
    @SetFromFlag private String privateKeyId;

    /**
     * TODO:
     *  - handle exceptions properly
     */

    public RavelloLocation() {
    }

    @Override
    public void configure(Map properties) {
        super.configure(properties);
        // TODO: Is a config key for the RavelloApi implementation worthwhile?
        String apiEndpoint = "https://cloud.ravellosystems.com/services";
        String username = (String) checkNotNull(properties.get("username"), "username");
        String password = (String) checkNotNull(properties.get("password"), "password");
        ravello = new RavelloApiImpl(apiEndpoint, username, password);
//        ravello = new RavelloApiLocalImpl();
    }

    public RavelloSshLocation obtain() throws NoMachinesAvailableException {
        return obtain(MutableMap.of());
    }

    @Override
    public RavelloSshLocation obtain(Map<?, ?> flags) throws NoMachinesAvailableException {
        checkNotNull(ravello, "Ravello API has not been configured");

        // Add a new VM with an SSH service
        VmDto created;
        final VmDto newMachine = makeVmDto();

        synchronized (lock) {
            // Create the app if necessary
            if (applicationModel == null) {
                applicationModel = createEmptyApplication();
            }

            // Update the app
            LOG.info("Adding new VM to {}: {}", applicationModel, newMachine);
            ApplicationDto forUpdate = applicationModel.toBuilder()
                    .addVm(newMachine)
                    .build();
            ravello.getApplicationApi().update(applicationModel.getId(), forUpdate);

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
            LOG.trace("App {} after update: {}", applicationModel.getId(), applicationModel);
        }

        LOG.info("Created new VM: " + created);

        // TODO: Wait for SSHable.
        LOG.info("SLEEPING FOR TEN MINUTES");
        try { Thread.sleep(1000*60*10); } catch (InterruptedException e) {}

        String hostname = created.getRuntimeInformation().getExternalFullyQualifiedDomainName();
        return getManagementContext().getLocationManager().createLocation(LocationSpec.spec(RavelloSshLocation.class)
                .configure("address", hostname)
                .configure("ravelloParent", this)
                .configure("displayName", hostname)
                .configure("user", sshUsername)
                .configure("privateKeyFile", privateKeyFile)
                .configure("vm", created));
    }



    @Override
    public void release(SshMachineLocation machine) {
        checkNotNull(ravello, "Ravello API has not been configured");
        checkNotNull(applicationModel, "applicationModel is null. Was it deleted?");
        checkArgument(machine instanceof RavelloSshLocation,
                "release() given instance of "+machine.getClass().getName()+", expected instance of "+RavelloSshLocation.class.getName());
        RavelloSshLocation ravelloMachine = RavelloSshLocation.class.cast(machine);
        LOG.info("Removing machine with VM: " + ravelloMachine.getVm());
        String appId = applicationModel.getId();
        synchronized (lock) {
            ApplicationDto forUpdate = ApplicationDto.builder()
                    .fromApplicationDto(applicationModel)
                    .removeVm(ravelloMachine.getVm().getId())
                    .build();
            // Kill application entirely if no VMs remain.
            if (forUpdate.getVMs().isEmpty()) {
                LOG.info("No VMs remaining in application[{}]. Going to delete.", appId);
                deleteApplicationModel();
            } else {
                ravello.getApplicationApi().update(appId, forUpdate);
                ravello.getApplicationApi().publishUpdates(appId);
                applicationModel = ravello.getApplicationApi().get(appId);
                LOG.trace("Removed {} from application. New model: {}", machine, applicationModel);
            }
        }
    }

    public void deleteApplicationModel() {
        checkNotNull(ravello, "Ravello API has not been configured");
        synchronized (lock) {
            if (applicationModel != null) {
                LOG.info("Deleting application model: " + applicationModel.getId());
                ravello.getApplicationApi().delete(applicationModel.getId());
            }
            applicationModel = null;
        }
    }

    private ApplicationDto createEmptyApplication() {
        LOG.info("Creating empty application for " + this);
        ApplicationDto toCreate = ApplicationDto.builder()
            .name(nameFor("app"))
            .version(0)
            .description("Brooklyn application")
            .build();
        ApplicationDto created = ravello.getApplicationApi().create(toCreate);
        checkState(created != null, "Failed to create empty Brooklyn application!");
        return created;
    }

    private VmDto makeVmDto() {
        String vmNameAndHostname = nameFor("vm");
        return VmDto.builder()
                .baseVmId("1671271")
                .name(vmNameAndHostname)
                .description("Test VM")
                .numCpus(1)
                .memorySize(SizeDto.gigabytes(1))
                .hostname(vmNameAndHostname)
                .keypairId(privateKeyId)
                .requiresKeypair(true)
                .hardDrives(HardDriveDto.builder()
                        .name(nameFor("hard-drive"))
                        .size(SizeDto.gigabytes(20))
                        .controller("virtio")
                        .boot(true)
                        .index(0)
                        .controllerPciSlot(0)
                        .controllerIndex(0)
                        .build())
                .networkConnections(NetworkConnectionDto.builder()
                        .name(nameFor("networkConnection"))
                        .accessPort(true)
                        .device(NetworkDeviceDto.builder()
                                .useAutomaticMac()
                                .deviceType("virtio")
                                .index(0)
                                .build())
                        .ipConfig(new IpConfigDto())
                        .build())
                .suppliedServices(SuppliedServiceDto.SSH_SERVICE)
                .build();
    }

    private String nameFor(String type) {
        return type + "-" + Identifiers.makeRandomId(8);
    }

}
