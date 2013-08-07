package io.cloudsoft.ravello.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApplicationDto {

    @JsonProperty("applicationProperties")
    private ApplicationPropertiesDto properties;

    @JsonProperty("vms")
    private List<VmDto> virtualMachines = new ArrayList<VmDto>();

    private ApplicationDto() {
        // For Jackson
    }

    public ApplicationDto(ApplicationPropertiesDto properties, List<VmDto> vms) {
        this.properties = properties;
        this.virtualMachines = vms;
    }

    public ApplicationPropertiesDto getProperties() {
        return properties;
    }

    public List<VmDto> getVMs() {
        return virtualMachines;
    }

}