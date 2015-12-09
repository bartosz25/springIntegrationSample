package com.waitingforcode.enrichers;

import org.springframework.stereotype.Component;

/**
 * Header enricher appending current miliseconds time to message header.
 *
 * @author Bartosz Konieczny
 */
@Component
public class TimeEnricher {

    public String appendAccessTime() throws InterruptedException {
        Thread.sleep(500);
        return ""+System.currentTimeMillis();
    }

}
