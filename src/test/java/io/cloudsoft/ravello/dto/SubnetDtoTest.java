package io.cloudsoft.ravello.dto;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

public class SubnetDtoTest {

    @Test
    public void testRemovingNetworkRef() {
        SubnetDto subnet = SubnetDto.builder()
                .networkConnectionRefs(1L, 2L, 3L, 4L, 5L)
                .build();
        assertTrue(subnet.getNetworkConnectionRefs().contains(4L));
        subnet = subnet.toBuilder().removeNetworkConnectionRef(3L, 4L).build();
        assertEquals(subnet.getNetworkConnectionRefs(), ImmutableSet.of(1L, 2L, 5L));
    }

}
