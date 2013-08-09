package io.cloudsoft.ravello.client;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import io.cloudsoft.ravello.api.ApplicationApi;
import io.cloudsoft.ravello.dto.ApplicationDto;
import io.cloudsoft.ravello.dto.ApplicationDto.ApplicationPropertiesDto;
import io.cloudsoft.ravello.dto.HardDriveDto;
import io.cloudsoft.ravello.dto.IpConfigDto;
import io.cloudsoft.ravello.dto.NetworkConnectionDto;
import io.cloudsoft.ravello.dto.NetworkDeviceDto;
import io.cloudsoft.ravello.dto.SizeDto;
import io.cloudsoft.ravello.dto.SuppliedServiceDto;
import io.cloudsoft.ravello.dto.VmDto;

@Test(groups = "live")
public class ApplicationImplLiveTest extends LiveTest {

    // TODO: Add tests to live group
    // TODO: Confirm applications always deleted.

    final ApplicationApi appApi = ravello.getApplicationApi();

    public String nameFor(String type) {
        return "test-" + type + "-" + UUID.randomUUID();
    }

    /** Makes an ApplicationDto with one VM. */
    private ApplicationDto makeTestApplication() {
        VmDto appVm = VmDto.builder()
                .name(nameFor("vm"))
                .description("Test VM")
                .numCpus(1)
                .memorySize(SizeDto.gigabytes(1))
                .hardDrives(HardDriveDto.builder()
                        .name(nameFor("hard-drive"))
                        .size(SizeDto.gigabytes(20))
                        .controller("ide")
                        .boot(true)
                        .index(0)
                        .controllerPciSlot(0)
                        .controllerIndex(0)
                        .peripheral(true)
                        .build())
//                .networkConnections(NetworkConnectionDto.builder()
//                        .name(nameFor("networkConnection"))
//                        .accessPort(true)
//                        .device(NetworkDeviceDto.builder().useAutomaticMac().deviceType("virtio").index(0).build())
//                        .ipConfig(new IpConfigDto())
//                        .build())
//                .suppliedServices(SuppliedServiceDto.SSH_SERVICE)
                .build();
        return ApplicationDto.builder()
                .name(nameFor("app"))
                .description("Test application")
                .vms(appVm)
                .build();
    }

    @Test(groups = "live")
    public void testGetAllApplications() {
        List<ApplicationPropertiesDto> applications = appApi.get();
        assertNotNull(applications);
    }

    @Test(groups = "live")
    public void testCreateAndDeleteApplication() {
        ApplicationDto created = null;
        try {
            ApplicationDto toCreate = makeTestApplication();
            created = appApi.create(toCreate);

            assertNotNull(created, "Created application should not be null");
            assertNotNull(created.getId());
            assertNotNull(created.getVMs());
            assertEquals(1, created.getVMs().size());
            assertEquals(toCreate.getVMs().get(0).getName(),
                    created.getVMs().get(0).getName());
        } finally {
            if (created != null) {
                appApi.delete(created.getId());
                assertNull(appApi.get(created.getId()));
            }
        }
    }

    @Test(groups = "live")
    public void testPublishingApplication() {
        ApplicationDto created = null;
        try {
            ApplicationDto toCreate = makeTestApplication();
            created = appApi.create(toCreate);

            assertNotNull(created, "Created application should not be null");
            assertFalse(created.isPublished(), "Newly created application should not be published");

            String id = created.getId();
            appApi.publish(id, "AMAZON", "Virginia");
            assertTrue(appApi.get(id).isPublished(), "Application should be published");

        } finally {
            if (created != null) {
                appApi.delete(created.getId());
            }
        }
    }

}
