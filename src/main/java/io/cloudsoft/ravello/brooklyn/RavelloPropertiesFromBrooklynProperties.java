package io.cloudsoft.ravello.brooklyn;

import java.util.Map;

import com.google.common.base.Predicates;
import com.google.common.collect.Maps;

import brooklyn.config.BrooklynProperties;
import brooklyn.config.ConfigUtils;

public class RavelloPropertiesFromBrooklynProperties {

    public static Map<String, Object> getRavelloProperties(Map<String, Object> properties) {
        Map<String, Object> ravelloProperties = Maps.newHashMap();
        ravelloProperties.putAll(getGenericRavelloProperties(properties));
        ravelloProperties.putAll(getNamedRavelloProperties(properties));
        return ravelloProperties;
    }

    private static Map<String, Object> getGenericRavelloProperties(Map<String, Object> properties) {
        String prefix = "brooklyn.ravello.";
        BrooklynProperties filteredProperties = ConfigUtils.filterForPrefixAndStrip(properties, prefix);
        return ConfigUtils.filterFor(filteredProperties,
                Predicates.not(Predicates.containsPattern("\\."))).asMapWithStringKeys();
    }

    private static Map<String, Object> getNamedRavelloProperties(Map<String, Object> properties) {
        String prefix = "brooklyn.location.named.ravello.";
        BrooklynProperties filteredProperties = ConfigUtils.filterForPrefixAndStrip(properties, prefix);
        return ConfigUtils.filterFor(filteredProperties,
                Predicates.not(Predicates.containsPattern("\\."))).asMapWithStringKeys();
    }

}
