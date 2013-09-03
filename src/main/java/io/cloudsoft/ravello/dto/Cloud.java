package io.cloudsoft.ravello.dto;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public enum Cloud {

    AMAZON("Virginia", "Oregon"),
    HPCLOUD("US West AZ 1", "US West AZ 2", "US West AZ 3"),
    UNRECOGNIZED;

    private static final Set<String> KNOWN_CLOUDS = ImmutableSet.of(
            AMAZON.name(),
            HPCLOUD.name());

    private final Set<String> regions;

    Cloud(String... regions) {
        this.regions = ImmutableSet.copyOf(regions);
    }

    public boolean hasRegion(String name) {
        return this.regions.contains(name);
    }

    public static Cloud fromString(String cloud) {
        try {
            return cloud != null ? valueOf(cloud.toUpperCase()) : UNRECOGNIZED;
        } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
        }
    }

    public static boolean isKnownCloud(String cloud) {
        return KNOWN_CLOUDS.contains(cloud);
    }

}
