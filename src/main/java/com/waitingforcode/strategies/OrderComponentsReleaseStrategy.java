package com.waitingforcode.strategies;

import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.integration.aggregator.ReleaseStrategy;
import org.springframework.integration.store.MessageGroup;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * Release strategy for splitted messages. Release strategy represents the moment when splitted messages can be released for
 * aggregation. In our case, we'll release {@link MessageGroup} when it contains a message with "isLast" header set to true.
 *
 * @author Bartosz Konieczny
 */
@Component
public class OrderComponentsReleaseStrategy implements ReleaseStrategy {

    @Override
    public boolean canRelease(MessageGroup messageGroup) {
        for (Message<?> msg : messageGroup.getMessages()) {
            if ((boolean)msg.getHeaders().get(OrderComponentsCorrelationStrategy.LAST_KEY)) {
                return true;
            }
        }
        return false;
    }
}
