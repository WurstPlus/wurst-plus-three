package me.travis.wurstplusthree.event.processor;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Madmegsox1
 * @since 04/06/2021
 */

public final class EventProcessor {

    private final List<Listener> events;

    public EventProcessor() {
        events = new ArrayList<>();
    }

    /**
     * @param object the class that the events are in
     * @throws IllegalArgumentException
     */
    public void addEventListener(Object object) throws IllegalArgumentException {
        getEvents(object);
    }

    public void removeEventListener(Object object) {
        List<Listener> toRemove = new ArrayList<>();
        for(Listener listener : events){
            if(object == listener.object){
                toRemove.add(listener);
            }
        }
        for (Listener listener : toRemove) {
            events.remove(listener);
        }
    }

    /**
     * @param object takes the class where the events are committed
     */
    private void getEvents(Object object) {
        Class<?> clazz = object.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(CommitEvent.class)) {
                Class<?>[] prams = method.getParameterTypes();
                if (prams.length != 1) {
                    throw new IllegalArgumentException("Method " + method + " doesnt have any event parameters");
                }
                if (!Event.class.isAssignableFrom(prams[0])) {
                    throw new IllegalArgumentException("Method " + method + " doesnt have any event parameters only non event parameters");
                }
                this.events.add(new Listener(method, object, prams[0], getPriority(method)));
                this.events.sort(Comparator.comparing(o -> o.priority));
            }
        }
    }

    /**
     * @param event the event that you are listening for
     * @return if the event was posted or not at a boolean
     */
    public boolean postEvent(Event event) {

        for (Listener listener : events){
            if(listener.event == event.getClass()){
                try {
                    listener.method.setAccessible(true);
                    listener.method.invoke(listener.object, event);
                } catch (Exception e) {
                    System.out.println(e);
                    return false;
                }
            }
        }
        return true;
    }

    private EventPriority getPriority(Method method) {
        return method.getAnnotation(CommitEvent.class).priority();
    }
}
