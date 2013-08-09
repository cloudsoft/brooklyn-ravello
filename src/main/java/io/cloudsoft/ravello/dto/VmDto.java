package io.cloudsoft.ravello.dto;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class VmDto {

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return builder().fromVmDto(this);
    }

    public static class Builder {
        private String id;
        private String name;
        private String description;
        private Integer numCpus;
        private SizeDto memorySize;
        private VmRuntimeInformationDto runtimeInformation;
        private List<HardDriveDto> hardDrives;
        private Set<SuppliedServiceDto> suppliedServices;
        private List<NetworkConnectionDto> networkConnections;

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
        public Builder numCpus(Integer numCpus) {
            this.numCpus = numCpus;
            return this;
        }
        public Builder memorySize(SizeDto memorySize) {
            this.memorySize = memorySize;
            return this;
        }

        /** Only visible for {@link io.cloudsoft.ravello.client.RavelloApiLocalImpl} */
        @VisibleForTesting
        public Builder runtimeInformation(VmRuntimeInformationDto runtimeInformation) {
            this.runtimeInformation = runtimeInformation;
            return this;
        }

        public Builder hardDrives(List<HardDriveDto> hardDrives) {
            this.hardDrives = ImmutableList.copyOf(hardDrives);
            return this;
        }
        public Builder hardDrives(HardDriveDto... hardDrives) {
            this.hardDrives = ImmutableList.copyOf(hardDrives);
            return this;
        }
        public Builder networkConnections(List<NetworkConnectionDto> networkConnections) {
            this.networkConnections = networkConnections;
            return this;
        }
        public Builder networkConnections(NetworkConnectionDto... networkConnections) {
            this.networkConnections = ImmutableList.copyOf(networkConnections);
            return this;
        }
        public Builder suppliedServices(Set<SuppliedServiceDto> suppliedServices) {
            this.suppliedServices = suppliedServices;
            return this;
        }
        public Builder suppliedServices(SuppliedServiceDto... suppliedServices) {
            this.suppliedServices = ImmutableSet.copyOf(suppliedServices);
            return this;
        }

        public VmDto build() {
            VmPropertiesDto properties = new VmPropertiesDto(id, name, description, numCpus, memorySize, runtimeInformation);
            return new VmDto(properties, hardDrives, suppliedServices, networkConnections);
        }

        public Builder fromVmDto(VmDto in) {
            return this
                    .id(in.getId())
                    .name(in.getName())
                    .description(in.getDescription())
                    .numCpus(in.getNumCpus())
                    .memorySize(in.getMemorySize())
                    .hardDrives(in.getHardDrives())
                    .suppliedServices(in.getSuppliedServices())
                    .networkConnections(in.getNetworkConnections());
        }
    }

    @JsonProperty private VmPropertiesDto vmProperties;
    @JsonProperty private List<HardDriveDto> hardDrives;
    @JsonProperty private Set<SuppliedServiceDto> suppliedServices;
    @JsonProperty private List<NetworkConnectionDto> networkConnections;

    private VmDto() {
        // For Jackson
    }

    protected VmDto(VmPropertiesDto properties, List<HardDriveDto> hardDrives, Set<SuppliedServiceDto> suppliedServices,
            List<NetworkConnectionDto> networkConnections) {
        this.vmProperties = properties;
        this.hardDrives = hardDrives != null ? ImmutableList.copyOf(hardDrives) : null;
        this.suppliedServices = suppliedServices != null ? ImmutableSet.copyOf(suppliedServices) : null;
        this.networkConnections = networkConnections != null ? ImmutableList.copyOf(networkConnections) : null;
    }

    public List<HardDriveDto> getHardDrives() {
        return hardDrives;
    }

    public Set<SuppliedServiceDto> getSuppliedServices() {
        return suppliedServices;
    }

    public List<NetworkConnectionDto> getNetworkConnections() {
        return networkConnections;
    }

    public String getId() {
        return vmProperties != null ? vmProperties.getId() : null;
    }

    public String getName() {
        return vmProperties != null ? vmProperties.getName() : null;
    }

    public String getDescription() {
        return vmProperties != null ? vmProperties.getDescription() : null;
    }

    public Integer getNumCpus() {
        return vmProperties != null ? vmProperties.getNumCpus() : null;
    }

    public SizeDto getMemorySize() {
        return vmProperties != null ? vmProperties.getMemorySize() : null;
    }

    public VmRuntimeInformationDto getRuntimeInformation() {
        return vmProperties != null ? vmProperties.getRuntimeInformation() : null;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("properties", vmProperties)
                .add("hardDrives", hardDrives)
                .add("suppliedServices", suppliedServices)
                .add("networkConnections", networkConnections)
                .toString();
    }

    public static class VmPropertiesDto {

        @JsonProperty private String id;
        @JsonProperty private String name;
        @JsonProperty private String description;
        @JsonProperty private Integer numCpus;
        @JsonProperty private SizeDto memorySize;
        @JsonProperty private VmRuntimeInformationDto runtimeInformation;

        private VmPropertiesDto() {
            // For Jackson
        }

        private VmPropertiesDto(String id, String name, String description, Integer numCpus, SizeDto memorySize,
                VmRuntimeInformationDto runtimeInformation) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.numCpus = numCpus;
            this.memorySize = memorySize;
            this.runtimeInformation = runtimeInformation;
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

        public Integer getNumCpus() {
            return numCpus;
        }

        public SizeDto getMemorySize() {
            return memorySize;
        }

        public VmRuntimeInformationDto getRuntimeInformation() {
            return runtimeInformation;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .add("id", id)
                    .add("name", name)
                    .add("description", description)
                    .add("numCpus", numCpus)
                    .add("memorySize", memorySize)
                    .add("runtimeInformation", runtimeInformation)
                    .toString();
        }

    }
}