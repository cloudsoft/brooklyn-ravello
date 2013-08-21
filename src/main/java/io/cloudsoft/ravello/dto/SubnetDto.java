package io.cloudsoft.ravello.dto;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
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
        private Set<Long> networkConnectionRefs;

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
            this.networkConnectionRefs = networkConnectionRefs;
            return this;
        }
        public Builder networkConnectionRefs(Long... networkConnectionRefs) {
            this.networkConnectionRefs = ImmutableSet.copyOf(networkConnectionRefs);
            return this;
        }
        public Builder removeNetworkConnectionRef(Long... references) {
            return removeNetworkConnectionRefs(ImmutableList.copyOf(references));
        }
        public Builder removeNetworkConnectionRefs(final Iterable<Long> networkConnectionRefs) {
            if (this.networkConnectionRefs != null) {
                this.networkConnectionRefs = Sets.difference(
                        this.networkConnectionRefs, ImmutableSet.copyOf(networkConnectionRefs));
            }
            return this;
        }

        public SubnetDto build() {
            return new SubnetDto(id, dhcp, ip, mask, networkConnectionRefs);
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
