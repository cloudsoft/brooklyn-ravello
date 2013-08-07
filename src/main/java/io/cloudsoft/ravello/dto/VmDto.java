package io.cloudsoft.ravello.dto;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class VmDto {

    @JsonProperty
    private VmPropertiesDto vmProperties;

    @JsonProperty
    private List<HardDriveDto> hardDrives;

    @JsonProperty
    private Set<SuppliedServiceDto> suppliedServices;

    @JsonProperty
    private List<NetworkConnectionDto> networkConnections;

    private VmDto() {
        // For Jackson
    }

    /** Adds {@link SuppliedServiceDto#SSH_SERVICE} to supplied services. */
    public VmDto(String id, String name, String description, Integer numCpus, SizeDto memorySize,
            List<HardDriveDto> hardDrives, Set<SuppliedServiceDto> suppliedServices,
            List<NetworkConnectionDto> networkConnections) {
        this.vmProperties = new VmPropertiesDto(id, name, description, numCpus, memorySize);
        this.hardDrives = ImmutableList.copyOf(hardDrives);
        this.suppliedServices = ImmutableSet.<SuppliedServiceDto>builder()
                .addAll(suppliedServices)
                .add(SuppliedServiceDto.SSH_SERVICE)
                .build();
        this.networkConnections = ImmutableList.copyOf(networkConnections);
    }

    public List<HardDriveDto> getHardDrives() {
        return hardDrives;
    }

    public String getId() {
        return vmProperties.id;
    }

    public String getName() {
        return vmProperties.name;
    }

    public String getDescription() {
        return vmProperties.description;
    }

    public Integer getNumCpus() {
        return vmProperties.numCpus;
    }

    public SizeDto getMemorySize() {
        return vmProperties.memorySize;
    }

    // TODO: As with ApplicationPropertiesDto, would be good if this was a private class.
    public static class VmPropertiesDto {

        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("description")
        private String description;

        @JsonProperty("numCpus")
        private Integer numCpus;

        @JsonProperty("memorySize")
        private SizeDto memorySize;

        private VmPropertiesDto() {
            // For Jackson
        }

        public VmPropertiesDto(String id, String name, String description, Integer numCpus, SizeDto memorySize) {
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