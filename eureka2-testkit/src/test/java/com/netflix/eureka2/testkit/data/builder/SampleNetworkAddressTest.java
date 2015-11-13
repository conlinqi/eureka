package com.netflix.eureka2.testkit.data.builder;

import java.util.Iterator;

import com.netflix.eureka2.model.StdModelsInjector;
import com.netflix.eureka2.model.instance.NetworkAddress;
import com.netflix.eureka2.model.instance.NetworkAddress.ProtocolType;
import org.junit.Test;

import static com.netflix.eureka2.model.instance.StdNetworkAddress.NetworkAddressBuilderImpl.aNetworkAddress;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Tomasz Bak
 */
public class SampleNetworkAddressTest {

    static {
        StdModelsInjector.injectStdModels();
    }

    @Test(timeout = 60000)
    public void testGeneratesSubsequentIpAddresses() throws Exception {
        Iterator<NetworkAddress> addressIterator = SampleNetworkAddress.collectionOfIPv4("20", "test.internal", "private");

        NetworkAddress expectedFirst = aNetworkAddress()
                .withHostName("ip20_0_0_1.test.internal")
                .withIpAddress("20.0.0.1")
                .withLabel("private")
                .withProtocolType(ProtocolType.IPv4)
                .build();
        assertThat(addressIterator.next(), is(equalTo(expectedFirst)));

        NetworkAddress expectedSecond = aNetworkAddress()
                .withHostName("ip20_0_0_2.test.internal")
                .withIpAddress("20.0.0.2")
                .withLabel("private")
                .withProtocolType(ProtocolType.IPv4)
                .build();
        assertThat(addressIterator.next(), is(equalTo(expectedSecond)));
    }
}