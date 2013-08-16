package io.cloudsoft.ravello.brooklyn;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Maps;

import brooklyn.config.BrooklynProperties;
import brooklyn.location.LocationSpec;
import brooklyn.location.basic.SshMachineLocation;
import brooklyn.management.internal.LocalManagementContext;
import brooklyn.util.collections.MutableMap;

public class RavelloLocationProvisionTest {

    private static final Logger LOG = LoggerFactory.getLogger(RavelloLocationProvisionTest.class);
    
    private LocalManagementContext managementContext;
    private RavelloLocation location;

    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        BrooklynProperties properties = BrooklynProperties.Factory.newDefault();
        managementContext = new LocalManagementContext(properties);
        location = (RavelloLocation) managementContext.getLocationRegistry().resolve("ravello");
    }

    @AfterMethod(alwaysRun=true)
    public void tearDown() throws Exception {
        // Believe it's enough to simply delete the entire application.
        // There's no need to release ssh locations separately.
        if (location != null) location.deleteApplicationModel();
        if (managementContext != null) managementContext.terminate();
    }

    @Test(groups="live")
    public void testObtainRavelloLocation() throws Exception {
        assertNotNull(location);
        RavelloSshLocation sshLocation = location.obtain();
        assertNotNull(sshLocation);
    }

    @Test(groups="live")
    public void testObtainRavelloLocationAndRunSshCommand() throws Exception {
        RavelloSshLocation sshLocation = location.obtain();
        Map details = MutableMap.of(
                "hostname", sshLocation.getAddress().getHostAddress(),
                "user", sshLocation.getUser());
        LOG.info("Got "+sshLocation+" at "+location+": "+details+". Now running commands over SSH.");

        String result = execWithOutput(sshLocation, Arrays.asList("echo trying sshLocation", "hostname", "date"));
        assertTrue(result.contains("trying sshLocation"));
    }

    private String execWithOutput(SshMachineLocation m, List<String> commands) {
        Map<String, ByteArrayOutputStream> flags = Maps.newLinkedHashMap();
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        flags.put("out", stdout);
        flags.put("err", stderr);
        m.execCommands(flags, "test", commands);
        LOG.info("Output from "+commands+":\n"+new String(stdout.toByteArray()));
        return new String(stdout.toByteArray());
    }

}
