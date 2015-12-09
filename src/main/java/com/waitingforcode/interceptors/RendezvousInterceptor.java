package com.waitingforcode.interceptors;

import com.waitingforcode.model.Product;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Sample channel interceptor which changes priority level when payload's level is equal to 0.
 *
 * @author Bartosz Konieczny
 */
@Component
public class RendezvousInterceptor implements ChannelInterceptor {
    private static Random RANDOM_MAKER = new Random();

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel messageChannel) {
        if (canApply(message)) {
            Product product = (Product) message.getPayload();
            if (product.canApplyPriorityLevel()) {
                product.setPriorityLevel(RANDOM_MAKER.nextInt(100)+1); // +1 because it starts from 0
            }
        }
        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel messageChannel, boolean sent) {
        if (!sent) {
            // NOTE : for monitoring purposes we should prefer something more persistent as LOGGER, but for simple
            // test cases it's simpler (less verbose, less time-taking) to work with System's prints
            System.out.println("Message ("+message+") was not sent correctly to message channel "+messageChannel);
        }
    }

    @Override
    public boolean preReceive(MessageChannel messageChannel) {
        // do nothing, consider receiving as valid
        return true;
    }

    @Override
    public Message<?> postReceive(Message<?> message, MessageChannel messageChannel) {
        // do nothing too
        return message;
    }

    private boolean canApply(Message<?> message) {
        return message.getPayload() instanceof Product;
    }

}
