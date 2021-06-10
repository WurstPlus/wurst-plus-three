package me.travis.wurstplusthree.event.processor;

import java.lang.reflect.Method;

/**
 * @author Madmegsox1
 * @since 05/06/2021
 */

public final class Listener {
    public final Method method;
    public final Object object;
    public final Class<?> event;
    public final EventPriority priority;

    public Listener(Method method, Object object, Class<?> event, EventPriority priority){
        this.method = method;
        this.object = object;
        this.event = event;
        this.priority = priority;
    }
}
