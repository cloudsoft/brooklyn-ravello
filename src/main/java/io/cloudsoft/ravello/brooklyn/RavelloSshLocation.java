package io.cloudsoft.ravello.brooklyn;

import brooklyn.location.basic.SshMachineLocation;
import io.cloudsoft.ravello.dto.VmDto;

public class RavelloSshLocation extends SshMachineLocation {

    private final VmDto vm;

    public RavelloSshLocation(VmDto ownedVM) {
        super();
        this.vm = ownedVM;
        setConfig(SSH_HOST, vm.getRuntimeInformation().getExternalFqdn());
        // TODO: Get port from suppliedServices ssh service
        setConfig(SSH_PORT, 22);
    }
}
