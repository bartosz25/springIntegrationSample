package com.waitingforcode.tests.channels;

import com.waitingforcode.handlers.PubSubErrorHandler;
import com.waitingforcode.model.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test cases illustrating the features of {@link PublishSubscribeChannel}
 *
 * @author Bartosz Konieczny
 */
@ContextConfiguration(locations = "classpath:META-INF/pub-sub-channel.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class PubSubChannelTest {

    private final Map<String, Message<?>> receivedMessages = new HashMap<String, Message<?>>();

    @Autowired
    @Qualifier("pubSubChannel")
    private PublishSubscribeChannel pubSubChannel;

    @Autowired
    @Qualifier("pubSubChannelWithErrorHandler")
    private PublishSubscribeChannel pubSubChannelWithErrorHandler;

    @Autowired
    private PubSubErrorHandler pubSubErrorHandler;

    @Test
    public void testSendingAndMinMaxSubscribers() {
        MessageHandler handler1 = constructSampleHandler("#h1");
        pubSubChannel.subscribe(handler1);

        Product milk = new Product();
        milk.setName("milk");
        Message<Product> milkMsg = MessageBuilder.withPayload(milk).build();
        pubSubChannel.send(milkMsg);

        Message<?> handler1Message = receivedMessages.get("#h1");
        assertEquals("milk object should be sent through publish subscribe channel", milk, handler1Message.getPayload());

        // now add some subscriber limits
        pubSubChannel.setMaxSubscribers(1);
        MessageHandler handler2 = constructSampleHandler("#h2");
        boolean wasIae = false;
        try {
            pubSubChannel.subscribe(handler2);
        } catch (IllegalArgumentException iae) {
            wasIae = true;
        }
        assertTrue("IllegalArgumentException is expected for maximum subscriber exceeded error", wasIae);

        pubSubChannel.setMaxSubscribers(10);
        /**
         * Even if we set the minimal number of subscribers and this number is not reached, we won't receive an exception
         * as in the case of maximal number of subscribers. It's because this parameter doesn't mean the same thing.
         * In fact, minSubscribers is applied to PublishSubscribeChannel's dispatcher (
         * {@link org.springframework.integration.dispatcher.BroadcastingDispatcher}) which contains dispatching method
         * (public boolean dispatch(Message<?> message). Inside this method the dispatcher checks if the number of delivered
         * messages is equal to minSubscribers parameter. The comparison is made only when minSubscribers is greater than 0.
         * If the number of delivered messages is lower than minSubscribers, false is returned by send(Message<?>) method.
         */
        pubSubChannel.setMinSubscribers(5);

        Product water = new Product();
        water.setName("water");
        Message<Product> waterMsg = MessageBuilder.withPayload(water).build();
        boolean wasCorrectlySend = pubSubChannel.send(waterMsg);
        handler1Message = receivedMessages.get("#h1");
        assertEquals("water object should be sent through publish subscribe channel", water, handler1Message.getPayload());
        assertFalse("Message shouldn't be correctly sent (min 5 subscribers defined, but only 1 message handler subscribed)",
                wasCorrectlySend);

        // set new min subscribers number
        pubSubChannel.setMinSubscribers(2);
        pubSubChannel.subscribe(handler2);
        wasCorrectlySend = pubSubChannel.send(waterMsg);
        assertTrue("This time message should be correctly send (min 2 subscribers expected, 2 message handlers subscribed)",
                wasCorrectlySend);
        handler1Message = receivedMessages.get("#h2");
        assertEquals("water object should be sent through publish subscribe channel", water, handler1Message.getPayload());

        // now test unsubscribing
        pubSubChannel.unsubscribe(handler2);
        pubSubChannel.send(milkMsg);
        handler1Message = receivedMessages.get("#h2");
        assertNotEquals("Unsubscribed handler shouldn't receive newly crated message with 'milk' payload",
                milk, handler1Message.getPayload());
    }

    @Test
    public void testWithErrorHandler() {
        final String errorMessage = "Hard-coded MessagingException";
        MessageHandler handler3 = new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                throw new MessagingException(errorMessage);
            }
        };
        pubSubChannelWithErrorHandler.subscribe(handler3);
        Product water = new Product();
        water.setName("water");
        Message<Product> waterMsg = MessageBuilder.withPayload(water).build();
        pubSubChannelWithErrorHandler.send(waterMsg);

        // message of error thrown by defined error-handler should be the same as errorMessage object from this method
        // sleep 1 second because pubSubChannelWithErrorHandler is executed in separated thread and sleeping is the less
        // verbose way to "synchronize" both threads
        try {
            Thread.sleep(1000);
        } catch (Exception e){
            fail("Sleeping failed");
        }
        Throwable thrownError = pubSubErrorHandler.getHandlerErrors().poll();
        assertEquals("Bad error was thrown by error-handler", errorMessage, thrownError.getCause().getMessage());

        pubSubChannelWithErrorHandler.setIgnoreFailures(true);
        pubSubChannelWithErrorHandler.send(waterMsg);
        try {
            Thread.sleep(1000);
        } catch (Exception e){
            fail("Sleeping failed");
        }
        thrownError = pubSubErrorHandler.getHandlerErrors().poll();
        assertNull("Error handler shouldn't be invoked when ignore-failures attribute is set to true", thrownError);
    }

    private MessageHandler constructSampleHandler(final String handlerId) {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                receivedMessages.put(handlerId, message);
            }
        };
    }

}
