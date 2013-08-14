package io.cloudsoft.ravello.brooklyn;

import brooklyn.location.OsDetails;
import brooklyn.location.basic.BasicOsDetails;
import brooklyn.location.basic.BasicOsDetails.Factory;
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

    @Override
    public OsDetails getOsDetails() {
        // TODO!
        return Factory.ANONYMOUS_LINUX_64;
    }
}
