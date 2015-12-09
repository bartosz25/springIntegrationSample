package com.waitingforcode.tests.channels;

import com.waitingforcode.model.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.channel.RendezvousChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test cases to illustrate {@link RendezvousChannel} specificity.
 *
 * @author Bartosz Konieczny
 */
@ContextConfiguration(locations = "classpath:META-INF/rendezvous-channel.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class RendezvousChannelTest {

    @Autowired
    @Qualifier("rvChannel")
    private RendezvousChannel rvChannel;

    @Test
    public void testSimpleSend() throws InterruptedException {
        rvChannel.clear();
        final boolean[] messagesSendState = new boolean[]{false, false}; // both messages haven't been send yet
        final CountDownLatch latch = new CountDownLatch(1);
        final Message<Product> milkMsg = constructMessage("milk");
        final Message<Product> coffeeMsg = constructMessage("coffee");
        new Thread(new Runnable() {
            @Override
            public void run() {
                messagesSendState[0] = rvChannel.send(milkMsg);
                messagesSendState[1] = rvChannel.send(coffeeMsg, 500);
                latch.countDown();
            }
        }).start();
        Message<?> receivedMsg = rvChannel.receive(1000);
        assertTrue("The first message (milk message) should be consumed because of immediate receive() call",
                messagesSendState[0]);
        assertFalse("The second message (coffee message) shouldn't be send because the first one was consumed under 3 seconds " +
                "(for 0.5 second sending timeout)", messagesSendState[1]);
        assertEquals("Received message should be the same as sent", milkMsg, receivedMsg);

        latch.await(4000, TimeUnit.SECONDS);

        // We check also if channel interceptor behaves correctly (enriches message's payload with random priority level when
        // passed level is equal to 0)
        assertNotEquals("milkMsg should have changed priority level", milkMsg.getPayload().getPriorityLevel(), 0);
        assertNotEquals("coffeeMsg should have changed priority level", coffeeMsg.getPayload().getPriorityLevel(), 0);

    }

    @Test
    public void testSendWithBlock() {
        rvChannel.clear();
        Message<Product> milkMsg = constructMessage("milk");
        long timeout = System.currentTimeMillis()+5000;
        boolean wasSent = false;
        // Test while 5 seconds if message can be sent. It couldn't be because of {@link java.util.concurrent.SynchronousQueue}
        // specificity to accept new elements only when the old one is consumed. Here we try only to push new element and not
        // consume, so the queue is still full. It behaves like 1-sized queue.
        while (timeout > System.currentTimeMillis() && !wasSent) {
            wasSent = rvChannel.send(milkMsg, 500);
        }
        assertFalse("Message shouldn't be sent", wasSent);
    }

    @Test
    public void testProduceConsumerScenario() throws InterruptedException {
        // Test RendezvousChannel in correct, producer-consumer scenario, where every sent message is received directly after
        // by consumer
        rvChannel.clear();
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        final String[] products = new String[] {"milk", "tea", "coffee", "wine", "banana", "bread", "salt", "pepper"};
        final boolean[] messagesState = new boolean[] {false, false, false, false, false, false, false, false};
        final Message<?>[] receivedMessage = new Message<?>[products.length];
        // producer
        new Thread(
            new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < products.length; i++) {
                        Message<Product> msg = constructMessage(products[i]);
                        messagesState[i] = rvChannel.send(msg, 2000);
                    }
                    countDownLatch.countDown();
                }
            }
        ).start();

        // consumer
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < products.length; i++) {
                            receivedMessage[i] = rvChannel.receive(2000);
                        }
                        countDownLatch.countDown();
                    }
                }
        ).start();
        countDownLatch.await();
        for (int i = 0; i < products.length; i++) {
            assertTrue("Message should be correctly received by consumer", messagesState[i]);
            assertEquals("Message was sent in bad order", products[i], ((Product) receivedMessage[i].getPayload()).getName());
        }
    }

    private Message<Product> constructMessage(String name) {
        Product product = new Product();
        product.setName(name);
        return MessageBuilder.withPayload(product).build();
    }
}
