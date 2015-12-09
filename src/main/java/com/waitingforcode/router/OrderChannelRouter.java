package com.waitingforcode.router;

import com.waitingforcode.model.Order;
import org.springframework.stereotype.Component;

/**
 * Sample case for {@link Order} router.
 *
 * @author Bartosz Konieczny
 */
@Component
public class OrderChannelRouter {

    public String resolveOrderRoute(Order order) {
        order.calculateFinalPrice();
        String channel = "receiverCheapOrders";
        if (order.getFinalPrice() > 50d) {
            channel = "receiverExpensiveOrders";
        }
        return channel;
    }

}
