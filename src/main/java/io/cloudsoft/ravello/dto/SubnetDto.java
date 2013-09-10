package io.cloudsoft.ravello.dto;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;

import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class SubnetDto {

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return builder().fromSubnetDto(this);
    }

    public static class Builder {
        private Long id;
        private DhcpDto dhcp;
        private String ip;
        private String mask;
        private Set<Long> networkConnectionRefs = Sets.newHashSet();

        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        public Builder dhcp(DhcpDto dhcp) {
            this.dhcp = dhcp;
            return this;
        }
        public Builder ip(String ip) {
            this.ip = ip;
            return this;
        }
        public Builder mask(String mask) {
            this.mask = mask;
            return this;
        }
        public Builder networkConnectionRefs(Set<Long> networkConnectionRefs) {
            this.networkConnectionRefs = Sets.newHashSet(networkConnectionRefs);
            return this;
        }
        public Builder networkConnectionRefs(Long... networkConnectionRefs) {
            this.networkConnectionRefs = Sets.newHashSet(networkConnectionRefs);
            return this;
        }
        public Builder removeNetworkConnectionRefs(Long... references) {
            return removeNetworkConnectionRefs(ImmutableSet.copyOf(references));
        }
        public Builder removeNetworkConnectionRefs(Iterable<Long> references) {
            return removeNetworkConnectionRefs(ImmutableSet.copyOf(references));
        }
        public Builder removeNetworkConnectionRefs(Collection<Long> networkConnectionRefs) {
            this.networkConnectionRefs.removeAll(networkConnectionRefs);
            return this;
        }

        public SubnetDto build() {
            return new SubnetDto(id, dhcp, ip, mask, ImmutableSet.copyOf(networkConnectionRefs));
        }

        public Builder fromSubnetDto(SubnetDto in) {
            return this.id(in.getId())
                    .ip(in.getIp())
                    .mask(in.getMask())
                    .dhcp(in.getDhcp())
                    .networkConnectionRefs(in.getNetworkConnectionRefs());
        }
    }

    @JsonProperty private Long id;
    @JsonProperty private DhcpDto dhcp;
    @JsonProperty private String ip;
    @JsonProperty private String mask;
    @JsonProperty private Set<Long> networkConnectionRefs = Sets.newHashSet();

    private SubnetDto() {
        // For Jackson
    }

    private SubnetDto(Long id, DhcpDto dhcp, String ip, String mask, Set<Long> networkConnectionRefs) {
        this.id = id;
        this.dhcp = dhcp;
        this.ip = ip;
        this.mask = mask;
        this.networkConnectionRefs = networkConnectionRefs != null ? ImmutableSet.copyOf(networkConnectionRefs) : Collections.<Long>emptySet();
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
    public Set<Long> getNetworkConnectionRefs() {
        return networkConnectionRefs;
    }
}
