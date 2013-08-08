package io.cloudsoft.ravello.brooklyn;

import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.testng.annotations.Test;

import com.google.common.collect.Maps;

public class RavelloPropertiesFromBrooklynPropertiesTest {

    private static Map<String, Object> genericProperties() {
        Map<String, Object> properties = Maps.newHashMap();
        properties.put("brooklyn.ravello.username", "bob");
        properties.put("brooklyn.ravello.password", "asdf");
        properties.put("brooklyn.ravello.example", "foo");
        return properties;
    }

    private static Map<String, Object> namedProperties() {
        Map<String, Object> properties = Maps.newHashMap();
        properties.put("brooklyn.location.named.ravello.username", "john");
        properties.put("brooklyn.location.named.ravello.password", "fdsa");
        return properties;
    }

    @Test
    public void testGenericProperties() {
        Map<String, Object> properties = RavelloPropertiesFromBrooklynProperties.getRavelloProperties(genericProperties());
        assertEquals(properties.get("username"), "bob");
        assertEquals(properties.get("password"), "asdf");
    }

    @Test
    public void testNamedProperties() {
        Map<String, Object> properties = RavelloPropertiesFromBrooklynProperties.getRavelloProperties(namedProperties());
        assertEquals(properties.get("username"), "john");
        assertEquals(properties.get("password"), "fdsa");
    }

    @Test
    public void testAllProperties() {
        Map<String, Object> allProperties = Maps.newHashMap();
        allProperties.putAll(genericProperties());
        allProperties.putAll(namedProperties());
        Map<String, Object> properties = RavelloPropertiesFromBrooklynProperties.getRavelloProperties(allProperties);
        assertEquals(properties.get("username"), "john");
        assertEquals(properties.get("password"), "fdsa");
        assertEquals(properties.get("example"), "foo");
    }

}
