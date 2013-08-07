package io.cloudsoft.ravello.dto;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.List;

import org.testng.annotations.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import io.cloudsoft.ravello.dto.VmDto.VmPropertiesDto;

public class VmDtoTest extends MarshallingTest {

    @Test
    public void testUnmarshalVM() {
        VmDto vm = unmarshalFile("image-get.json", VmDto.class);
        assertNotNull(vm);
        assertEquals(vm.getId(), "1671271");
        assertEquals(vm.getMemorySize().getValue(), Integer.valueOf(2));
        assertEquals(vm.getHardDrives().size(), 1);
    }

    @Test
    public void testUnmarshalListOfApplicationProperties() {
        List<VmPropertiesDto> properties = unmarshalFile("images-get.json", new TypeReference<List<VmPropertiesDto>>() {});
        assertNotNull(properties);
        assertEquals(properties.size(), 3);
    }

}
