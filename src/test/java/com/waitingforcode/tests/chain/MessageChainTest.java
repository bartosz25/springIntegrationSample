package com.waitingforcode.tests.chain;

import com.waitingforcode.model.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.MessageRejectedException;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Some test cases to illustrate message handler chain and some of its features as filters and
 * header enrichers.
 *
 * @author Bartosz Konieczny
 */
@ContextConfiguration(locations = "classpath:META-INF/message-chain.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class MessageChainTest {

    @Autowired
    @Qualifier("sender")
    private DirectChannel sender;

    @Autowired
    @Qualifier("receiver")
    private QueueChannel receiver;

    @Autowired
    @Qualifier("trash")
    private QueueChannel trash;

    @Autowired
    @Qualifier("senderForNested")
    private DirectChannel senderForNested;

    @Autowired
    @Qualifier("receiverForNested")
    private QueueChannel receiverForNested;

    @Test
    public void testSimpleSend() {
        Product coffee = constructProduct("coffee");
        Message<Product> coffeeMsg = MessageBuilder.withPayload(coffee).build();
        sender.send(coffeeMsg);

        Message<?> receivedMsg = receiver.receive(3000);
        assertEquals("Received message should be equal to coffee message", receivedMsg.getPayload(), coffeeMsg.getPayload());

        String enrichedValue = (String) receivedMsg.getHeaders().get("enriched");
        assertEquals("Received message should contain 'xxx' enriched header", "xxx", enrichedValue);
    }

    @Test
    public void testCoffeeFilter() throws InterruptedException {
        Product milk = constructProduct("milk");
        Message<Product> milkMsg = MessageBuilder.withPayload(milk).build();
        boolean wasMre = false;
        try {
            sender.send(milkMsg);

        } catch (MessageRejectedException mre) {
            wasMre = true;
        }
        Message<?> receivedMsg = receiver.receive(3000);
        assertNull("Received message should be null because of CoffeeFilter", receivedMsg);
        assertTrue("MessageRejectedException should be thrown on message rejecting", wasMre);

        receivedMsg = trash.receive(4000);
        assertEquals("Discard channel should correctly handle rejected 'milk' message", milk, receivedMsg.getPayload());
    }

    @Test
    public void testNested() {
        Product coffee = constructProduct("coffee");
        Message<Product> milkMsg = MessageBuilder.withPayload(coffee).build();
        senderForNested.send(milkMsg, 3000);
        Message<?> receivedMsg = receiverForNested.receive(3000);
        // Expected order of gateways are: main channel, gateway defined in main channel, gateway defined in previous gateway
        // So values of corresponding headers (respectively main-channel, nested-1 and nested-2) should have values in ascending
        // order
        long mainChannel = Long.valueOf((String)receivedMsg.getHeaders().get("main-channel")).longValue();
        long nested1 = Long.valueOf((String)receivedMsg.getHeaders().get("nested-1")).longValue();
        long nested2 = Long.valueOf((String)receivedMsg.getHeaders().get("nested-2")).longValue();
        assertTrue("Main channel should be called before the 1st gateway",nested1 > mainChannel);
        assertTrue("The 1st gateway should be called before the 2nd gateway", nested2 > nested1);
        assertEquals("Bad payload object was send through gateways", coffee, receivedMsg.getPayload());
    }

    private Product constructProduct(String name) {
        Product product = new Product();
        product.setName(name);
        return product;
    }
}
