package com.waitingforcode.comparators;

import com.waitingforcode.model.Product;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Comparator;

/**
 * Sample comparator used by priority queue to decide which product is more luxury and should be ordered before another one.
 *
 * @author Bartosz Konieczny
 */
@Component
public class ProductMessageComparator implements Comparator<Message<?>> {

    @Override
    public int compare(Message<?> message1, Message<?> message2) {
        if (!(message1.getPayload() instanceof Product) || !(message2.getPayload() instanceof Product)) {
            throw new IllegalArgumentException("Only messages with Product.class instances can be compared here");
        }

        Product product1 = (Product)message1.getPayload();
        Product product2 = (Product)message2.getPayload();
        int result = new Integer(product1.getPriorityLevel()).compareTo(new Integer(product2.getPriorityLevel()));
        if (result == 0) {
            result = product1.getName().compareTo(product2.getName());
        }
        return result;
    }
}
