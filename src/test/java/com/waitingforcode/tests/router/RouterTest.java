package com.waitingforcode.tests.router;

import com.waitingforcode.model.Order;
import com.waitingforcode.model.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.MessagingException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Test cases for routers.
 *
 * @author Bartosz Konieczny
 */
@ContextConfiguration(locations = "classpath:META-INF/router-sample.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class RouterTest {

    @Autowired
    @Qualifier("sender")
    private DirectChannel sender;

    @Autowired
    @Qualifier("staticSender")
    private DirectChannel staticSender;

    @Autowired
    @Qualifier("staticSenderNoFailing")
    private DirectChannel staticSenderNoFailing;

    @Autowired
    @Qualifier("senderRouting")
    private DirectChannel senderRouting;

    @Autowired
    @Qualifier("senderRoutingSelector")
    private DirectChannel senderRoutingSelector;

    @Autowired
    @Qualifier("receiverExpensiveOrders")
    private QueueChannel receiverExp;

    @Autowired
    @Qualifier("receiverCheapOrders")
    private QueueChannel receiverChe;

    @Autowired
    @Qualifier("recipient1")
    private QueueChannel recipient1;

    @Autowired
    @Qualifier("recipient2")
    private QueueChannel recipient2;

    @Test
    public void testStaticRouting() {
        Order order = new Order();
        order.setId(30); // will match configured route

        Message<Order> msg = MessageBuilder.<Order>withPayload(order).build();
        staticSender.send(msg, 2000);

        Message<?> received = receiverExp.receive(2000);
        assertEquals("Message should be sent to receiverCheapOrders channel", ((Order) received.getPayload()).getId(),
                order.getId());

        // test with Order containing not matching value
        order.setId(31);
        boolean wasMe = false;
        try {
            staticSender.send(msg, 2000);
        } catch (MessagingException me) {
            wasMe = true;
        }
        assertTrue("MessagingException should be thrown for non-resolvable channel (if resolution-required is set to true)",
                wasMe);

        // compare resolution-required="true" with resolution-required="false" - should throw MessageDeliveryException
        // "org.springframework.messaging.MessageDeliveryException: no channel resolved by router and no default output
        // channel defined"
        boolean wasMde = false;
        try {
            staticSenderNoFailing.send(msg, 2000);
        } catch (MessageDeliveryException mde) {
            wasMde = true;
        }
        assertTrue("MessageDeliveryException should be thrown for non-resolvable channel without resolution required", wasMde);
    }

    @Test
    public void testRecipientRouting() {
        Order order = new Order();
        order.setId(20000);
        Message<Order> msg = MessageBuilder.<Order>withPayload(order).build();

        senderRouting.send(msg, 200);

        Message<?> msgExpReceived = recipient1.receive(2000);
        assertEquals("Message should be sent to recipient1 channel", ((Order) msgExpReceived.getPayload()).getId(),
                order.getId());

        Message<?> msgCheapReceived = recipient2.receive(2000);
        assertEquals("Message should be sent to recipient2 channel", ((Order) msgCheapReceived.getPayload()).getId(),
                order.getId());
    }

    @Test
    public void testRecipientRoutingWithSelectors() {
        Product milk = new Product();
        milk.setName("milk");
        milk.setPrice(30);
        Order order = new Order();
        order.setId(300);
        order.getProducts().add(milk);
        order.calculateFinalPrice();

        Message<Order> msg = MessageBuilder.<Order>withPayload(order).build();
        senderRoutingSelector.send(msg, 200);

        Message<?> msgCheapReceived = recipient1.receive(2000);
        assertEquals("Message should be sent to recipient1 channel", ((Order) msgCheapReceived.getPayload()).getId(),
                order.getId());

        Message<?> msgExpReceived = recipient2.receive(2000);
        assertNull("Message should be sent only for 1st recipent because final price is lower than 50", msgExpReceived);
    }

    @Test
    public void testDynamicRouting() {
        Order order = new Order();
        order.setId(20);
        Product milk = new Product();
        milk.setName("milk");
        milk.setPrice(39.99d);
        Product coffee = new Product();
        coffee.setName("coffee");
        coffee.setPrice(10.01d);
        order.addProduct(milk);
        order.addProduct(coffee);

        Message<Order> msg = MessageBuilder.<Order>withPayload(order).build();
        sender.send(msg, 2000);

        Message<?> msgExpReceived = receiverExp.receive(2000);
        assertNull("Message shouldn't be sent to receiverExpensiveOrders channel", msgExpReceived);

        Message<?> msgCheapReceived = receiverChe.receive(2000);
        assertEquals("Message should be sent to receiverCheapOrders channel", ((Order) msgCheapReceived.getPayload()).getId(),
                order.getId());
    }
}
