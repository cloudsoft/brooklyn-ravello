package io.cloudsoft.ravello.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DhcpDto {

    @JsonProperty private Long id;
    @JsonProperty private String ip;
    @JsonProperty private String ipRangeBegin;
    @JsonProperty private String ipRangeEnd;
    @JsonProperty private Boolean active;

    private DhcpDto() {
        // For Jackson
    }

    private DhcpDto(Long id, String ip, String ipRangeBegin, String ipRangeEnd, Boolean active) {
        this.id = id;
        this.ip = ip;
        this.ipRangeBegin = ipRangeBegin;
        this.ipRangeEnd = ipRangeEnd;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public String getIpRangeBegin() {
        return ipRangeBegin;
    }

    public String getIpRangeEnd() {
        return ipRangeEnd;
    }

    public Boolean getActive() {
        return active;
    }
}
