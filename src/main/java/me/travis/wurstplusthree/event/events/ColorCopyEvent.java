package me.travis.wurstplusthree.event.events;

import me.travis.wurstplusthree.event.EventStage;
import me.travis.wurstplusthree.gui.component.Component;
import me.travis.wurstplusthree.util.elements.Colour;

public class ColorCopyEvent extends EventStage {
    public Colour copedColor;
    public Component colorComponent;
    public EventType eventType;

    public ColorCopyEvent(Colour val, Component component, EventType eventType){
        this.copedColor = val;
        this.colorComponent = component;
        this.eventType = eventType;
    }

    public enum EventType {
        COPY,
        PAST
    }
}
