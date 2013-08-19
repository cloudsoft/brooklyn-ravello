package io.cloudsoft.ravello.dto;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.Collections;
import java.util.List;

import org.testng.annotations.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import io.cloudsoft.ravello.dto.ApplicationDto.ApplicationPropertiesDto;

public class ApplicationDtoTest extends MarshallingTest {

    @Test
    public void testUnmarshalApplication() {
        ApplicationDto application = unmarshalFile("application-get.json", ApplicationDto.class);
        assertEquals("nginx-tomcat7-mysql-i", application.getName());
        assertEquals(3, application.getVMs().size());
    }

    @Test
    public void testUnmarshalListOfApplicationProperties() {
        List<ApplicationPropertiesDto> properties = unmarshalFile("applications-get.json",
                new TypeReference<List<ApplicationPropertiesDto>>() {});
        assertEquals(1, properties.size());
        assertEquals("nginx-tomcat7-mysql-i", properties.get(0).getName());
        assertNotNull(properties.get(0).getDescription());
    }

    @Test
    public void testVersionDefaultsToZero() {
        ApplicationDto app = ApplicationDto.builder().name("xyz").build();
        assertEquals(app.getName(), "xyz");
        assertNull(app.getDescription());
        assertNull(app.getId());
        assertEquals(app.getVMs(), Collections.emptyList());
        assertEquals(app.getVersion(), 0);
    }

}
