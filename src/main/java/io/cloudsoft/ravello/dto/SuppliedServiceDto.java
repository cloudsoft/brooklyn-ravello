package io.cloudsoft.ravello.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SuppliedServiceDto {

    public static final SuppliedServiceDto SSH_SERVICE = SuppliedServiceDto.builder()
            .name("ssh")
            .portRange("22")
            .protocol("SSH")
            .globalService(true)
            .build();

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private String description;
        private String listenIp;
        private String portRange;
        private String protocol;
        private Boolean globalService;

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
        public Builder listenIp(String listenIp) {
            this.listenIp = listenIp;
            return this;
        }
        public Builder portRange(String portRange) {
            this.portRange = portRange;
            return this;
        }
        public Builder protocol(String protocol) {
            this.protocol = protocol;
            return this;
        }
        public Builder globalService(Boolean globalService) {
            this.globalService = globalService;
            return this;
        }
        
        public SuppliedServiceDto build() {
            return new SuppliedServiceDto(id, name, description, listenIp, portRange, protocol, globalService);
        }
    }

    @JsonProperty private String id;
    @JsonProperty private String name;
    @JsonProperty private String description;
    @JsonProperty("ip") private String listenIp;
    @JsonProperty private String portRange;
    @JsonProperty private String protocol;
    @JsonProperty private Boolean globalService;

    private SuppliedServiceDto() {
        // For Jackson
    }

    protected SuppliedServiceDto(String id, String name, String description,
            String listenIp, String portRange, String protocol, Boolean globalService) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.listenIp = listenIp;
        this.portRange = portRange;
        this.protocol = protocol;
        this.globalService = globalService;
    }
}
