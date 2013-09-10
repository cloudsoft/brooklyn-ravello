package io.cloudsoft.ravello.brooklyn;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import brooklyn.util.text.Identifiers;
import io.cloudsoft.ravello.api.RavelloApi;
import io.cloudsoft.ravello.dto.ApplicationDto;
import io.cloudsoft.ravello.dto.HardDriveDto;
import io.cloudsoft.ravello.dto.IpConfigDto;
import io.cloudsoft.ravello.dto.NetworkConnectionDto;
import io.cloudsoft.ravello.dto.NetworkDeviceDto;
import io.cloudsoft.ravello.dto.SizeDto;
import io.cloudsoft.ravello.dto.SuppliedServiceDto;
import io.cloudsoft.ravello.dto.VmDto;

public class RavelloLocationApplicationManager {

    private static final Logger LOG = LoggerFactory.getLogger(RavelloLocationApplicationManager.class);

    private final RavelloApi ravello;
    private final String privateKeyId;
    private final String targetCloud;
    private final String targetRegion;

    // Accesses to applicationModel should be synchronised
    /**
     * Holds the current representation of the application. This should mirror the object returned
     * by a GET to /..ravello../applications/:id, but might NOT represent the true published application.
     */
    private ApplicationDto applicationModel;

    /** Creates an application manager that publishes applications to the given cloud and region. */
    RavelloLocationApplicationManager(RavelloApi ravelloApi, String privateKeyId, String targetCloud, String targetRegion) {
        this.ravello = ravelloApi;
        this.privateKeyId = privateKeyId;
        this.targetCloud = targetCloud;
        this.targetRegion = targetRegion;
    }

    public VmDto createNewPublishedVM(Collection<?> inboundPorts) {
        checkNotNull(inboundPorts, "inboundPorts");

        final VmDto newMachine;
        final ApplicationDto forUpdate, updated;

        synchronized (this) {
            if (applicationModel == null) {
                applicationModel = createEmptyApplication();
            }

            // Update the app
            newMachine = makeVmDto(inboundPorts);
            LOG.info("Adding new VM to {}: {}", applicationModel, newMachine);
            forUpdate = applicationModel.toBuilder()
                    .addVm(newMachine)
                    .build();
            applicationModel = ravello.getApplicationApi().update(applicationModel.getId(), forUpdate);
            LOG.trace("App {} after update: {}", applicationModel.getId(), applicationModel);
            updated = publishApplication();
        }

        // Fetch the latest model of the app and get the VM with name matching the one created
        VmDto created = Iterables.find(updated.getVMs(), new Predicate<VmDto>() {
            @Override
            public boolean apply(VmDto input) {
                return input.getName().equals(newMachine.getName());
            }
        });

        return created;
    }

    /** Sets and returns applicationDto to latest version from server */
    private synchronized ApplicationDto publishApplication() {
        String appId = applicationModel.getId();

        // If the app has not been published before, publish the app to chosen cloud.
        // Otherwise, publish updates.
        if (!applicationModel.isPublished()) {
            LOG.info("Publishing app[{}] to {}:{}", appId, targetCloud, targetRegion);
            ravello.getApplicationApi().publish(appId, targetCloud, targetRegion);
        } else {
            LOG.info("Publishing updates for app[{}]", appId);
            ravello.getApplicationApi().publishUpdates(appId);
        }

        // Update application model
        applicationModel = ravello.getApplicationApi().get(appId);
        return applicationModel;
    }

    public void release(VmDto vm) {
        synchronized (this) {
            String appId = applicationModel.getId();
            LOG.info("Removing VM from {}: {}", appId, vm);
            ApplicationDto forUpdate = ApplicationDto.builder()
                    .fromApplicationDto(applicationModel)
                    .removeVm(vm.getId())
                    .build();
            // Kill application entirely if no VMs remain.
            if (forUpdate.getVMs().isEmpty()) {
                LOG.info("No VMs remaining in app[{}]. Going to delete.", appId);
                deleteApplicationModel();
            } else {
                ravello.getApplicationApi().update(appId, forUpdate);
                publishApplication();
                LOG.trace("Removed {} from app[{}]. New model: {}", vm, appId, applicationModel);
            }
        }
    }

    public synchronized void deleteApplicationModel() {
        if (applicationModel != null) {
            LOG.info("Deleting application model: " + applicationModel.getId());
            ravello.getApplicationApi().delete(applicationModel.getId());
            applicationModel = null;
        }
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

    private VmDto makeVmDto(Collection<?> inboundPorts) {
        // TODO: Could use Brooklyn's CloudMachineNamer
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
                .suppliedServices(getServices(inboundPorts))
                .build();
    }

    private Set<SuppliedServiceDto> getServices(Collection<?> inboundPorts) {
        checkNotNull(inboundPorts, "inboundPorts");

        Predicate<SuppliedServiceDto> isSshProtocolService = new Predicate<SuppliedServiceDto>() {
            @Override public boolean apply(@Nullable SuppliedServiceDto input) {
                return input != null && input.getProtocol().toLowerCase().equals("ssh");
            }
        };

        // 27017 = default mongod port, 28017 = mongo admin interface
        Map<String, String> knownPortsAndProtocols = ImmutableMap.of(
                "22", "SSH",
                "80", "HTTP",
                "443", "HTTPS",
                "27017", "TCP",
                "28017", "HTTP");

        Set<SuppliedServiceDto> services = Sets.newHashSet();
        for (Object portObject : inboundPorts) {
            String port = portObject.toString();
            String protocol = "TCP";
            if (!knownPortsAndProtocols.containsKey(port)) {
                if (LOG.isDebugEnabled()) LOG.debug("Guessing {} protocol for port: {}", protocol, port);
            } else {
                protocol = knownPortsAndProtocols.get(port);
                if (LOG.isTraceEnabled()) LOG.trace("Using {} protocol for port: {}", protocol, port);
            }
            services.add(SuppliedServiceDto.builder()
                    .portRange(port)
                    .protocol(protocol)
                    .globalService(true)
                    .name(protocol + ":" + port)
                    .build());
        }

        if (!Iterables.any(services, isSshProtocolService)) {
            LOG.warn("New VM does not have an SSH service.");
        }

        return services;
    }

    private String nameFor(String type) {
        return type + Identifiers.makeRandomId(8);
    }

}
