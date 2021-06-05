package me.travis.wurstplusthree.event.processor;



import me.travis.wurstplusthree.util.elements.Pair;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Madmegsox1
 * @since 04/06/2021
 */

public class EventProcessor {
    private final HashMap<Method, Pair<Object, Class<?>>> eventMap;

    public EventProcessor() {
        eventMap = new HashMap<>();
    }

    /**
     * @param object the class that the events are in
     * @throws IllegalArgumentException
     */
    public void addEventListener(Object object) throws IllegalArgumentException {
        getEvents(object);
    }

    public void removeEventListener(Object object) {
        ArrayList<Method> toRemove = new ArrayList<>();
        for (Method method : eventMap.keySet()) {
            if (object == eventMap.get(method).getKey()) {
                toRemove.add(method);
            }
        }
        for (Method method : toRemove) {
            eventMap.remove(method);
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
                Class<?> eventType = prams[0];
                if (!Event.class.isAssignableFrom(eventType)) {
                    throw new IllegalArgumentException("Method " + method + " doesnt have any event parameters only non event parameters");
                }
                Pair<Object, Class<?>> pair = new Pair<>(object, eventType);
                this.eventMap.put(method, pair);
                this.eventMap.clear();
                this.eventMap.putAll(sortMap(this.eventMap));
            }
        }
    }

    private HashMap<Method, Pair<Object, Class<?>>> sortMap(HashMap<Method, Pair<Object, Class<?>>> map) {
        HashMap<Method, Pair<Object, Class<?>>> finalMap = new HashMap<>();
        HashMap<Method, Pair<Object, Class<?>>> high = new HashMap<>();
        HashMap<Method, Pair<Object, Class<?>>> none = new HashMap<>();
        HashMap<Method, Pair<Object, Class<?>>> low = new HashMap<>();
        for (Method method : map.keySet()) {
            if (getPriority(method) == EventPriority.HIGH) {
                high.put(method, map.get(method));
            } else if (getPriority(method) == EventPriority.LOW) {
                low.put(method, map.get(method));
            } else {
                none.put(method, map.get(method));
            }
        }
        finalMap.putAll(high);
        finalMap.putAll(none);
        finalMap.putAll(low);
        return finalMap;
    }

    /**
     * @return the event map
     */
    public HashMap<Method, Pair<Object, Class<?>>> getEventMap() {
        return eventMap;
    }

    /**
     * @param event the event that you are listening for
     * @return if the event was posted or not at a boolean
     */
    public boolean postEvent(Event event) {
        for (Method method : getEventMap().keySet()) {
            EventPriority priority = getPriority(method);
            Pair<Object, Class<?>> pair = getEventMap().get(method);
            if (pair.getValue() == event.getClass()) {
                try {
                    method.setAccessible(true);
                    method.invoke(pair.getKey(), event);
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
