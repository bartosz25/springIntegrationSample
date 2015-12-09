package com.waitingforcode.services;

import org.springframework.integration.annotation.Payload;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import sun.net.www.MessageHeader;

/**
 * Class illustrating sample activator.
 *
 * @author Bartosz Konieczny
 */
@Component
public class SampleActivator {

    private boolean notAnnotatedMethodCalled = false;
    private boolean directChannelMethodInvoked = false;

    public void notAnnotatedMethod() {
        notAnnotatedMethodCalled = true;
    }

    public boolean getNotAnnotatedMethodCalled() {
        return notAnnotatedMethodCalled;
    }

    public boolean getDirectChannelMethodInvoked() {
        return directChannelMethodInvoked;
    }

    // @ServiceActivator isn't mandatory when we specify method="message-handler-name" attribute in
    // <int:service-activator /> tag. Its presence is explained only by the wish to store a example
    // of @ServiceActivator annotation's use.
    //@ServiceActivator(inputChannel="directChannel")
    public void sampleHandler(Message<String> msg) {
        directChannelMethodInvoked = true;
        System.out.println("sampleHandler called for "+msg.getPayload()+" with headers "+msg.getHeaders());
    }

    public Message<String> handlerWithReturn(Message<String> msg) {
        System.out.println("Handling "+msg.getPayload()+" with headers "+msg.getHeaders());
        return MessageBuilder.withPayload("Content changed by activator").build();
    }
}
