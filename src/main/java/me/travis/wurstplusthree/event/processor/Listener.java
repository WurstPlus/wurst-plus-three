package me.travis.wurstplusthree.event.processor;

import java.lang.reflect.Method;

/**
 * @author Madmegsox1
 * @since 05/06/2021
 */

public class Listener {
    Method method;
    Object object;
    Class<?> event;
    EventPriority priority;

    public Listener(Method method, Object object, Class<?> event, EventPriority priority){
        this.method = method;
        this.object = object;
        this.event = event;
        this.priority = priority;
    }
}
