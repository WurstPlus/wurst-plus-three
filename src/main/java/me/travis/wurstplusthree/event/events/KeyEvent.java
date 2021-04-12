package me.travis.wurstplusthree.event.events;

import me.travis.wurstplusthree.event.Event;

public class KeyEvent
        extends Event {
    public boolean info;
    public boolean pressed;

    public KeyEvent(int stage, boolean info, boolean pressed) {
        super(stage);
        this.info = info;
        this.pressed = pressed;
    }
}

