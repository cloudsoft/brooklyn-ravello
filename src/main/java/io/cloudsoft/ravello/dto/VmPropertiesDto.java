package io.cloudsoft.ravello.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VmPropertiesDto {

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

    public VmPropertiesDto() {
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