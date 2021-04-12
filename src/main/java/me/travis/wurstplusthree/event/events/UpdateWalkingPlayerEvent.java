package me.travis.wurstplusthree.event.events;

import me.travis.wurstplusthree.event.Event;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class UpdateWalkingPlayerEvent
        extends Event {
    public UpdateWalkingPlayerEvent(int stage) {
        super(stage);
    }
}

