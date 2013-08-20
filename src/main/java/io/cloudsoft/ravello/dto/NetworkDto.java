package io.cloudsoft.ravello.dto;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class NetworkDto {

    @JsonProperty private DnsServiceDto dnsService;
    @JsonProperty private List<RouterDto> routers = Lists.newArrayList();
    @JsonProperty("routerLegs") private List<RouterConnectionDto> routerConnections = Lists.newArrayList();
    @JsonProperty private List<SubnetDto> subnets = Lists.newArrayList();

    private NetworkDto() {
        // For Jackson
    }

    private NetworkDto(DnsServiceDto dnsService, List<RouterDto> routers, List<RouterConnectionDto> routerConnections, List<SubnetDto> subnets) {
        this.dnsService = dnsService;
        this.routers = routers != null ? ImmutableList.copyOf(routers) : Collections.<RouterDto>emptyList();
        this.routerConnections = routerConnections != null ? ImmutableList.copyOf(routerConnections) : Collections.<RouterConnectionDto>emptyList();
        this.subnets = subnets != null ? ImmutableList.copyOf(subnets) : Collections.<SubnetDto>emptyList();
    }

    public DnsServiceDto getDnsService() {
        return dnsService;
    }

    @Nonnull
    public List<RouterDto> getRouters() {
        return routers;
    }

    @Nonnull
    public List<RouterConnectionDto> getRouterConnections() {
        return routerConnections;
    }

    @Nonnull
    public List<SubnetDto> getSubnets() {
        return subnets;
    }
}
