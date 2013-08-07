package io.cloudsoft.ravello.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

public class ApplicationDto {

    @JsonProperty("applicationProperties")
    private ApplicationPropertiesDto properties;

    @JsonProperty("vms")
    private List<VmDto> virtualMachines = new ArrayList<VmDto>();

    private ApplicationDto() {
        // For Jackson
    }

    public ApplicationDto(String id, String name, String description, Boolean published) {
        this(id, name, description, published, ImmutableList.<VmDto>of());
    }

    public ApplicationDto(String id, String name, String description, Boolean published, List<VmDto> vms) {
        this.properties = new ApplicationPropertiesDto(id, name, description, published);
        this.virtualMachines = ImmutableList.copyOf(vms);
    }

    public List<VmDto> getVMs() {
        return virtualMachines;
    }

    public String getName() {
        return properties.name;
    }

    public String getDescription() {
        return properties.description;
    }

    public String getId() {
        return properties.id;
    }

    public Boolean isPublished() {
        return properties.published;
    }

    // TODO: Would be good for this to be private and for GET /applications to deserialise
    // a list of ApplicationDtos rather than a list of ApplicationDto.ApplicationPropertiesDto.
    public static class ApplicationPropertiesDto {

        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("description")
        private String description;

        @JsonProperty("published")
        private Boolean published;

        private ApplicationPropertiesDto() {
            // For Jackson
        }

        public ApplicationPropertiesDto(String id, String name, String description, Boolean published) {
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

        public Boolean getPublished() {
            return published;
        }
    }

}