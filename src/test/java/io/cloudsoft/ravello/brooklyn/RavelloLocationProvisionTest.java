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
import brooklyn.location.basic.SshMachineLocation;
import brooklyn.management.internal.LocalManagementContext;
import brooklyn.util.collections.MutableMap;

public class RavelloLocationProvisionTest {

    private static final Logger LOG = LoggerFactory.getLogger(RavelloLocationProvisionTest.class);
    
    private LocalManagementContext managementContext;

    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        BrooklynProperties properties = BrooklynProperties.Factory.newDefault();
        managementContext = new LocalManagementContext(properties);
    }

    @AfterMethod(alwaysRun=true)
    public void tearDown() throws Exception {
        if (managementContext != null) managementContext.terminate();
    }

    @Test(groups="Live")
    public void testObtainRavelloLocation() throws Exception {
        RavelloLocation location = resolve();
        assertNotNull(location);
        RavelloSshLocation sshLocation = location.obtain();
        assertNotNull(sshLocation);
    }

    @Test(groups="Live")
    public void testObtainRavelloLocationAndRunSshCommand() throws Exception {
        RavelloLocation location = resolve();
        RavelloSshLocation sshLocation = location.obtain();
        try {
            Map details = MutableMap.of(
                    "hostname", sshLocation.getAddress().getHostAddress(),
                    "user", sshLocation.getUser());
            LOG.info("Got "+sshLocation+" at "+location+": "+details+". Now running commands over SSH.");

            // echo conflates spaces of arguments
            String result = execWithOutput(sshLocation, Arrays.asList("echo trying sshLocation", "hostname", "date"));
            assertTrue(result.contains("trying sshLocation"));

        } finally {
            location.release(sshLocation);
        }
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

    private RavelloLocation resolve() {
        return (RavelloLocation) managementContext.getLocationRegistry().resolve("ravello");
    }
}
