package io.cloudsoft.ravello.dto;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public enum Cloud {

    AMAZON("Virginia", "Oregon"),
    HPCLOUD("US West AZ 1", "US West AZ 2", "US West AZ 3"),
    RACKSPACE_OS("Dallas", "Chicago"),
    UNRECOGNIZED;

    public static final Set<String> KNOWN_CLOUDS = ImmutableSet.of(
            AMAZON.name(),
            HPCLOUD.name(),
            RACKSPACE_OS.name());

    private final Set<String> regions;

    Cloud(String... regions) {
        this.regions = ImmutableSet.copyOf(regions);
    }

    public boolean hasRegion(String name) {
        return this.regions.contains(name);
    }

    public static Cloud fromValue(String cloud) {
        try {
            return valueOf(checkNotNull(cloud, "cloud"));
        } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
        }
    }

}
