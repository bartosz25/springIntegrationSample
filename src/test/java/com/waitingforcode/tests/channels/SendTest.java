package com.waitingforcode.tests.channels;

import com.waitingforcode.channels.StringOutputChannel;
import com.waitingforcode.model.Order;
import com.waitingforcode.model.Product;
import com.waitingforcode.model.ProductWrapper;
import com.waitingforcode.services.SampleActivator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Test case showing how to send a message with basic channel.
 *
 * @author Bartosz Konieczny
 */
@ContextConfiguration(locations = "classpath:META-INF/directChannel-with-activator.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class SendTest {

    private ApplicationContext context;

    @Autowired
    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    @Test
    public void testActivator() {
        SampleActivator activator = context.getBean(SampleActivator.class);
        assertFalse("Direct channel method shouldn't be invoked at this stage",
                activator.getDirectChannelMethodInvoked());
        assertFalse("Method not annotated with @ServiceActivator shouldn't be invoked",
                activator.getNotAnnotatedMethodCalled());

        MessageChannel direct = context.getBean("directChannel", MessageChannel.class);
        Message<String> sampleMsg = MessageBuilder.withPayload("Test content")
                .setHeader("website", "http://www.waitingforcode.com")
                .build();
        direct.send(sampleMsg);

        assertTrue("Direct channel method should be invoked after sending a message", activator.getDirectChannelMethodInvoked());
        assertFalse("Method not annotated with @ServiceActivator shouldn't be invoked",
                activator.getNotAnnotatedMethodCalled());
    }

    @Test
    public void testConvert() {
        MessageChannel multipleMessagesChannel = context.getBean("stockCheckingChannel", MessageChannel.class);

        // The stockCheckingChannel supports two types of messages: Order and Product. Both are handled differently, so we test
        // them separately. Product first:
        Product milk = new Product();
        milk.setName("Milk");
        Product cereal = new Product();
        cereal.setName("Cereal");

        Message<Product> productMsg = MessageBuilder.withPayload(milk).build();
        multipleMessagesChannel.send(productMsg);

        // Order after:
        Order breakfastOrder = new Order();
        breakfastOrder.addProduct(milk);
        breakfastOrder.addProduct(cereal);
        Message<Order> orderMsg = MessageBuilder.withPayload(breakfastOrder).build();
        multipleMessagesChannel.send(orderMsg);

        // normal message directly
        Product water = new Product();
        water.setName("Water");
        ProductWrapper wrapper = new ProductWrapper();
        wrapper.addProduct(water);
        Message<ProductWrapper> wrapperMsg = MessageBuilder.withPayload(wrapper).build();
        multipleMessagesChannel.send(wrapperMsg);
    }

    @Test
    public void testWithoutSubscriber() {
        MessageChannel channelNoSubscriber = context.getBean("channelNoSubscriber", MessageChannel.class);
        Message<String> sampleMsg = MessageBuilder.withPayload("Should not be delivered").build();
        boolean wasMde = false;
        try {
            channelNoSubscriber.send(sampleMsg);
        } catch (MessageDeliveryException mde) {
            wasMde = true;
        }
        assertTrue("MessageDeliveryException should be thrown for message sent without subscribers", wasMde);
    }

    /**
     * Tests sending bad typed message. Note however that this error can be simply resolved by adding a converter
     * in this direction: String => Order and String => Product. You can see it one of the furthers test cases.
     */
    @Test
    public void testWithBadDatatype() {
        MessageChannel multipleMessagesChannel = context.getBean("stockCheckingChannel", MessageChannel.class);
        Message<String> sampleMsg = MessageBuilder.withPayload("Bad content shouldn't be delivered correctly").build();
        String exceptionMsg = "";
        try {
            multipleMessagesChannel.send(sampleMsg);
        } catch (MessageDeliveryException mde) {
            exceptionMsg = mde.getMessage();
        }
        String expectedBeginMsg = "Channel 'stockCheckingChannel' expected one of the following datataypes";
        assertTrue("Exception should begin by '"+expectedBeginMsg+"' but it is '"+exceptionMsg+"'",
                exceptionMsg.startsWith(expectedBeginMsg));
    }

    @Test
    public void testActivatorWithReturn() {
        MessageChannel direct = context.getBean("anotherDirectChannel", MessageChannel.class);
        Message<String> sampleMsg = MessageBuilder.withPayload("Test content")
                .setHeader("name", "Bartosz")
                .build();
        direct.send(sampleMsg);

        StringOutputChannel outputSample = context.getBean(StringOutputChannel.class);
        assertNotEquals(sampleMsg.getPayload(), (String)outputSample.getReceivedMessage().getPayload(),
                "Activator should modify the message before transmitting it into output channel");
    }

}
