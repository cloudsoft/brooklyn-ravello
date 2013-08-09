package io.cloudsoft.ravello.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Contained in VmDto properties when a VM is running. */
public class VmRuntimeInformationDto {

    @JsonProperty public Boolean differsFromPublished;
    @JsonProperty public String externalFqdn;
    @JsonProperty public String state;

    private VmRuntimeInformationDto() {
        // For Jackson
    }

    public VmRuntimeInformationDto(Boolean differsFromPublished, String externalFqdn, String state) {
        this.differsFromPublished = differsFromPublished;
        this.externalFqdn = externalFqdn;
        this.state = state;
    }

    public Boolean getDiffersFromPublished() {
        return differsFromPublished;
    }

    public String getExternalFqdn() {
        return externalFqdn;
    }

    public String getState() {
        return state;
    }
}
