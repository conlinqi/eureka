package com.netflix.eureka2.server.channel;

import java.util.concurrent.TimeUnit;

import com.netflix.eureka2.metric.noop.NoOpStateMachineMetrics;
import com.netflix.eureka2.spi.transport.EurekaConnection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author David Liu
 */
public class AbstractHandlerChannelTest {

    private final EurekaConnection transport = mock(EurekaConnection.class);
    private final Exception transportOnErrorReturn = new Exception();
    private final TestSubscriber<Void> subscriber = new TestSubscriber<>();

    private AbstractHandlerChannel channel;

    @Before
    public void setUp() {
        when(transport.submit(anyObject())).thenReturn(Observable.<Void>empty());
        // this is current BaseMessageConnection behaviour
        when(transport.onError(any(Throwable.class))).thenReturn(Observable.<Void>error(transportOnErrorReturn));
        channel = spy(new TestHandlerChannel(transport));
    }

    @Test(timeout = 60000)
    public void testCloseChannelOnSendError() throws Exception {
        when(transport.submit(anyObject())).thenReturn(Observable.<Void>error(new Exception("msg")));

        channel.asLifecycleObservable().subscribe(subscriber);

        channel.sendOnTransport("My Message");

        verify(channel, times(1)).close();
        subscriber.awaitTerminalEvent(10, TimeUnit.SECONDS);
        Assert.assertEquals(1, subscriber.getOnErrorEvents().size());
    }

    @Test(timeout = 60000)
    public void testCloseChannelWhenSendingErrorOnTransportSuccessfully() throws Exception {

        channel.asLifecycleObservable().subscribe(subscriber);


        channel.sendErrorOnTransport(new Exception("some error"));

        verify(channel, times(1)).close();
        subscriber.awaitTerminalEvent(10, TimeUnit.SECONDS);
        Assert.assertEquals(1, subscriber.getOnErrorEvents().size());
    }

    enum TestState {}

    public class TestHandlerChannel extends AbstractHandlerChannel<TestState> {
        protected TestHandlerChannel(EurekaConnection transport) {
            super(null, transport, new NoOpStateMachineMetrics<TestState>());
        }
    }
}
