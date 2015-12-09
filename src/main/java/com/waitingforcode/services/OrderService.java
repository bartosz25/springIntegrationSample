package com.waitingforcode.services;

import com.waitingforcode.model.Order;
import org.springframework.integration.annotation.Gateway;

/**
 * TODO : comment this !
 *
 * @author Bartosz Konieczny
 */
public interface OrderService {

    @Gateway(requestChannel = "sender", replyChannel = "receiver")
    void sendOrder(Order order);

}
