package io.cloudsoft.ravello.dto;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class VmDto {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private String description;
        private Integer numCpus;
        private SizeDto memorySize;
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
        public Builder hardDrives(List<HardDriveDto> hardDrives) {
            this.hardDrives = ImmutableList.copyOf(hardDrives);
            return this;
        }
        public Builder hardDrives(HardDriveDto... hardDrives) {
            this.hardDrives = ImmutableList.copyOf(hardDrives);
            return this;
        }
        public Builder networkConnections(List<NetworkConnectionDto> networkConnections) {
            this.networkConnections = ImmutableList.copyOf(networkConnections);
            return this;
        }
        public Builder networkConnections(NetworkConnectionDto... networkConnections) {
            this.networkConnections = ImmutableList.copyOf(networkConnections);
            return this;
        }
        public Builder suppliedServices(List<SuppliedServiceDto> suppliedServices) {
            this.suppliedServices = ImmutableSet.copyOf(suppliedServices);
            return this;
        }
        public Builder suppliedServices(SuppliedServiceDto... suppliedServices) {
            this.suppliedServices = ImmutableSet.copyOf(suppliedServices);
            return this;
        }

        public VmDto build() {
            return new VmDto(id, name, description, numCpus, memorySize, hardDrives, suppliedServices, networkConnections);
        }

    }

    @JsonProperty private VmPropertiesDto vmProperties;
    @JsonProperty private List<HardDriveDto> hardDrives;
    @JsonProperty private Set<SuppliedServiceDto> suppliedServices;
    @JsonProperty private List<NetworkConnectionDto> networkConnections;

    private VmDto() {
        // For Jackson
    }

    protected VmDto(String id, String name, String description, Integer numCpus, SizeDto memorySize,
            List<HardDriveDto> hardDrives, Set<SuppliedServiceDto> suppliedServices,
            List<NetworkConnectionDto> networkConnections) {
        this.vmProperties = new VmPropertiesDto(id, name, description, numCpus, memorySize);
        this.hardDrives = hardDrives;
        this.suppliedServices = suppliedServices;
        this.networkConnections = networkConnections;
    }

    public List<HardDriveDto> getHardDrives() {
        return hardDrives;
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

    public Set<SuppliedServiceDto> getSuppliedServices() {
        return suppliedServices;
    }

    public List<NetworkConnectionDto> getNetworkConnections() {
        return networkConnections;
    }

    public static class VmPropertiesDto {

        @JsonProperty private String id;
        @JsonProperty private String name;
        @JsonProperty private String description;
        @JsonProperty private Integer numCpus;
        @JsonProperty private SizeDto memorySize;

        private VmPropertiesDto() {
            // For Jackson
        }

        private VmPropertiesDto(String id, String name, String description, Integer numCpus, SizeDto memorySize) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.numCpus = numCpus;
            this.memorySize = memorySize;
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
    }
}