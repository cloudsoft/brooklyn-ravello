package io.cloudsoft.ravello.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HardDriveDto {

    @JsonProperty
    String id;

    @JsonProperty
    String name;

    @JsonProperty
    Boolean boot;

    @JsonProperty
    SizeDto size;

    @JsonProperty
    Integer index = 0;

    @JsonProperty("controller")
    String controller = "ide";

    @JsonProperty
    Integer controllerIndex = 0;

    @JsonProperty
    Integer controllerPciSlot = 0;

    @JsonProperty
    Boolean peripheral = true;

    private HardDriveDto() {
        // For Jackson
    }

    public HardDriveDto(String id, String name, Boolean bootable, SizeDto size) {
        this.id = id;
        this.name = name;
        this.boot = bootable;
        this.size = size;
    }
}
