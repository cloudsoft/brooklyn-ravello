package io.cloudsoft.ravello.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NetworkDeviceDto {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Boolean useAutomaticMac;
        private String macAdress;
        private String deviceType;
        private Integer pciSlot;
        private Integer index;

        public Builder macAdress(String macAdress) {
            this.macAdress = macAdress;
            this.useAutomaticMac = false;
            return this;
        }
        public Builder deviceType(String deviceType) {
            this.deviceType = deviceType;
            return this;
        }
        public Builder index(Integer index) {
            this.index = index;
            return this;
        }
        public Builder pciSlot(Integer pciSlot) {
            this.pciSlot = pciSlot;
            return this;
        }
        public Builder useAutomaticMac() {
            this.macAdress = null;
            this.useAutomaticMac = true;
            return this;
        }

        public NetworkDeviceDto build() {
            return new NetworkDeviceDto(useAutomaticMac, macAdress, deviceType, index, pciSlot);
        }
    }

    @JsonProperty("mac") private String macAddress;
    @JsonProperty private String deviceType;
    @JsonProperty private Integer index;
    @JsonProperty private Integer pciSlot;
    @JsonProperty private Boolean useAutomaticMac;

    private NetworkDeviceDto() {
        // For Jackson
    }

    /** Uses the given mac addresses. */
    protected NetworkDeviceDto(Boolean useAutomaticMac, String macAddress, String deviceType, Integer index, Integer pciSlot) {
        this.useAutomaticMac = useAutomaticMac;
        this.macAddress = macAddress;
        this.deviceType = deviceType;
        this.index = index;
        this.pciSlot = pciSlot;
    }

}
