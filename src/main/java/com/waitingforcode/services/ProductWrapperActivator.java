package com.waitingforcode.services;

import com.waitingforcode.model.ProductWrapper;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * TODO : comment this !
 *
 * @author Bartosz Konieczny
 */
@Component
public class ProductWrapperActivator {

    @ServiceActivator(inputChannel="stockCheckingChannel")
    public void handleWrapper(Message<ProductWrapper> msg) {
        System.out.println("Service activator called for "+msg.getPayload()+" with headers "+msg.getHeaders());
    }

}
