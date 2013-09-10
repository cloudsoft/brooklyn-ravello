package io.cloudsoft.ravello.brooklyn;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.config.BrooklynProperties;
import brooklyn.config.ConfigUtils;

// TODO: Use Brooklyn's LocationPropertiesFromBrooklynProperties class as total replacement
import com.google.common.collect.Maps;

public class RavelloPropertiesFromBrooklynProperties {

    private static final Logger log = LoggerFactory.getLogger(RavelloPropertiesFromBrooklynProperties.class);
    
    public static Map<String, Object> getRavelloProperties(Map<String, Object> properties) {
        Map<String, Object> ravelloProperties = Maps.newHashMap();
        ravelloProperties.putAll(getGenericRavelloProperties(properties));
        ravelloProperties.putAll(getGenericOldStyleRavelloProperties(properties));
        ravelloProperties.putAll(getNamedRavelloProperties(properties));
        return ravelloProperties;
    }

    private static Map<String, Object> getPrefixedProperties(String prefix, Map<String, Object> properties) {
        BrooklynProperties filteredProperties = ConfigUtils.filterForPrefixAndStrip(properties, prefix);
        // TODO confirm, I think we *should* include keys which contain '.' character (Alex)
//        return ConfigUtils.filterFor(filteredProperties,
//                Predicates.not(Predicates.containsPattern("\\."))).asMapWithStringKeys();
        return filteredProperties.asMapWithStringKeys();
    }
    
    private static Map<String, Object> getGenericRavelloProperties(Map<String, Object> properties) {
        return getPrefixedProperties("brooklyn.location.ravello.", properties);
    }

    private static Map<String, Object> getGenericOldStyleRavelloProperties(Map<String, Object> properties) {
        // NB: this syntax is deprecated in 0.6.0
        Map<String, Object> result = getPrefixedProperties("brooklyn.ravello.", properties);
        if (!result.isEmpty()) {
            log.warn("Properties beginning with deprecated syntax 'brooklyn.ravello' should be renamed as 'brooklyn.location.ravello': "+result);
        }
        return result;
    }

    // FIXME don't think we need this one -- should be inherited when requested as named:XXX
    private static Map<String, Object> getNamedRavelloProperties(Map<String, Object> properties) {
        return getPrefixedProperties("brooklyn.location.named.ravello.", properties);
    }

}
