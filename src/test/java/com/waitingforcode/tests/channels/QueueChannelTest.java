package com.waitingforcode.tests.channels;

import com.waitingforcode.model.Product;
import com.waitingforcode.selectors.CoffeeSelector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test cases for {@link QueueChannel} features.
 *
 * @author Bartosz Konieczny
 */
@ContextConfiguration(locations = "classpath:META-INF/queue-channel.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class QueueChannelTest {

    @Autowired
    @Qualifier("queueChannel")
    private QueueChannel queueChannel;

    @Autowired
    @Qualifier("directChannel")
    private DirectChannel directChannel;

    @Autowired
    private CoffeeSelector coffeeSelector;

    @Test
    public void testReceiving() {
        // clear QueueChannel queue. Otherwise the sending will fail with MessageDeliveryException
        queueChannel.clear();
        Message<Product> coffeeMsg = constructMessage("coffee");
        directChannel.send(coffeeMsg);

        Message<?> received = queueChannel.receive(2000);
        assertEquals("Payload of received message should be the same as for sent message", received.getPayload(),
                coffeeMsg.getPayload());
    }

    @Test
    public void testQueueCapacity() {
        Message<Product> coffeeMsg = constructMessage("coffee");
        int remaining = queueChannel.getRemainingCapacity();
        boolean wasMde = false;
        for (int i = 0; i <= queueChannel.getQueueSize(); i++) {
            try {
                directChannel.send(coffeeMsg, 1000);
                assertEquals("Queue doesn't decrease correctly", --remaining, queueChannel.getRemainingCapacity());
            } catch (MessageDeliveryException mde) {
                wasMde = true;
            }
        }
        assertTrue("MessageDeliveryException should be thrown when we try to send message to full queue", wasMde);
    }

    @Test
    public void testPurge() {
        queueChannel.clear();
        Message<Product> coffeeMsg = constructMessage("coffee");
        directChannel.send(coffeeMsg, 1000);
        directChannel.send(coffeeMsg, 1000);
        directChannel.send(coffeeMsg, 1000);
        Message<Product> waterMsg = constructMessage("water");
        directChannel.send(waterMsg, 1000);
        Message<Product> cokeMsg = constructMessage("coke");
        directChannel.send(cokeMsg, 1000);

        // Messages weren't consumed by QueueChannel. It's why we'll test purge() method and remove messages not containg
        // coffee Product.
        assertEquals("No place should be available after adding items to the queue", queueChannel.getRemainingCapacity(), 0);
        queueChannel.purge(coffeeSelector);
        assertEquals("After purging 'no-coffee' products, 2 places should be available in the queue",
                2, queueChannel.getRemainingCapacity());
    }

    private Message<Product> constructMessage(String name) {
        Product product = new Product();
        product.setName(name);
        return MessageBuilder.withPayload(product).build();
    }

}