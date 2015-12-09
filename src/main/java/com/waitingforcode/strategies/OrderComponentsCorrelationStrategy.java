package com.waitingforcode.strategies;

import org.springframework.integration.aggregator.CorrelationStrategy;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * Correlation strategy is an interface defining which key is used to associate {@link Message} to appropriated
 * {@link org.springframework.integration.store.MessageGroup}. In the case of our splitting of messages with {@link com.waitingforcode.model.Order}
 * payload, used key'll be order's id header.
 *
 * @author Bartosz Konieczny
 */
@Component
public class OrderComponentsCorrelationStrategy implements CorrelationStrategy {

    // key put into header and used by correlation strategy to link several Message<Product> to appropriate oneMessage<Order>
    public static final String CORRELATION_KEY = "orderId";
    // key of parameter meaning if splitted element is the last element needed to aggregate
    public static final String LAST_KEY = "isLast";

    @Override
    public Object getCorrelationKey(Message<?> message) {
        if (!message.getHeaders().containsKey(CORRELATION_KEY)) {
            throw new IllegalStateException("Message splitted by order splitter must contain orderId header. Present headers " +
                    "were: "+message.getHeaders());
        }
        return message.getHeaders().get(CORRELATION_KEY);
    }
}
