package io.cloudsoft.ravello.dto;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

public class CloudTest {

    @Test
    public void testFromString() {
        assertEquals(Cloud.fromString("AMAZON"), Cloud.AMAZON);
    }

    @Test

    public void testFromStringIgnoresCapitalisation() {
        assertEquals(Cloud.fromString("hpcloud"), Cloud.HPCLOUD);
        assertEquals(Cloud.fromString("Amazon"), Cloud.AMAZON);
        assertEquals(Cloud.fromString("aMaZoN"), Cloud.AMAZON);
    }

    @Test
    public void testFromUnknownStringIsUnrecognized() {
        assertEquals(Cloud.fromString("asdf"), Cloud.UNRECOGNIZED);
    }

    @Test
    public void testFromNullIsUnrecognized() {
        assertEquals(Cloud.fromString(null), Cloud.UNRECOGNIZED);
    }

    @Test
    public void testIsKnown() {
        assertTrue(Cloud.isKnownCloud(Cloud.HPCLOUD.name()));
        assertFalse(Cloud.isKnownCloud("asdf"));
    }

    @Test
    public void testHasRegion() {
        assertTrue(Cloud.fromString("hpcloud").hasRegion("US West AZ 1"));
    }

}
