package com.waitingforcode.tests.gateways;

import com.waitingforcode.model.Product;
import com.waitingforcode.services.ProductService;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test case for Spring Integration's messaging gateways.
 *
 * @author Bartosz Konieczny
 */
@ContextConfiguration(locations = "classpath:META-INF/messaging-gateways.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class CheckGatewayTest {

    @Autowired
    @Qualifier("inputBookingChannel")
    private MessageChannel sender;

    @Autowired
    @Qualifier("outputBookingChannel")
    private PollableChannel bookingChannel;

    @Autowired
    @Qualifier("outputSellingChannel")
    private PollableChannel sellingChannel;

    @Autowired
    @Qualifier("outputBuyingChannel")
    private PollableChannel buyingChannel;

    @Autowired
    private ProductService productService;

    @Test
    public void testSimpleMessageReceiving() {
        Product milk = new Product();
        milk.setName("milk");

        // Please note that we don't use sender anywhere to transfer the message from
        // one channel to another. It's made directly by Spring through @Gateway annotated
        // method in ProductService interface
        productService.bookProduct(milk);

        Message<?> msg = bookingChannel.receive(3000);
        assertEquals("Object sent through Gateway should be milk", msg.getPayload(), milk);
    }

    @Test
    public void testSellingChannel() {
        Product coffee = new Product();
        coffee.setName("coffee");

        productService.sellProduct(coffee);

        Message<?> msg = sellingChannel.receive(3000);
        assertEquals("Object sent through Gateway should be coffee", msg.getPayload(), coffee);

        // Supplementary headers, created with @Gateway's headers attribute, should be present in this case.
        // One of them was resolved dynamically, from ProductHeaderBean's getReversedName method.
        assertEquals("Bad reversed product name was generated", "eeffoc", (String)msg.getHeaders().get("dynamicHeader"));
        assertEquals("Hard-coded header's value is not the same as expected", "hardCodedValue",
                (String)msg.getHeaders().get("fixedHeader"));
    }

    @Test
    public void testBuyingChannel() {
        Product potatoes = new Product();
        potatoes.setName("potatoes");

        productService.buyProduct("Wholesaler & son", 39.99d, potatoes);
        Message<?> msg = buyingChannel.receive(3000);
        assertEquals("Object sent through Gateway should be potatoes", msg.getPayload(), potatoes);

        // Supplementary headers, created with @Header annotation, should be present in this case.
        assertTrue("Price should be 39.99", (double)msg.getHeaders().get("price") == 39.99d);
        assertEquals("Bad seller company passed in header", "Wholesaler & son", (String)msg.getHeaders().get("companyName"));
    }

}
