package com.waitingforcode.aggregators;

import com.waitingforcode.model.Order;
import com.waitingforcode.model.Product;
import com.waitingforcode.strategies.OrderComponentsCorrelationStrategy;
import org.springframework.integration.aggregator.AbstractAggregatingMessageGroupProcessor;
import org.springframework.integration.aggregator.AggregatingMessageHandler;
import org.springframework.integration.aggregator.CorrelationStrategy;
import org.springframework.integration.aggregator.MessageGroupProcessor;
import org.springframework.integration.aggregator.ReleaseStrategy;
import org.springframework.integration.annotation.Aggregator;
import org.springframework.integration.store.MessageGroup;
import org.springframework.integration.store.MessageGroupStore;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * Sample aggregator which gets all product messages splitted by {@link com.waitingforcode.splitters.OrderComponentsSplitter}
 * and put them back together into single {@link Order} instance, used as payload of final message.
 *
 * @author Bartosz Konieczny
 */
@Component
public class OrderComponentsAggregator {

    @Aggregator
    public Order aggregate(Collection<Message<?>> products) {
        Order order = new Order();
        for (Message<?> msg : products) {
            order.addProduct((Product)msg.getPayload());
            order.setId((int) msg.getHeaders().get(OrderComponentsCorrelationStrategy.CORRELATION_KEY));
        }
        order.calculateFinalPrice();
        return order;
    }
}
