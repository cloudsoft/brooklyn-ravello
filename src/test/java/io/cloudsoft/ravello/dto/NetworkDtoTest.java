package io.cloudsoft.ravello.dto;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class NetworkDtoTest extends MarshallingTest {

    NetworkDto network;

    @BeforeClass
    public void loadApplication() {
        ApplicationDto application = unmarshalFile("application-get.json", ApplicationDto.class);
        assertNotNull(application);
        assertNotNull(application.getNetwork());
        network = application.getNetwork();
    }

    @Test
    public void testNetwork() {
        assertNotNull(network.getRouterConnections().size());
        assertNotNull(network.getRouters().size()) ;
        assertNotNull(network.getSubnets().size());
    }

    @Test
    public void testSubnet() {
        assertEquals(network.getSubnets().size(), 1);
        SubnetDto subnet = network.getSubnets().get(0);
        assertEquals(subnet.getId(), Long.valueOf(609784127362566801L));
        assertEquals(subnet.getIp(), "10.0.0.0");
        assertEquals(subnet.getNetworkConnectionRefs().size(), 3);

        assertEquals(subnet.getDhcp().getIp(), "10.0.0.1");
        assertEquals(subnet.getDhcp().getId(), Long.valueOf(3689913275268769151L));
    }

    @Test
    public void testRouterConnections() {
        assertEquals(network.getRouterConnections().size(), 1);
        RouterConnectionDto connection = network.getRouterConnections().get(0);
        assertEquals(connection.getSubnetRef(), Long.valueOf(609784127362566801L));
        assertEquals(connection.getRouterRef(), Long.valueOf(4142998413910809417L));
    }
}
