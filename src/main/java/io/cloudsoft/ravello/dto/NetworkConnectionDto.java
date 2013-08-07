package io.cloudsoft.ravello.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NetworkConnectionDto {

    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    Boolean accessPort;

    @JsonProperty
    private NetworkDeviceDto device;

    @JsonProperty
    private IpConfigDto ipConfig;

    @JsonProperty
    private String vlanTag = "";

    private NetworkConnectionDto() {
        // For Jackson
    }

    public NetworkConnectionDto(String id, String name, Boolean accessPort, NetworkDeviceDto device,
            IpConfigDto ipConfig) {
        this.id = id;
        this.name = name;
        this.accessPort = accessPort;
        this.device = device;
        this.ipConfig = ipConfig;
    }
}
