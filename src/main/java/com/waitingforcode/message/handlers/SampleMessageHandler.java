package com.waitingforcode.message.handlers;

import org.springframework.integration.handler.AbstractMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * Sample implementation of {@link org.springframework.integration.handler.AbstractMessageHandler} abstract class, defined here
 * only for learning purposes.
 *
 * @author Bartosz Konieczny
 */
@Component
public class SampleMessageHandler extends AbstractMessageHandler {

    private Message<?> lastMessage;

    public Message<?> getLastMessage() {
        return this.lastMessage;
    }

    @Override
    protected void handleMessageInternal(Message<?> message) throws Exception {
        this.lastMessage = message;
    }
}
