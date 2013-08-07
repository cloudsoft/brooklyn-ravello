package io.cloudsoft.ravello.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VmDto {

    @JsonProperty("vmProperties")
    private VmPropertiesDto vmProperties;

    @JsonProperty("hardDrives")
    private List<HardDriveDto> hardDrives;

    public VmDto() {
        // For Jackson
    }

    public VmDto(VmPropertiesDto properties, List<HardDriveDto> hardDrives) {
        this.vmProperties = properties;
        this.hardDrives = hardDrives;
    }

    public VmPropertiesDto getProperties() {
        return vmProperties;
    }

    public List<HardDriveDto> getHardDrives() {
        return hardDrives;
    }

}