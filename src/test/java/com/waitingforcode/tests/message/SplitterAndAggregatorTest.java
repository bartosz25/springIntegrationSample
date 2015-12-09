package com.waitingforcode.tests.message;

import com.waitingforcode.aggregators.OrderComponentsAggregator;
import com.waitingforcode.model.Order;
import com.waitingforcode.model.Product;
import com.waitingforcode.services.OrderService;
import com.waitingforcode.splitters.OrderComponentsSplitter;
import com.waitingforcode.strategies.OrderComponentsCorrelationStrategy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.channel.RendezvousChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Sample test for splitting-aggregation in Spring Integration.
 *
 * @author Bartosz Konieczny
 */
@ContextConfiguration(locations = "classpath:META-INF/splitting-aggregating.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class SplitterAndAggregatorTest {
    @Autowired
    @Qualifier("sender")
    private DirectChannel sender;

    @Autowired
    @Qualifier("receiver")
    private QueueChannel receiver;

    @Autowired
    @Qualifier("orderComponentsSplitter")
    private OrderComponentsSplitter splitter;

    @Test
    public void splitAndAggregate() {
        Order vegetarianMeal = new Order();
        vegetarianMeal.setId(3392);
        vegetarianMeal.addProduct(constructProduct("carrot"));
        vegetarianMeal.addProduct(constructProduct("apple"));
        vegetarianMeal.addProduct(constructProduct("potatoes"));
        vegetarianMeal.addProduct(constructProduct("beets"));
        vegetarianMeal.addProduct(constructProduct("lettuce"));
        Message<?> orderMsg = MessageBuilder.withPayload(vegetarianMeal).setHeaderIfAbsent(OrderComponentsCorrelationStrategy
                .CORRELATION_KEY, vegetarianMeal.getId()).build();
        sender.send(orderMsg, 4000);

        Message<?> receivedMsg = receiver.receive(4000);
        Order receivedOrder = (Order) receivedMsg.getPayload();
        assertTrue("Initial order price should be 0", vegetarianMeal.getFinalPrice() == 0d);
        assertEquals("Bad Order was sent as payload", vegetarianMeal.getId(), receivedOrder.getId());
        assertTrue("Aggregation failed, expected price was 28.6", 28.6d == receivedOrder.getFinalPrice());

        List<Message<?>> splitted = splitter.getSplittedMessagesByKey(""+vegetarianMeal.getId());
        assertEquals("5 splitted messages are expected", 5, splitted.size());
        List<String> acceptedNames = new ArrayList<String>();
        acceptedNames.add("carrot");
        acceptedNames.add("apple");
        acceptedNames.add("potatoes");
        acceptedNames.add("beets");
        acceptedNames.add("lettuce");
        for (Message<?> msg : splitted) {
            Product msgPayload = (Product) msg.getPayload();
            assertTrue("Unauthorized element was splitted", acceptedNames.indexOf(msgPayload.getName()) > -1);
        }
        for (Product product : receivedOrder.getProducts()) {
            assertTrue("Unauthorized element was sent to receiver", acceptedNames.indexOf(product.getName()) > -1);
        }
    }

    private Product constructProduct(String name) {
        Product product = new Product();
        product.setName(name);
        return product;
    }
}
