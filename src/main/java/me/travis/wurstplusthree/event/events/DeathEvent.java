package me.travis.wurstplusthree.event.events;

import me.travis.wurstplusthree.event.Event;
import net.minecraft.entity.player.EntityPlayer;

public class DeathEvent
        extends Event {
    public EntityPlayer player;

    public DeathEvent(EntityPlayer player) {
        this.player = player;
    }
}

