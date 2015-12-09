package com.waitingforcode.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StopWatch;

/**
 * @author Bartosz Konieczny
 */
@ContextConfiguration(locations = "classpath:META-INF/channel-adapter.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class StopWatchTest {

    @Test
    public void test() throws InterruptedException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("test#1");
        Thread.sleep(1000);
        stopWatch.stop();
        System.out.println("result #1 : "+stopWatch.getLastTaskInfo().getTaskName()+" = "+stopWatch.getLastTaskInfo()
                .getTimeSeconds()+ " seconds");

        stopWatch.start("test#2");
        Thread.sleep(2000);
        stopWatch.stop();
        System.out.println("result #2 : "+stopWatch.getLastTaskInfo().getTaskName()+" = "+stopWatch.getLastTaskInfo()
                .getTimeSeconds()+ " seconds");

    }
}
