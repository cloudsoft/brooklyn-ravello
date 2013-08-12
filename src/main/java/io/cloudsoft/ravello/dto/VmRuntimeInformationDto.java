package io.cloudsoft.ravello.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

/** Contained in VmDto properties when a VM is running. */
public class VmRuntimeInformationDto {

    @JsonProperty public Boolean differsFromPublished;
    @JsonProperty("externalFqdn") public String externalFullyQualifiedDomainName;
    @JsonProperty public String state;

    private VmRuntimeInformationDto() {
        // For Jackson
    }

    public VmRuntimeInformationDto(Boolean differsFromPublished, String externalFqdn, String state) {
        this.differsFromPublished = differsFromPublished;
        this.externalFullyQualifiedDomainName = externalFqdn;
        this.state = state;
    }

    public Boolean differsFromPublished() {
        return differsFromPublished;
    }

    public String getExternalFullyQualifiedDomainName() {
        return externalFullyQualifiedDomainName;
    }

    public String getState() {
        return state;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("differsFromPublished", differsFromPublished)
                .add("externalFullyQualifiedDomainName", externalFullyQualifiedDomainName)
                .add("state", state)
                .omitNullValues()
                .toString();
    }
}
