package me.travis.wurstplusthree.event.events;

import me.travis.wurstplusthree.event.Event;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

public class PacketEvent
        extends Event {
    private final Packet<?> packet;

    public PacketEvent(int stage, Packet<?> packet) {
        super(stage);
        this.packet = packet;
    }

    public <T extends Packet<?>> T getPacket() {
        return (T) this.packet;
    }

    @Cancelable
    public static class Send
            extends PacketEvent {
        public Send(int stage, Packet<?> packet) {
            super(stage, packet);
        }
    }

    @Cancelable
    public static class Receive
            extends PacketEvent {
        public Receive(int stage, Packet<?> packet) {
            super(stage, packet);
        }
    }
}

