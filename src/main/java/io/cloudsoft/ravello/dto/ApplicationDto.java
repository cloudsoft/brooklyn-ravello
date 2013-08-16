package io.cloudsoft.ravello.dto;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ApplicationDto {

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return builder().fromApplicationDto(this);
    }

    public static class Builder {
        private List<VmDto> vms;
        private String id;
        private String name;
        private String description;
        private Integer version;
        private Boolean published;

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
            this.vms = vms;
            return this;
        }
        public Builder vms(VmDto... vms) {
            this.vms = ImmutableList.copyOf(vms);
            return this;
        }
        public Builder addVm(VmDto vm) {
            this.vms = this.vms == null
                ? ImmutableList.of(vm)
                : ImmutableList.<VmDto>builder().add(checkNotNull(vm, "vm")).addAll(vms).build();
            return this;
        }
        public Builder removeVm(final String id) {
            checkNotNull(id, "id");
            if (this.vms == null) return this;
            ImmutableList.Builder<VmDto> vms = ImmutableList.builder();
            for (VmDto vm : this.vms) {
                if (!vm.getId().equals(id)) vms.add(vm);
            }
            this.vms = vms.build();
            return this;
        }
        public Builder version(Integer version) {
            this.version = version;
            return this;
        }
        public Builder incrementVersion() {
            this.version += 1;
            return this;
        }

        public ApplicationDto build() {
            return new ApplicationDto(id, name, description, published, version, vms);
        }

        public Builder fromApplicationDto(ApplicationDto in) {
            return this.id(in.getId())
                .name(in.getName())
                .description(in.getDescription())
                .published(in.isPublished())
                .version(in.getVersion())
                .vms(in.getVMs());
        }

    }

    @JsonProperty("applicationProperties")
    private ApplicationPropertiesDto properties;

    @JsonProperty("vms")
    private List<VmDto> virtualMachines = new ArrayList<VmDto>();

    /** Defaults to zero */
    @JsonProperty private int version;

    private ApplicationDto() {
        // For Jackson
    }

    protected ApplicationDto(String id, String name, String description, Boolean published, Integer version,
            List<VmDto> vms) {
        this.properties = new ApplicationPropertiesDto(id, name, description, published);
        this.virtualMachines = vms == null ? Collections.<VmDto>emptyList() : ImmutableList.copyOf(vms);
        this.version = version == null ? 0 : version;
    }

    @Nonnull
    public List<VmDto> getVMs() {
        return virtualMachines;
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

    public int getVersion() {
        return this.version;
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
                .add("version", version)
                .add("#vms", virtualMachines.size())
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