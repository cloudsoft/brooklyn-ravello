package io.cloudsoft.ravello.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HardDriveDto {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private Boolean boot;
        private SizeDto size;
        private Integer index;
        private String controller;
        private Integer controllerIndex;
        private Integer controllerPciSlot;
        private Boolean peripheral;

        public Builder id(String id) {
            this.id = id;
            return this;
        }
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        public Builder boot(Boolean boot) {
            this.boot = boot;
            return this;
        }
        public Builder size(SizeDto size) {
            this.size = size;
            return this;
        }
        public Builder index(Integer index) {
            this.index = index;
            return this;
        }
        public Builder controller(String controller) {
            this.controller = controller;
            return this;
        }
        public Builder controllerIndex(Integer controllerIndex) {
            this.controllerIndex = controllerIndex;
            return this;
        }
        public Builder controllerPciSlot(Integer controllerPciSlot) {
            this.controllerPciSlot = controllerPciSlot;
            return this;
        }
        public Builder peripheral(Boolean peripheral) {
            this.peripheral = peripheral;
            return this;
        }

        public HardDriveDto build() {
            return new HardDriveDto(id, name, boot, size, index, controller, controllerIndex, controllerPciSlot, peripheral);
        }
    }

    @JsonProperty private String id;
    @JsonProperty private String name;
    @JsonProperty private Boolean boot;
    @JsonProperty private SizeDto size;
    /** Index of the hard drive in the list of a VM's hard drives */
    @JsonProperty private Integer index;
    @JsonProperty private String controller;
    @JsonProperty private Integer controllerIndex;
    @JsonProperty private Integer controllerPciSlot;
    @JsonProperty private Boolean peripheral;

    private HardDriveDto() {
        // For Jackson
    }

    protected HardDriveDto(String id, String name, Boolean boot, SizeDto size, Integer index, String controller,
            Integer controllerIndex, Integer controllerPciSlot, Boolean peripheral) {
        this.id = id;
        this.name = name;
        this.boot = boot;
        this.size = size;
        this.index = index;
        this.controller = controller;
        this.controllerIndex = controllerIndex;
        this.controllerPciSlot = controllerPciSlot;
        this.peripheral = peripheral;
    }
}
