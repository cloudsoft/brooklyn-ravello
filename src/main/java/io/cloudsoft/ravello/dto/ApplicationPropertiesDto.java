package io.cloudsoft.ravello.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApplicationPropertiesDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("published")
    private Boolean published;

    public ApplicationPropertiesDto() {
        // For Jackson
    }

    public ApplicationPropertiesDto(String id, String name, String description, Boolean published) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.published = published;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public Boolean isPublished() {
        return published;
    }

}