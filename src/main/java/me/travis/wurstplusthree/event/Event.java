package me.travis.wurstplusthree.event;

import net.minecraftforge.fml.common.eventhandler.Event;

public class Event
        extends net.minecraftforge.fml.common.eventhandler.Event {
    private int stage;

    public Event() {
    }

    public Event(int stage) {
        this.stage = stage;
    }

    public int getStage() {
        return this.stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }
}

