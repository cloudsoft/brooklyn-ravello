package io.cloudsoft.ravello.brooklyn;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Closeables;

import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.location.LocationSpec;
import brooklyn.location.NoMachinesAvailableException;
import brooklyn.location.basic.SshMachineLocation;
import brooklyn.location.cloud.AbstractCloudMachineProvisioningLocation;
import brooklyn.util.collections.MutableMap;
import brooklyn.util.flags.SetFromFlag;
import brooklyn.util.internal.Repeater;
import brooklyn.util.time.Time;
import io.cloudsoft.ravello.api.RavelloApi;
import io.cloudsoft.ravello.client.RavelloApiImpl;
import io.cloudsoft.ravello.dto.Cloud;
import io.cloudsoft.ravello.dto.VmDto;

public class RavelloLocation extends AbstractCloudMachineProvisioningLocation {

    private static final Logger LOG = LoggerFactory.getLogger(RavelloLocation.class);

    private RavelloLocationApplicationManager applicationManager;
    private final String sshUsername = "ravello";
    @SetFromFlag private String privateKeyFile;

    public static final ConfigKey<String> USERNAME = ConfigKeys.newStringConfigKey("username");
    public static final ConfigKey<String> PASSWORD = ConfigKeys.newStringConfigKey("password");
    public static final ConfigKey<String> PRIVATE_KEY_ID = ConfigKeys.newStringConfigKey("privateKeyId");
    public static final ConfigKey<String> PREFERRED_CLOUD = ConfigKeys.newStringConfigKey("preferredCloud");
    public static final ConfigKey<String> PREFERRED_REGION = ConfigKeys.newStringConfigKey("preferredRegion");

    /**
     * TODO:
     *  - handle exceptions properly
     */

    public RavelloLocation() {
    }

    @Override
    public void configure(Map properties) {
        super.configure(properties);
        String apiEndpoint = "https://cloud.ravellosystems.com/services";
        String username = checkNotNull(getConfig(USERNAME), "username");
        String password = checkNotNull(getConfig(PASSWORD), "password");
        String privateKeyId = checkNotNull(getConfig(PRIVATE_KEY_ID), "privateKeyId");

        RavelloApi ravello = new RavelloApiImpl(apiEndpoint, username, password);

        String preferredCloud = getConfig(PREFERRED_CLOUD);
        String preferredRegion = getConfig(PREFERRED_REGION);

        // TODO: Could default to first available region if preferredCloud not null but no region given
        if (preferredCloud == null || preferredRegion == null) {
            LOG.info("Preferred cloud/region not both specified. Defaulting to {}:{}", Cloud.AMAZON.name(), "Virginia");
            preferredCloud = Cloud.AMAZON.name();
            preferredRegion = "Virginia";
        } else {
            LOG.debug("Preferred cloud/region: {}:{}", preferredCloud, preferredRegion);
        }

        applicationManager = new RavelloLocationApplicationManager(ravello, privateKeyId, preferredCloud, preferredRegion);
    }

    public RavelloSshMachineLocation obtain() throws NoMachinesAvailableException {
        return obtain(MutableMap.of("inboundPorts", ImmutableList.of(22)));
    }

    @Override
    public RavelloSshMachineLocation obtain(Map<?, ?> flags) throws NoMachinesAvailableException {
        checkNotNull(applicationManager, "Ravello app manager has not been configured");

        Collection<?> inboundPorts = Collections.emptyList();
        if (flags.containsKey("inboundPorts") && flags.get("inboundPorts") instanceof Collection) {
            inboundPorts = Collection.class.cast(flags.get("inboundPorts"));
        }

        // Add a new VM with an SSH service
        VmDto created = applicationManager.createNewPublishedVM(inboundPorts);
        if (created.getRuntimeInformation() == null || created.getRuntimeInformation().getExternalFullyQualifiedDomainName() == null) {
            throw new NoMachinesAvailableException("A VM was created but it does not appear to have been published. " +
                    "It has no external fully qualified domain name: " + created);
        }

        LOG.info("Created new VM in {}: {}", this, created);
        waitForReachable(created);

        String hostname = created.getRuntimeInformation().getExternalFullyQualifiedDomainName();
        return getManagementContext().getLocationManager().createLocation(LocationSpec.create(RavelloSshMachineLocation.class)
                .configure("address", hostname)
                .configure("ravelloParent", this)
                .configure("displayName", hostname)
                .configure("user", sshUsername)
                .configure("privateKeyFile", privateKeyFile)
                .configure("vm", created));
    }

    /**
     * Repeatedly attempts to execute a command over ssh. Returns when the command completes, throws
     * IllegalStateException if the command was not successfully run in twenty minutes.
     */
    protected void waitForReachable(VmDto vm) {

        final String hostname = vm.getRuntimeInformation().getExternalFullyQualifiedDomainName();
        // TODO Expose as a config key
        int sshTimeout = 20 * 60 * 1000;
        LOG.info("Started vm[{}]. Waiting up to {} for it to be sshable on {}@{}",
                        vm.getId(), Time.makeTimeStringRounded(sshTimeout), sshUsername, hostname);

        boolean reachable = new Repeater()
                .repeat()
                .every(10, TimeUnit.SECONDS)
                .until(new Callable<Boolean>() {
                    public Boolean call() {
                        SshMachineLocation sshLoc = null;
                        try {
                            InetAddress inet;
                            try {
                                inet = InetAddress.getByName(hostname);
                            } catch (Exception e) {
                                LOG.debug("Hostname "+hostname+" not ready yet; will keep trying ("+e+")");
                                return false;
                            }
                            MutableMap<Object, Object> cfg = MutableMap.builder()
                                    .put("address", inet)
                                    .put("user", sshUsername)
                                    .put("privateKeyFile", privateKeyFile)
                                    .build();
                            if (getManagementContext()!=null) {
                                sshLoc = getManagementContext().getLocationManager().createLocation(cfg, SshMachineLocation.class);
                            } else {
                                // fall back to legacy way (will log warning)
                                sshLoc = new SshMachineLocation(cfg);
                            }
                            int exitStatus = sshLoc.execScript(MutableMap.<String,Object>of(), "reachable", ImmutableList.of("true"));
                            return exitStatus == 0;
                        } finally {
                            Closeables.closeQuietly(sshLoc);
                        }
                    }
                })
                .limitTimeTo(sshTimeout, TimeUnit.MILLISECONDS)
                .run();

        if (!reachable) {
            throw new IllegalStateException("SSH failed for "+
                    sshUsername+"@"+hostname+" after waiting "+Time.makeTimeStringRounded(sshTimeout));
        }
    }

    @Override
    public void release(SshMachineLocation machine) {
        checkNotNull(applicationManager, "Ravello app manager has not been configured");
        checkState(machine instanceof RavelloSshMachineLocation,
                "release() given instance of " + machine.getClass().getName() + ", expected instance of " + RavelloSshMachineLocation.class.getName());

        RavelloSshMachineLocation ravelloMachine = RavelloSshMachineLocation.class.cast(machine);
        applicationManager.release(ravelloMachine.getVm());
    }

    public void deleteApplicationModel() {
        checkNotNull(applicationManager, "Ravello app manager has not been configured");
        applicationManager.deleteApplicationModel();
    }

    public Map<String, Object> getProvisioningFlags(Collection<String> tags) {
        // TODO if we need provisioning flags...
        LOG.debug("Provisioning flags not supported for "+this+" ("+tags+")");
        return MutableMap.<String, Object>of();
    }
    
}
