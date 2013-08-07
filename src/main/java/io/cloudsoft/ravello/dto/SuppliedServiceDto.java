package io.cloudsoft.ravello.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SuppliedServiceDto {

    public static final SuppliedServiceDto SSH_SERVICE = new SuppliedServiceDto(
            null, "ssh", null, null, "22", "SSH", true);

    @JsonProperty
    private String id;
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty("ip")
    private String listenIp;
    @JsonProperty
    private String portRange;
    @JsonProperty
    private String protocol;
    @JsonProperty
    private Boolean globalService;

    private SuppliedServiceDto() {
        // For Jackson
    }

    public SuppliedServiceDto(String id, String name, String description,
            String listenIp, String portRange, String protocol, Boolean globalService) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.listenIp = listenIp;
        this.portRange = portRange;
        this.protocol = protocol;
        this.globalService = globalService;
    }
}
