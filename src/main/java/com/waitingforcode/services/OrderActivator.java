package com.waitingforcode.services;

import com.waitingforcode.model.Order;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sample service activator for Message<Order> instances.
 *
 * @author Bartosz Konieczny
 */
@Component
public class OrderActivator {

    public Message<Order> handleOrderMsg(Message<Order> msg) {
        return msg;
    }
}
