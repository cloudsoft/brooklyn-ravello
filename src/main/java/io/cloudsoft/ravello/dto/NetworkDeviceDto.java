package io.cloudsoft.ravello.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NetworkDeviceDto {

    @JsonProperty("mac")
    private String macAddress;

    @JsonProperty
    private String deviceType;

    @JsonProperty
    private Integer index;

    @JsonProperty
    private Integer pciSlot;

    @JsonProperty
    private Boolean useAutomaticMac;

    private NetworkDeviceDto() {
        // For Jackson
    }

    /** Uses an automatic mac address */
    public NetworkDeviceDto(String deviceType, Integer index, Integer pciSlot) {
        this.deviceType = deviceType;
        this.index = index;
        this.pciSlot = pciSlot;
        this.useAutomaticMac = true;
        this.macAddress = null;
    }

    /** Uses the given mac addresses. */
    public NetworkDeviceDto(String macAddress, String deviceType, Integer index, Integer pciSlot) {
        this.macAddress = macAddress;
        this.deviceType = deviceType;
        this.index = index;
        this.pciSlot = pciSlot;
        this.useAutomaticMac = false;
    }

}
