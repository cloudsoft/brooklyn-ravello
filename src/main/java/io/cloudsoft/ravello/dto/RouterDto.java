package io.cloudsoft.ravello.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RouterDto {

    @JsonProperty private Long id;
    @JsonProperty private Boolean isExternal;

    private RouterDto() {
        // For Jackson
    }

    private RouterDto(Long id, Boolean external) {
        this.id = id;
        isExternal = external;
    }

    public Long getId() {
        return id;
    }

    public Boolean isExternal() {
        return isExternal;
    }
}
