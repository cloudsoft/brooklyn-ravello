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
        private String baseVmId;
        private String name;
        private String description;
        private Integer numCpus;
        private SizeDto memorySize;
        private String platform;
        private List<String> hostnames;
        private Boolean requiresKeypair;
        private String keypairId;
        private VmRuntimeInformationDto runtimeInformation;

        private List<HardDriveDto> hardDrives;
        private Set<SuppliedServiceDto> suppliedServices;
        private List<NetworkConnectionDto> networkConnections;

        public Builder id(String id) {
            this.id = id;
            return this;
        }
        public Builder baseVmId(String baseVmId) {
            this.baseVmId = baseVmId;
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
        public Builder hostname(String hostname) {
            this.hostnames = ImmutableList.of(hostname);
            return this;
        }
        public Builder hostnames(List<String> hostnames) {
            this.hostnames = hostnames;
            return this;
        }
        public Builder hostnames(String... hostnames) {
            this.hostnames = ImmutableList.copyOf(hostnames);
            return this;
        }
        public Builder platform(String platform) {
            this.platform = platform;
            return this;
        }
        public Builder requiresKeypair(Boolean requiresKeypair) {
            this.requiresKeypair = requiresKeypair;
            return this;
        }
        public Builder keypairId(String keypairId) {
            this.keypairId = keypairId;
            return this;
        }

        /** Only visible for {@link io.cloudsoft.ravello.client.RavelloApiLocalImpl} */
        @VisibleForTesting
        public Builder runtimeInformation(VmRuntimeInformationDto runtimeInformation) {
            this.runtimeInformation = runtimeInformation;
            return this;
        }

        public Builder hardDrives(List<HardDriveDto> hardDrives) {
            this.hardDrives = hardDrives;
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
            VmPropertiesDto properties = new VmPropertiesDto(id, baseVmId, name, description, numCpus, memorySize,
                    platform, hostnames, requiresKeypair, keypairId, runtimeInformation);
            return new VmDto(properties, hardDrives, suppliedServices, networkConnections);
        }

        public Builder fromVmDto(VmDto in) {
            return this
                    .id(in.getId())
                    .baseVmId(in.getBaseVmId())
                    .name(in.getName())
                    .description(in.getDescription())
                    .numCpus(in.getNumCpus())
                    .memorySize(in.getMemorySize())
                    .platform(in.getPlatform())
                    .hostnames(in.getHostnames())
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

    // Methods also on vmProperties:

    public String getId() {
        return vmProperties != null ? vmProperties.getId() : null;
    }

    public String getBaseVmId() {
        return vmProperties != null ? vmProperties.getBaseVmId() : null;
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

    public String getPlatform() {
        return vmProperties != null ? vmProperties.getPlatform() : null;
    }

    public List<String> getHostnames() {
        return vmProperties != null ? vmProperties.getHostnames() : null;
    }

    public Boolean requiresKeypair() {
        return vmProperties != null ? vmProperties.requiresKeypair() : null;
    }

    public String getKeypairId() {
        return vmProperties != null ? vmProperties.getKeypairId() : null;
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
                .omitNullValues()
                .toString();
    }

    public static class VmPropertiesDto {

        @JsonProperty private String id;
        @JsonProperty private String baseVmId;
        @JsonProperty private String name;
        @JsonProperty private String description;
        @JsonProperty private Integer numCpus;
        @JsonProperty private SizeDto memorySize;
        @JsonProperty private List<String> hostnames;
        @JsonProperty private String platform;
        @JsonProperty private Boolean requiresKeypair;

        /**
         * The docs say this is optional but the API always sets it to false if it's missing,
         * whatever the value of the property on the baseVmId.
         */
        @JsonProperty private Boolean supportsCloudInit = true;

        /** Docs say this should be on VmDto, but it's only accepted from the properties. */
        @JsonProperty private String keypairId;

        @JsonProperty private VmRuntimeInformationDto runtimeInformation;

        private VmPropertiesDto() {
            // For Jackson
        }

        private VmPropertiesDto(String id, String baseVmId, String name, String description, Integer numCpus,
                SizeDto memorySize, String platform, List<String> hostnames, Boolean requiresKeypair,
                String keypairId, VmRuntimeInformationDto runtimeInformation) {
            this.id = id;
            this.baseVmId = baseVmId;
            this.name = name;
            this.description = description;
            this.numCpus = numCpus;
            this.memorySize = memorySize;
            this.platform = platform;
            this.hostnames = hostnames != null ? ImmutableList.copyOf(hostnames) : null;
            this.keypairId = keypairId;
            this.requiresKeypair = requiresKeypair;
            this.runtimeInformation = runtimeInformation;
        }

        public String getId() {
            return id;
        }

        public String getBaseVmId() {
            return baseVmId;
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

        public String getPlatform() {
            return platform;
        }

        public List<String> getHostnames() {
            return hostnames;
        }

        public Boolean requiresKeypair() {
            return requiresKeypair;
        }

        public String getKeypairId() {
            return keypairId;
        }

        public VmRuntimeInformationDto getRuntimeInformation() {
            return runtimeInformation;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .add("id", id)
                    .add("baseVmId", baseVmId)
                    .add("name", name)
                    .add("description", description)
                    .add("numCpus", numCpus)
                    .add("memorySize", memorySize)
                    .add("platform", platform)
                    .add("hostnames", hostnames)
                    .add("requiresKeypair", requiresKeypair)
                    .add("keypairId", keypairId)
                    .add("runtimeInformation", runtimeInformation)
                    .omitNullValues()
                    .toString();
        }

    }
}