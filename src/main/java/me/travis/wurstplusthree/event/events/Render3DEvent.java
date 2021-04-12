package me.travis.wurstplusthree.event.events;

import me.travis.wurstplusthree.event.Event;

public class Render3DEvent
        extends Event {
    private final float partialTicks;

    public Render3DEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }
}

