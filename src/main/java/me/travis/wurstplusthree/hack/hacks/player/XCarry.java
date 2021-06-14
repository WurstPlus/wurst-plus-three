package me.travis.wurstplusthree.hack.hacks.player;

import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.event.processor.EventPriority;
import me.travis.wurstplusthree.hack.Hack;
import net.minecraft.network.play.client.CPacketCloseWindow;

@Hack.Registration(name = "XCarry", description = "carrys stuff", category = Hack.Category.PLAYER)
public class XCarry extends Hack {

    @CommitEvent(priority = EventPriority.LOW)
    public void onCloseGuiScreen(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketCloseWindow) {
            CPacketCloseWindow packet = event.getPacket();
            if (packet.windowId == XCarry.mc.player.inventoryContainer.windowId) {
                event.setCancelled(true);
            }
        }
    }

}
