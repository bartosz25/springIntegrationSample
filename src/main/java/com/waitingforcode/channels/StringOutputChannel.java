package com.waitingforcode.channels;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.stereotype.Component;

/**
 * TODO : comment this !
 *
 * @author Bartosz Konieczny
 */
@Component
public class StringOutputChannel implements MessageChannel {

    private Message<?> receivedMessage;

    @Override
    // for simplicity reason, send() is used to receive message
    public boolean send(Message<?> message) {
        receivedMessage = message;
        String msg = (String) message.getPayload();
        return true;
    }

    @Override
    public boolean send(Message<?> message, long l) {
        return this.send(message);
    }

    public Message<?> getReceivedMessage() {
        return receivedMessage;
    }
}
