package com.waitingforcode.splitters;

import com.waitingforcode.model.Order;
import com.waitingforcode.model.Product;
import com.waitingforcode.strategies.OrderComponentsCorrelationStrategy;
import org.springframework.integration.annotation.Splitter;
import org.springframework.integration.splitter.AbstractMessageSplitter;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Sample message splitter which takes each product composing an order and send it separately, in new message.
 *
 * @author Bartosz Konieczny
 */
@Component
public class OrderComponentsSplitter extends AbstractMessageSplitter  {

    private Map<String, List<Message<?>>> splittedMessages = new HashMap<String, List<Message<?>>>();

    @Override
    protected Object splitMessage(Message<?> message) {
        Collection<Message<?>> messages = new ArrayList<Message<?>>();
        Order order = (Order) message.getPayload();
        Iterator<?> iterator = order.getProducts().iterator();
        while (iterator.hasNext()) {
            Product product = (Product) iterator.next();
            Message<?> msg = MessageBuilder.withPayload(product)
                    .setHeaderIfAbsent(OrderComponentsCorrelationStrategy.CORRELATION_KEY, order.getId())
                    .setHeaderIfAbsent(OrderComponentsCorrelationStrategy.LAST_KEY, !iterator.hasNext())
                    .build();
            messages.add(msg);
            addMessage(""+order.getId(), msg);
        }
        return messages;
    }

    public Map<String, List<Message<?>>> getSplittedMessages() {
        return this.splittedMessages;
    }

    public List<Message<?>> getSplittedMessagesByKey(String key) {
        if (!getSplittedMessages().containsKey(key)) {
            addListOfSplittedMessages(key);
        }
        return getSplittedMessages().get(key);
    }

    private void addMessage(String key, Message<?> message) {
        getSplittedMessagesByKey(key).add(message);
    }

    private void addListOfSplittedMessages(String key) {
        getSplittedMessages().put(key, (new ArrayList<Message<?>>()));
    }
}