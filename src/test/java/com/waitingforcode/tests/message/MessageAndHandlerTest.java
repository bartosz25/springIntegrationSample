package com.waitingforcode.tests.message;

import com.waitingforcode.message.handlers.SampleMessageHandler;
import com.waitingforcode.model.Product;
import com.waitingforcode.model.ProductWrapper;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.springframework.integration.support.*;

/**
 * TODO : comment this !
 *
 * @author Bartosz Konieczny
 */
@ContextConfiguration(locations = "classpath:META-INF/message-and-message-handlers.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class MessageAndHandlerTest {

    @Autowired
    private SampleMessageHandler sampleMessageHandler;

    @Autowired
    @Qualifier(value = "pubSubChannel")
    private PublishSubscribeChannel publishSubscribeChannel;

    private List<String> headersNotFound = new ArrayList<String>();
    {
        headersNotFound.add(MessageHeaders.ID);
        headersNotFound.add(MessageHeaders.TIMESTAMP);
        headersNotFound.add("totalPrice");
    };

    @Test
    public void testSubscribe() {
        publishSubscribeChannel.subscribe(sampleMessageHandler);
        ProductWrapper wrapper = constructWrapper();
        Message<ProductWrapper> msg = new GenericMessage<ProductWrapper>(wrapper, new HashMap<String, Object>());
        publishSubscribeChannel.send(msg);

        assertEquals("Message sent through publish-subscribable channel should be  the same as message got by message handler " +
                " associated to this channel", (ProductWrapper) sampleMessageHandler.getLastMessage().getPayload(),
                msg.getPayload());
    }

    @Test
    public void testMessageManualInit() {
        ProductWrapper wrapper = constructWrapper();

        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("totalPrice", 31.99d);
        Message<ProductWrapper> message = new GenericMessage<ProductWrapper>(wrapper, headers);
        checkMessage(message, headersNotFound);
    }

    @Test
    public void testWithBuilder() {
        ProductWrapper wrapper = constructWrapper();
        Message<ProductWrapper> message = MessageBuilder.withPayload(wrapper)
                .setHeader("totalPrice", 31.99d)
                .build();
        checkMessage(message, headersNotFound);
    }

    @Test
    public void testImmutableHeaders() {
        Map<String, Object> headers = new HashMap<String, Object>();
        MessageHeaders messageHeaders = new MessageHeaders(headers);
        boolean wasUoe = false;
        try {
          messageHeaders.put("test", 1);
        } catch (UnsupportedOperationException uoe) {
            wasUoe = true;
        }
        assertTrue("UnsupportedOperationException should be thrown after trying to add new header after creating MessageHaders " +
                "object ", wasUoe);
    }

    private ProductWrapper constructWrapper() {
        Product milk = new Product();
        milk.setName("Milk");
        ProductWrapper wrapper = new ProductWrapper();
        wrapper.addProduct(milk);
        return wrapper;
    }

    private void checkMessage(Message message, List<String> headersNotFound) {
        MessageHeaders realHeaders = message.getHeaders();
        assertEquals("Number of expected headers aren't the same as for real MessageHeaders object", headersNotFound.size(),
                realHeaders.size());
        for (String key : realHeaders.keySet()) {
            headersNotFound.remove(key);
        }
        assertEquals("After iterating through all MessageHeaders entries, any header should left in headersNotFound list",
                headersNotFound.size(), 0);
    }
}
