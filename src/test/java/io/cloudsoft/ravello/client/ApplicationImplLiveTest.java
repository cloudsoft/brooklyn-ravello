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
import io.cloudsoft.ravello.dto.ApplicationPropertiesDto;
import io.cloudsoft.ravello.dto.HardDriveDto;
import io.cloudsoft.ravello.dto.IpConfigDto;
import io.cloudsoft.ravello.dto.NetworkConnectionDto;
import io.cloudsoft.ravello.dto.NetworkDeviceDto;
import io.cloudsoft.ravello.dto.SizeDto;
import io.cloudsoft.ravello.dto.SuppliedServiceDto;
import io.cloudsoft.ravello.dto.VmDto;
import io.cloudsoft.ravello.dto.VmPropertiesDto;

public class ApplicationImplLiveTest extends LiveTest {

    // TODO: Add tests to live group
    // TODO: Confirm applications always deleted.

    final ApplicationApi appApi = ravello.getApplicationApi();

    public String nameFor(String type) {
        return "test-" + type + "-" + UUID.randomUUID();
    }

    /** Makes an ApplicationDto with one VM. */
    private ApplicationDto makeTestApplication() {
        VmPropertiesDto vmProperties = new VmPropertiesDto(
                null,
                nameFor("vm"),
                "Test VM",
                1,
                SizeDto.gigabytes(1));
        VmDto appVm = new VmDto(
                vmProperties,
                ImmutableList.of(new HardDriveDto(
                        null,
                        nameFor("hardDrive"),
                        true,
                        SizeDto.gigabytes(20))),
                ImmutableSet.<SuppliedServiceDto>of(),
                ImmutableList.of(new NetworkConnectionDto(
                        null,
                        nameFor("networkConnection"),
                        true,
                        new NetworkDeviceDto("virtio", 0, 0),
                        new IpConfigDto())));
        ApplicationPropertiesDto appProperties = new ApplicationPropertiesDto(
                null,
                nameFor("app"),
                "Test application",
                null);
        return new ApplicationDto(
                appProperties,
                ImmutableList.of(appVm));
    }

    @Test
    public void testGetAllApplications() {
        List<ApplicationPropertiesDto> applications = appApi.get();
        assertNotNull(applications);
    }

    @Test
    public void testCreateAndDeleteApplication() {
        ApplicationDto created = null;
        try {
            ApplicationDto toCreate = makeTestApplication();
            created = appApi.create(toCreate);

            assertNotNull(created, "Created application should not be null");
            assertNotNull(created.getProperties().getId());
            assertNotNull(created.getVMs());
            assertEquals(1, created.getVMs().size());
            assertEquals(toCreate.getVMs().get(0).getProperties().getName(),
                          created.getVMs().get(0).getProperties().getName());
        } finally {
            if (created != null) {
                appApi.delete(created.getProperties().getId());
                assertNull(appApi.get(created.getProperties().getId()));
            }
        }
    }

    @Test
    public void testPublishingApplication() {
        ApplicationDto created = null;
        try {
            ApplicationDto toCreate = makeTestApplication();
            created = appApi.create(toCreate);

            assertNotNull(created, "Created application should not be null");
            assertFalse(created.getProperties().isPublished(), "Newly created application should not be published");

            String id = created.getProperties().getId();
            appApi.publish(id, "AMAZON", "Virginia");
            assertTrue(appApi.get(id).getProperties().isPublished(), "Application should be published");

        } finally {
            if (created != null) {
//                appApi.delete(created.getProperties().getId());
            }
        }
    }

}
