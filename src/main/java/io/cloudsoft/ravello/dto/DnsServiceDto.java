package io.cloudsoft.ravello.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DnsServiceDto {

    @JsonProperty private Long id;

    // In API doc but not given by implementation.
    //@JsonProperty private List<HostDto> hosts;

    private DnsServiceDto() {
        // For Jackson
    }

    private DnsServiceDto(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
