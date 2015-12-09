package com.waitingforcode.tests.adapter;

import com.waitingforcode.adapters.ProductChannelAdapter;
import com.waitingforcode.model.Product;
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
import static org.junit.Assert.assertNull;

/**
 * Test cases for channel adapters.
 *
 * @author Bartosz Konieczny
 */
@ContextConfiguration(locations = "classpath:META-INF/channel-adapter.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class ChannelAdaptersTest {

    @Autowired
    @Qualifier("sender1")
    private DirectChannel sender1;

    @Autowired
    @Qualifier("receiver1")
    private QueueChannel receiver1;

    @Autowired
    private ProductChannelAdapter productChannelAdapter;

    @Test
    public void testOutBound() {
        Product milk = new Product();
        milk.setName("milk");
        Message<Product> milkMsg = MessageBuilder.<Product>withPayload(milk).build();
        sender1.send(milkMsg, 2000);

        Product received = productChannelAdapter.getLastProduct();

        assertEquals("Bad message's payload was sent", milk.getName(), received.getName());
        receiver1.receive(2000);
    }

    @Test
    public void testInBound() {
        Product milk = new Product();
        milk.setName("milk");
        Message<Product> milkMsg = MessageBuilder.<Product>withPayload(milk).build();
        sender1.send(milkMsg, 2000);

        Message<?> receivedMsg = receiver1.receive(2000);
        Product receivedProduct = (Product) receivedMsg.getPayload();
        assertEquals("Message was not passed through inbound-channel-adapter before coming to receiver's channel",
                ProductChannelAdapter.PRICE, receivedProduct.getPrice(), 0);

        // this try will fail because of poller's sending rate (3 seconds) and receiver's waiting time (1 seconds)
        Product coffee = new Product();
        coffee.setName("coffee");
        Message<Product> coffeeMsg = MessageBuilder.<Product>withPayload(coffee).build();
        receivedMsg = receiver1.receive(1000);
        assertNull("Message shouldn't be received because of poller's too small sending rate", receivedMsg);
    }

}