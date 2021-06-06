package me.travis.wurstplusthree.event.events;

import me.travis.wurstplusthree.event.processor.Event;

/**
 * @author Madmegsox1
 * @since 05/06/2021
 */

public class TestEvent extends Event {
    public long startTime;
    public TestEvent(){
        startTime = System.currentTimeMillis();
    }
}
