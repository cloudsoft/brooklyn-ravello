package io.cloudsoft.ravello.dto;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class NetworkDto {

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return builder().fromNetworkDto(this);
    }

    public static class Builder {
        private DnsServiceDto dnsService;
        private List<RouterDto> routers;
        private List<RouterConnectionDto> routerConnections;
        private List<SubnetDto> subnets;

        public Builder dnsService(DnsServiceDto dnsService) {
            this.dnsService = dnsService;
            return this;
        }
        public Builder routers(List<RouterDto> routers) {
            this.routers = routers;
            return this;
        }
        public Builder routers(RouterDto... routers) {
            this.routers = ImmutableList.copyOf(routers);
            return this;
        }
        public Builder routerConnections(List<RouterConnectionDto> routerConnections) {
            this.routerConnections = routerConnections;
            return this;
        }
        public Builder routerConnections(RouterConnectionDto... routerConnections) {
            this.routerConnections = ImmutableList.copyOf(routerConnections);
            return this;
        }
        public Builder subnets(List<SubnetDto> subnets) {
            this.subnets = subnets;
            return this;
        }
        public Builder subnets(SubnetDto... subnets) {
            this.subnets = ImmutableList.copyOf(subnets);
            return this;
        }

        public NetworkDto build() {
            return new NetworkDto(dnsService, routers, routerConnections, subnets);
        }

        public Builder fromNetworkDto(NetworkDto in) {
            return this.dnsService(in.getDnsService())
                    .routers(in.getRouters())
                    .routerConnections(in.getRouterConnections())
                    .subnets(in.getSubnets());
        }
    }

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
