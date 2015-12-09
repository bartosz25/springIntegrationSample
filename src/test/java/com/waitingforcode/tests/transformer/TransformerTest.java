package com.waitingforcode.tests.transformer;

import com.waitingforcode.model.Order;
import com.waitingforcode.model.Product;
import com.waitingforcode.model.ShoppingCart;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for message transformers.
 *
 * @author Bartosz Konieczny
 */
@ContextConfiguration(locations = "classpath:META-INF/transformer-sample.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TransformerTest {

    @Autowired
    @Qualifier("sender")
    private DirectChannel sender;

    @Autowired
    @Qualifier("senderString")
    private DirectChannel senderString;

    @Autowired
    @Qualifier("senderJson")
    private DirectChannel senderJson;

    @Autowired
    @Qualifier("receiver")
    private QueueChannel receiver;

    @Autowired
    @Qualifier("receiverString")
    private QueueChannel receiverString;

    @Autowired
    @Qualifier("receiverJson")
    private QueueChannel receiverJson;

    @Test
    public void testOrderToShoppingCartTransform() {
        Message<Order> msg = constructSampleOrderMessage();
        Order order = msg.getPayload();

        sender.send(msg, 2000);

        Message<?> receivedMsg = receiver.receive(2000);
        ShoppingCart shoppingCart = (ShoppingCart) receivedMsg.getPayload();
        assertEquals("ShoppingCart should be transformed from Order instance created here", order.getId(),
                shoppingCart.getOrder().getId());
    }

    @Test
    public void testStringTransformer() {
        Message<Order> msg = constructSampleOrderMessage();
        senderString.send(msg, 2000);
        Message<?> receivedMsg = receiverString.receive(2000);
        assertEquals("Object to String transform failed", "Order {products: [Product {name: milk, priority level: 0, " +
                "price: 0.0}], final price: null}", receivedMsg.getPayload());
    }

    @Test
    public void testJsonTransformer() {
        Message<Order> msg = constructSampleOrderMessage();
        senderJson.send(msg, 2000);
        Message<?> receivedMsg = receiverJson.receive(2000);
        assertEquals("Object to JSON transform falied", "{\"id\":300,\"products\":[{\"name\":\"milk\",\"priorityLevel\":0," +
                "\"price\":0.0}],\"finalPrice\":0.0}", receivedMsg.getPayload());
    }

    private Message<Order> constructSampleOrderMessage() {
        Order order = new Order();
        order.setId(300);
        Product milk = new Product();
        milk.setName("milk");
        order.addProduct(milk);
        Message<Order> msg = MessageBuilder.<Order>withPayload(order).build();
        return msg;
    }

}
