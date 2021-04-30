package me.travis.wurstplusthree.hack.player;

import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.hack.Hack;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Hack.Registration(name = "XCarry", description = "carrys stuff", category = Hack.Category.PLAYER, isListening = false)
public class XCarry extends Hack {

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
