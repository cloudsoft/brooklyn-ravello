package io.cloudsoft.ravello.dto;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.Test;

public class ApplicationDtoTest extends MarshallingTest {

    @Test
    public void testUnmarshalListOfApplicationProperties() {
        ApplicationDto application = unmarshalFile("application-get.json", ApplicationDto.class);
        assertEquals("nginx-tomcat7-mysql-i", application.getProperties().getName());
        assertEquals(3, application.getVMs().size());
    }

}
