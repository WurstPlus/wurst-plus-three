package me.travis.wurstplusthree.event.events;

import me.travis.wurstplusthree.event.Event;
import net.minecraft.entity.player.EntityPlayer;

public class TotemPopEvent
        extends Event {
    private final EntityPlayer entity;

    public TotemPopEvent(EntityPlayer entity) {
        this.entity = entity;
    }

    public EntityPlayer getEntity() {
        return this.entity;
    }
}

