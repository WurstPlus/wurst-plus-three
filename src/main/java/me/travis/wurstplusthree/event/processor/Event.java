package me.travis.wurstplusthree.event.processor;

/**
 * @author Madmegsox1
 * @since 04/06/2021
 */

public class Event {
    private boolean isCancelled;

    public Event(){
        isCancelled = false;
    }

    /**
     * @return if the event is cancelled
     */
    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * @param cancelled boolean to set if the event is cancelled
     */
    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }
}
