package com.waitingforcode.transformer;

import com.waitingforcode.model.Order;
import com.waitingforcode.model.ShoppingCart;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Sample message transformer's implementation that constructs the payload for new message directly from incoming message's
 * payload.
 *
 * @author Bartosz Konieczny
 */
@Component
public class ShoppingCartTransformer {

    public ShoppingCart fromOrder(Order order) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setCreationDate(new Date());
        shoppingCart.setOrder(order);
        return shoppingCart;
    }

}
