package io.cloudsoft.ravello.brooklyn;

import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.testng.annotations.Test;

import com.google.common.collect.Maps;

public class RavelloPropertiesFromBrooklynPropertiesTest {

    private static Map<String, Object> genericProperties() {
        Map<String, Object> properties = Maps.newHashMap();
        properties.put("brooklyn.location.ravello.user", "bob");
        properties.put("brooklyn.location.ravello.password", "asdf");
        properties.put("brooklyn.location.ravello.example", "foo");
        return properties;
    }

    // FIXME not needed -- see comment in RavelloPropertiesFromBrooklynProperties
    private static Map<String, Object> namedProperties() {
        Map<String, Object> properties = Maps.newHashMap();
        properties.put("brooklyn.location.named.ravello.user", "john");
        properties.put("brooklyn.location.named.ravello.password", "fdsa");
        return properties;
    }

    @Test
    public void testGenericProperties() {
        Map<String, Object> properties = RavelloPropertiesFromBrooklynProperties.getRavelloProperties(genericProperties());
        assertEquals(properties.get("user"), "bob");
        assertEquals(properties.get("password"), "asdf");
    }

    @Test
    public void testNamedProperties() {
        Map<String, Object> properties = RavelloPropertiesFromBrooklynProperties.getRavelloProperties(namedProperties());
        assertEquals(properties.get("user"), "john");
        assertEquals(properties.get("password"), "fdsa");
    }

    @Test
    public void testAllProperties() {
        Map<String, Object> allProperties = Maps.newHashMap();
        allProperties.putAll(genericProperties());
        allProperties.putAll(namedProperties());
        Map<String, Object> properties = RavelloPropertiesFromBrooklynProperties.getRavelloProperties(allProperties);
        assertEquals(properties.get("user"), "john");
        assertEquals(properties.get("password"), "fdsa");
        assertEquals(properties.get("example"), "foo");
    }

}
