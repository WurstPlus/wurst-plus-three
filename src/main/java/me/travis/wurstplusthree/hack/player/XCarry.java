package me.travis.wurstplusthree.hack.player;

import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.hack.Hack;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class XCarry extends Hack {

    public XCarry() {
        super("XCarry", "carrys stuff", Category.PLAYER, false);
    }

    @SubscribeEvent
    public void onCloseGuiScreen(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketCloseWindow) {
            CPacketCloseWindow packet = event.getPacket();
            if (packet.windowId == XCarry.mc.player.inventoryContainer.windowId) {
                event.setCanceled(true);
            }
        }
    }

}
