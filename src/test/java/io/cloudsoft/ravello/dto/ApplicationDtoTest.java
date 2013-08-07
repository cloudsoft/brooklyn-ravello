package io.cloudsoft.ravello.dto;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

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

}
