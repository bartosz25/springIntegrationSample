package com.waitingforcode.handlers;

import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Sample error handler for publish-subscribe channel.
 *
 * @author Bartosz Konieczny
 */
@Component
public class PubSubErrorHandler implements ErrorHandler {

    private Queue<Throwable> handledErrors = new LinkedList<Throwable>();

    @Override
    public void handleError(Throwable throwable) {
        handledErrors.add(throwable);
    }

    public Queue<Throwable> getHandlerErrors() {
        return this.handledErrors;
    }
}
