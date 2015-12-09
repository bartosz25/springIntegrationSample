package com.waitingforcode.tests.channels;

import com.waitingforcode.messagestores.ProductMessageStore;
import com.waitingforcode.model.Product;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PriorityChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Test cases to show how do {@link PriorityChannel} and {@link org.springframework.integration.store.MessageStore} work.
 *
 * @author Bartosz Konieczny
 */
@ContextConfiguration(locations = "classpath:META-INF/priority-channel.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class PriorityChannelTest {

    @Autowired
    @Qualifier("priorityChannel")
    private PriorityChannel priorityChannel;

    @Autowired
    @Qualifier("directChannel")
    private DirectChannel directChannel;

    @Autowired
    @Qualifier("directChannelToQueue")
    private DirectChannel directChannelToQueue;

    @Autowired
    @Qualifier("queueChannelWithMS")
    private QueueChannel queueChannelWithStore;

    @Autowired
    private ProductMessageStore<Product> productMessageStore;

    @Test
    public void testSampleSend() {
        // You'll see that priority is ascending, number with the lowest value is considered as the most priority
        Message<Product> waterMsg = constructMessage("water", 10);
        Message<Product> milkMsg = constructMessage("milk", 3);
        directChannel.send(milkMsg);
        directChannel.send(waterMsg);

        Message<?> msg1 = priorityChannel.receive(1000);
        Message<?> msg2 = priorityChannel.receive(1000);

        assertEquals("Milk message is expected to be sent first", milkMsg, msg1);
        assertEquals("Water message is expected to be sent second", waterMsg, msg2);
    }

    @Test
    public void testWithTheSamePriority() {
        Message[] messages = new Message[5];
        for (int i = 0; i < 5; i++) {
            messages[i] = constructMessage("orange juice", 1);
            directChannel.send(messages[i]);
        }

        // You can observe that first sent element is received first in the case of two messages with the same payload (the same
        // name and priority level)
        for (int i = 0; i < 5; i++) {
            Message<?> received = priorityChannel.receive(1000);
            assertEquals("Message doesn't appear in expected state (FIFO state) when payloads are the same", messages[i],
                    received);
        }
    }

    @Test
    public void testMessageStore() throws IOException {
        Message<Product> waterMsg = constructMessage("water", 10);
        directChannelToQueue.send(waterMsg);

        Message<?> msg = queueChannelWithStore.receive(1000);
        // check if message's backup was made in specified directory
        String backupProductName = FileUtils.readFileToString(
                new File(productMessageStore.getBackupBasePath()+msg.getHeaders().getId()));
        assertEquals("Bad message was backuped", backupProductName, ((Product)msg.getPayload()).getName());
    }

    private Message<Product> constructMessage(String name, int stars) {
        Product product = new Product();
        product.setName(name);
        product.setPriorityLevel(stars);
        return MessageBuilder.withPayload(product).build();
    }

}
