package io.cloudsoft.ravello.brooklyn;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import brooklyn.location.Location;
import brooklyn.location.LocationRegistry;
import brooklyn.location.LocationResolver;
import brooklyn.location.LocationSpec;
import brooklyn.location.basic.BasicLocationRegistry;
import brooklyn.management.ManagementContext;
import brooklyn.util.collections.MutableMap;

public class RavelloLocationResolver implements LocationResolver {

    private static final Logger LOG = LoggerFactory.getLogger(RavelloLocationResolver.class);

    private ManagementContext managementContext;

    @Override
    public void init(ManagementContext managementContext) {
        this.managementContext = managementContext;
    }

    @Override
    public String getPrefix() {
        return "rr";
    }

    @Override
    public boolean accepts(String spec, LocationRegistry registry) {
        return BasicLocationRegistry.isResolverPrefixForSpec(this, spec, false);
    }

    @Deprecated
    @Override
    public Location newLocationFromString(Map properties, String spec) {
        return newLocationFromString(spec, Optional.<LocationRegistry>absent(), properties, new MutableMap());
    }

    @Override
    public Location newLocationFromString(Map locationFlags, String spec, LocationRegistry registry) {
        return newLocationFromString(spec, Optional.of(registry), registry.getProperties(), locationFlags);
    }

    @SuppressWarnings("unchecked")
    private RavelloLocation newLocationFromString(String spec, Optional<LocationRegistry> registry, Map properties, Map locationFlags) {
        Map allProperties = getAllProperties(registry, properties);
        Map ravelloProperties = RavelloPropertiesFromBrooklynProperties.getRavelloProperties(allProperties);
        ravelloProperties.putAll(locationFlags);

        LocationSpec<RavelloLocation> locSpec = LocationSpec.create(RavelloLocation.class).configure(ravelloProperties);
        return managementContext.getLocationManager().createLocation(locSpec);
    }

    @SuppressWarnings("unchecked")
    private Map getAllProperties(Optional<LocationRegistry> registry, Map<?,?> properties) {
        Map<Object,Object> allProperties = Maps.newHashMap();
        if (registry.isPresent()) allProperties.putAll(registry.get().getProperties());
        allProperties.putAll(properties);
        return allProperties;
    }
}
