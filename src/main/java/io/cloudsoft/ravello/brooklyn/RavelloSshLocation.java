package io.cloudsoft.ravello.brooklyn;

import brooklyn.location.basic.SshMachineLocation;
import brooklyn.util.flags.SetFromFlag;
import io.cloudsoft.ravello.dto.VmDto;

public class RavelloSshLocation extends SshMachineLocation {

    @SetFromFlag
    RavelloLocation parent;

    @SetFromFlag
    VmDto vm;

    public RavelloSshLocation() {
    }

    public VmDto getVm() {
        return vm;
    }
}
