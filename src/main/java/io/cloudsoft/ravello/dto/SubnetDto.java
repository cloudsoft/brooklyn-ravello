package io.cloudsoft.ravello.dto;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class SubnetDto {

    @JsonProperty private Long id;
    @JsonProperty private DhcpDto dhcp;
    @JsonProperty private String ip;
    @JsonProperty private String mask;
    @JsonProperty private List<Long> networkConnectionRefs = Lists.newArrayList();

    private SubnetDto() {
        // For Jackson
    }

    private SubnetDto(Long id, DhcpDto dhcp, String ip, String mask, List<Long> networkConnectionRefs) {
        this.id = id;
        this.dhcp = dhcp;
        this.ip = ip;
        this.mask = mask;
        this.networkConnectionRefs = networkConnectionRefs != null ? ImmutableList.copyOf(networkConnectionRefs) : Collections.<Long>emptyList();
    }

    public Long getId() {
        return id;
    }

    public DhcpDto getDhcp() {
        return dhcp;
    }

    public String getIp() {
        return ip;
    }

    public String getMask() {
        return mask;
    }

    @Nonnull
    public List<Long> getNetworkConnectionRefs() {
        return networkConnectionRefs;
    }
}
