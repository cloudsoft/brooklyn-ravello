package io.cloudsoft.ravello.dto;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class ApplicationDto {

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return builder().fromApplicationDto(this);
    }

    public static class Builder {
        private List<VmDto> vms = Lists.newArrayList();
        private String id;
        private String name;
        private String description;
        private Boolean published;
        private NetworkDto network;

        public Builder id(String id) {
            this.id = id;
            return this;
        }
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        public Builder published(Boolean published) {
            this.published = published;
            return this;
        }
        public Builder vms(List<VmDto> vms) {
            this.vms = Lists.newArrayList(vms);
            return this;
        }
        public Builder vms(VmDto... vms) {
            this.vms = Lists.newArrayList(vms);
            return this;
        }
        public Builder addVm(VmDto vm) {
            this.vms.add(vm);
            return this;
        }
        public Builder removeVm(final String id) {
            checkNotNull(id, "id");
            for (Iterator<VmDto> it = vms.iterator(); it.hasNext(); ) {
                VmDto vm = it.next();
                if (id.equals(vm.getId())) {
                    it.remove();
                    removeVmFromNetworkSubnetRefs(vm);
                }
            }

            return this;
        }

        private void removeVmFromNetworkSubnetRefs(VmDto removed) {
            // Need to remove the vm.networkConnections[].ipConfig.id from
            // application.network.subnets[].networkConnectionRefs
            if (this.network == null || removed == null) return;

            final Iterable<Long> idsToRemove = FluentIterable.from(removed.getNetworkConnections())
                    .transform(new Function<NetworkConnectionDto, Long>() {
                        @Override public Long apply(NetworkConnectionDto input) {
                            return input.getIpConfig() != null ? input.getIpConfig().getId() : null;
                        }})
                    .filter(Predicates.notNull());

            List<SubnetDto> revisedSubnets = Lists.transform(this.network.getSubnets(), new Function<SubnetDto, SubnetDto>() {
                @Override public SubnetDto apply(SubnetDto input) {
                    return input.toBuilder().removeNetworkConnectionRefs(idsToRemove).build();
                }
            });

            network(this.network.toBuilder().subnets(revisedSubnets).build());
        }

        public Builder network(NetworkDto network) {
            this.network = network;
            return this;
        }

        public ApplicationDto build() {
            return new ApplicationDto(id, name, description, published, ImmutableList.copyOf(vms), network);
        }

        public Builder fromApplicationDto(ApplicationDto in) {
            return this.id(in.getId())
                .name(in.getName())
                .description(in.getDescription())
                .network(in.getNetwork())
                .published(in.isPublished())
                .vms(in.getVMs());
        }

    }

    @JsonProperty("applicationProperties")
    private ApplicationPropertiesDto properties;

    @JsonProperty("vms")
    private List<VmDto> virtualMachines = new ArrayList<VmDto>();

    @JsonProperty
    private NetworkDto network;

    private ApplicationDto() {
        // For Jackson
    }

    protected ApplicationDto(String id, String name, String description, Boolean published,
            List<VmDto> vms, NetworkDto network) {
        this.properties = new ApplicationPropertiesDto(id, name, description, published);
        this.virtualMachines = vms == null ? Collections.<VmDto>emptyList() : ImmutableList.copyOf(vms);
        this.network = network;
    }

    @Nonnull
    public List<VmDto> getVMs() {
        return virtualMachines;
    }

    public NetworkDto getNetwork() {
        return network;
    }

    public String getName() {
        return properties != null ? properties.getName() : null;
    }

    public String getDescription() {
        return properties != null ? properties.getDescription() : null;
    }

    public String getId() {
        return properties != null ? properties.getId() : null;
    }

    @Nonnull
    public Boolean isPublished() {
        return properties != null && properties.isPublished();
    }

    public ApplicationPropertiesDto getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", getId())
                .add("name", getName())
                .add("published", isPublished())
                .add("#vms", virtualMachines.size())
                .add("network", network)
                .omitNullValues()
                .toString();
    }

    public static class ApplicationPropertiesDto {

        @JsonProperty private String id;
        @JsonProperty private String name;
        @JsonProperty private String description;
        @JsonProperty private Boolean published;

        private ApplicationPropertiesDto() {
            // For Jackson
        }

        private ApplicationPropertiesDto(String id, String name, String description, Boolean published) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.published = published;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Boolean isPublished() {
            return published != null && published;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .add("id", id)
                    .add("name", name)
                    .add("description", description)
                    .add("published", published)
                    .omitNullValues()
                    .toString();
        }
    }

}