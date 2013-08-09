package io.cloudsoft.ravello.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NetworkConnectionDto {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private Boolean accessPort;
        private NetworkDeviceDto device;
        private IpConfigDto ipConfig;
        private String vlanTag;

        public Builder id(String id) {
            this.id = id;
            return this;
        }
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        public Builder accessPort(Boolean accessPort) {
            this.accessPort = accessPort;
            return this;
        }
        public Builder device(NetworkDeviceDto device) {
            this.device = device;
            return this;
        }
        public Builder ipConfig(IpConfigDto ipConfig) {
            this.ipConfig = ipConfig;
            return this;
        }
        public Builder vlanTag(String vlanTag) {
            this.vlanTag = vlanTag;
            return this;
        }

        public NetworkConnectionDto build() {
            return new NetworkConnectionDto(id, name, accessPort, device, ipConfig, vlanTag);
        }
    }

    @JsonProperty private String id;
    @JsonProperty private String name;
    @JsonProperty private Boolean accessPort;
    @JsonProperty private NetworkDeviceDto device;
    @JsonProperty private IpConfigDto ipConfig;
    @JsonProperty private String vlanTag;

    private NetworkConnectionDto() {
        // For Jackson
    }

    protected NetworkConnectionDto(String id, String name, Boolean accessPort, NetworkDeviceDto device,
            IpConfigDto ipConfig, String vlanTag) {
        this.id = id;
        this.name = name;
        this.accessPort = accessPort;
        this.device = device;
        this.ipConfig = ipConfig;
        this.vlanTag = vlanTag;
    }
}
