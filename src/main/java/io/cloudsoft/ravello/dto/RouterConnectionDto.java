package io.cloudsoft.ravello.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RouterConnectionDto {

    @JsonProperty private Long id;
    @JsonProperty private Long routerRef;
    @JsonProperty private Long subnetRef;
    @JsonProperty private String ip;

    private RouterConnectionDto() {
        // For Jackson
    }

    private RouterConnectionDto(Long id, Long routerRef, Long subnetRef, String ip) {
        this.id = id;
        this.routerRef = routerRef;
        this.subnetRef = subnetRef;
        this.ip = ip;
    }

    public Long getId() {
        return id;
    }

    public Long getRouterRef() {
        return routerRef;
    }

    public Long getSubnetRef() {
        return subnetRef;
    }

    public String getIp() {
        return ip;
    }
}
