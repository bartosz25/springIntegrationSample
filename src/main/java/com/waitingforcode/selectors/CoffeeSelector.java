package com.waitingforcode.selectors;

import com.waitingforcode.model.Product;
import org.springframework.integration.core.MessageSelector;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * Sample selector used to accept only messages containing "coffee" product.
 *
 * @author Bartosz Konieczny
 */
@Component
public class CoffeeSelector implements MessageSelector {
    @Override
    public boolean accept(Message<?> message) {
        if (message.getPayload().getClass() != Product.class) {
            return false;
        }
        Product product = (Product) message.getPayload();
        return "coffee".equalsIgnoreCase(product.getName());
    }
}
