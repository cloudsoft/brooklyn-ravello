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
    public VmDto(VmPropertiesDto properties, List<HardDriveDto> hardDrives, Set<SuppliedServiceDto> suppliedServices,
            List<NetworkConnectionDto> networkConnections) {
        this.vmProperties = properties;
        this.hardDrives = ImmutableList.copyOf(hardDrives);
        this.suppliedServices = ImmutableSet.<SuppliedServiceDto>builder()
                .addAll(suppliedServices)
                .add(SuppliedServiceDto.SSH_SERVICE)
                .build();
        this.networkConnections = ImmutableList.copyOf(networkConnections);
    }

    public VmPropertiesDto getProperties() {
        return vmProperties;
    }

    public List<HardDriveDto> getHardDrives() {
        return hardDrives;
    }

}