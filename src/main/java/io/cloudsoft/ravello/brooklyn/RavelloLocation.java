package io.cloudsoft.ravello.brooklyn;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;

import brooklyn.location.LocationSpec;
import brooklyn.location.NoMachinesAvailableException;
import brooklyn.location.basic.SshMachineLocation;
import brooklyn.location.cloud.AbstractCloudMachineProvisioningLocation;
import brooklyn.util.collections.MutableMap;
import brooklyn.util.flags.SetFromFlag;
import io.cloudsoft.ravello.api.RavelloApi;
import io.cloudsoft.ravello.client.RavelloApiImpl;
import io.cloudsoft.ravello.client.RavelloApiLocalImpl;
import io.cloudsoft.ravello.dto.ApplicationDto;
import io.cloudsoft.ravello.dto.VmDto;

public class RavelloLocation extends AbstractCloudMachineProvisioningLocation {

    private static final Logger LOG = LoggerFactory.getLogger(RavelloLocation.class);

    private RavelloLocationApplicationManager applicationManager;
    private final String sshUsername = "ravello";
    @SetFromFlag private String privateKeyFile;

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
        String username = (String) checkNotNull(properties.get("username"), "username");
        String password = (String) checkNotNull(properties.get("password"), "password");
        String privateKeyId = (String) checkNotNull(properties.get("privateKeyId"), "privateKeyId");

        RavelloApi ravello = new RavelloApiImpl(apiEndpoint, username, password);
//        ravello = new RavelloApiLocalImpl();
        applicationManager = new RavelloLocationApplicationManager(ravello, privateKeyId);
    }

    public RavelloSshLocation obtain() throws NoMachinesAvailableException {
        return obtain(MutableMap.of("inboundPorts", ImmutableList.of(22)));
    }

    @Override
    public RavelloSshLocation obtain(Map<?, ?> flags) throws NoMachinesAvailableException {
        checkNotNull(applicationManager, "Ravello app manager has not been configured");

        Collection<?> inboundPorts = Collections.emptyList();
        if (flags.containsKey("inboundPorts") && flags.get("inboundPorts") instanceof Collection) {
            inboundPorts = Collection.class.cast(flags.get("inboundPorts"));
        }

        // Add a new VM with an SSH service
        VmDto created = applicationManager.createNewPublishedVM(inboundPorts);
        LOG.info("Created new VM: " + created);

        // TODO: Wait for SSHable.
        //waitForReachable(created);
        LOG.info("SLEEPING FOR TEN MINUTES");
        try { Thread.sleep(1000*60*10); } catch (InterruptedException e) {}

        String hostname = created.getRuntimeInformation().getExternalFullyQualifiedDomainName();
        return getManagementContext().getLocationManager().createLocation(LocationSpec.create(RavelloSshLocation.class)
                .configure("address", hostname)
                .configure("ravelloParent", this)
                .configure("displayName", hostname)
                .configure("user", sshUsername)
                .configure("privateKeyFile", privateKeyFile)
                .configure("vm", created));
    }

//    protected void waitForReachable(VmDto vm) {
//        String hostname = vm.getRuntimeInformation().getExternalFullyQualifiedDomainName();
//        int sshTimeout = 10 * 60 * 1000;
//        LOG.info("Started VM. Waiting {} for it to be sshable on {}@{}",
//                        Time.makeTimeStringRounded(sshTimeout), sshUsername, hostname);
//
//        boolean reachable = new Repeater()
//            .repeat()
//            .every(1, SECONDS)
//            .until(new Callable<Boolean>() {
//                public Boolean call() {
//                    Statement statement = Statements.newStatementList(exec("hostname"));
                    // NB this assumes passwordless sudo !
//                    ExecResponse response = computeService.runScriptOnNode(nodeRef.getId(), statement,
//                            overrideLoginCredentials(expectedCredentialsRef).runAsRoot(false));
//                    return response.getExitStatus() == 0;
//                }})
//            .limitTimeTo(sshTimeout, MILLISECONDS)
//            .run();
//
//        if (!reachable) {
//            throw new IllegalStateException("SSH failed for "+
//                    sshUsername+"@"+hostname+" after waiting "+Time.makeTimeStringRounded(sshTimeout));
//        }
//    }

    @Override
    public void release(SshMachineLocation machine) {
        checkNotNull(applicationManager, "Ravello app manager has not been configured");
        checkState(machine instanceof RavelloSshLocation,
                "release() given instance of " + machine.getClass().getName() + ", expected instance of " + RavelloSshLocation.class.getName());

        RavelloSshLocation ravelloMachine = RavelloSshLocation.class.cast(machine);
        applicationManager.release(ravelloMachine.getVm());
    }

    public void deleteApplicationModel() {
        checkNotNull(applicationManager, "Ravello app manager has not been configured");
        applicationManager.deleteApplicationModel();
    }

}
