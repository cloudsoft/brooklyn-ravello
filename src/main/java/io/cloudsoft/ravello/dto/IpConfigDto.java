package io.cloudsoft.ravello.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;

/** Placeholder class that always uses autoIpConfig. */
public class IpConfigDto {

    @JsonProperty private Long id;
    @JsonProperty private Map<String, String> autoIpConfig = Maps.newHashMap();
    @JsonProperty private Boolean hasPublicIP;

}
