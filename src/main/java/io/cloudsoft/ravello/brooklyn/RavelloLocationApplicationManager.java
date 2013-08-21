package io.cloudsoft.ravello.brooklyn;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
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

    final ScheduledExecutorService updateManager = Executors.newScheduledThreadPool(4);

    private final RavelloApi ravello;
    private final String privateKeyId;
    private final long publicationPause;
    private final TimeUnit publicationPauseTimeUnit;

    // Accesses to these fields should be synchronised
    /**
     * Holds the current representation of the application. This should mirror the object returned
     * by a GET to /..ravello../applications/:id, but might NOT represent the true published application.
     */
    private ApplicationDto applicationModel;
    private final List<CountDownLatch> latchesAwaitingAPublication = Lists.newLinkedList();
    private int publishCount = 0;

    /** Creates an application manager that waits for twenty seconds before publishing updates */
    RavelloLocationApplicationManager(RavelloApi ravelloApi, String privateKeyId) {
        this(ravelloApi, privateKeyId, 20);
    }

    /** Creates an application manager that waits publicationPause seconds before publishing updates */
    RavelloLocationApplicationManager(RavelloApi ravelloApi, String privateKeyId, long publicationPause) {
        this(ravelloApi, privateKeyId, publicationPause, TimeUnit.SECONDS);
    }

    RavelloLocationApplicationManager(RavelloApi ravelloApi, String privateKeyId, long publicationPause, TimeUnit timeUnit) {
        this.ravello = ravelloApi;
        this.privateKeyId = privateKeyId;
        this.publicationPause = publicationPause;
        this.publicationPauseTimeUnit = timeUnit;
    }

    public VmDto createNewPublishedVM(Collection<?> inboundPorts) {
        checkNotNull(inboundPorts, "inboundPorts");

        final VmDto newMachine;
        final ApplicationDto forUpdate;
        CountDownLatch publishedLatch;

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
            publishedLatch = submitPublishTask();
        }

        try {
            publishedLatch.await();
        } catch (InterruptedException e) {
            LOG.warn("Wait for publishedLatch interrupted. Can't guarantee VM has started: " + newMachine, e);
        }

        // Fetch the latest model of the app and get the VM with name matching the one created
        ApplicationDto localApplicationModel = ravello.getApplicationApi().get(forUpdate.getId());
        VmDto created = Iterables.find(localApplicationModel.getVMs(), new Predicate<VmDto>() {
            @Override
            public boolean apply(VmDto input) {
                return input.getName().equals(newMachine.getName());
            }
        });

        return created;
    }

    private synchronized CountDownLatch submitPublishTask() {
        final Integer countAtSubmit = ++publishCount;
        CountDownLatch publishedLatch = new CountDownLatch(1);
        latchesAwaitingAPublication.add(publishedLatch);

        Runnable publisher = new Runnable() {
            @Override public void run() {
                // Check for existence of other updates: taskCount == countAtSubmit
                // if no more tasks submitted then try to publish, otherwise let a future version of
                // this task publish.
                // if publish fails then retry in thirty seconds - publish probably already running.
                synchronized (RavelloLocationApplicationManager.this) {
                    // Chance application was deleted before the task ran
                    if (applicationModel == null) {
                        LOG.info("Publish task running but applicationModel null. The application was probably deleted.");
                        return;
                    }
                    String appId = applicationModel.getId();

                    // If the app has not been published before, publish the app to chosen cloud.
                    // Otherwise, publish updates.
                    if (countAtSubmit == publishCount) {
                        if (!applicationModel.isPublished()) {
                            LOG.info("Publishing app[{}]", appId);
                            ravello.getApplicationApi().publish(appId, "AMAZON", "Virginia");
                        } else {
                            LOG.info("Publishing updates for app[{}]", appId);
                            ravello.getApplicationApi().publishUpdates(appId);
                        }

                        // TODO: Assuming success
                        for (CountDownLatch latch : latchesAwaitingAPublication) {
                            latch.countDown();
                        }
                        latchesAwaitingAPublication.clear();

                        // Update application model
                        applicationModel = ravello.getApplicationApi().get(appId);
                    } else {
                        LOG.debug("Postponing publication of app[{}]: count at submit was {}, count is now {}",
                                appId, countAtSubmit, publishCount);
                    }
                }
            }
        };

        LOG.info("Scheduling publication of app[{}] for {} {} time", applicationModel.getId(), publicationPause, publicationPauseTimeUnit.name().toLowerCase());
        updateManager.schedule(publisher, publicationPause, publicationPauseTimeUnit);
        return publishedLatch;
    }

    public void release(VmDto vm) {
        LOG.info("Removing VM: " + vm);
        CountDownLatch publishedLatch;
        synchronized (this) {
            String appId = applicationModel.getId();
            ApplicationDto forUpdate = ApplicationDto.builder()
                    .fromApplicationDto(applicationModel)
                    .removeVm(vm.getId())
                    .build();
            // Kill application entirely if no VMs remain.
            if (forUpdate.getVMs().isEmpty()) {
                LOG.info("No VMs remaining in app[{}]. Going to delete.", appId);
                deleteApplicationModel();
                return;
            } else {
                ravello.getApplicationApi().update(appId, forUpdate);
                publishedLatch = submitPublishTask();
                applicationModel = ravello.getApplicationApi().get(appId);
                LOG.trace("Removed {} from app[{}]. New model: {}", vm, appId, applicationModel);
            }
        }
        try {
            publishedLatch.await();
        } catch (InterruptedException e) {
            LOG.warn("Wait for publishedLatch interrupted. Can't guarantee VM has been released: "+vm, e);
        }
    }

    public synchronized void deleteApplicationModel() {
        if (applicationModel != null) {
            // TODO: updateManager/latchesAwaitingAPublication?
            LOG.info("Deleting application model: " + applicationModel.getId());
            ravello.getApplicationApi().delete(applicationModel.getId());
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

    private VmDto makeVmDto(Collection<?> inboundPorts) {
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
        return type + "-" + Identifiers.makeRandomId(8);
    }

}
