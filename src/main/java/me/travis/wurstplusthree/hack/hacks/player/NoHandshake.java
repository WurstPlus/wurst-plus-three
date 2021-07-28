package me.travis.wurstplusthree.hack.hacks.player;



import io.netty.buffer.Unpooled;
import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.event.processor.EventPriority;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.HackPriority;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

/**
 * madmeg moment
 * @author Ronald(K3B)Jones SKIDDED
 * @since 07/05/2005
 */

@Hack.Registration(name = "NoHandshake", description = "Do not shake hand", category = Hack.Category.PLAYER, priority = HackPriority.Low)
public class NoHandshake extends Hack {

    @CommitEvent(priority = EventPriority.LOW)
    public void onPacketSend(PacketEvent.Send event) {
        CPacketCustomPayload packet;
        if (event.getPacket() instanceof FMLProxyPacket && !mc.isSingleplayer()) {
            event.setCancelled(true);
        }
        if (event.getPacket() instanceof CPacketCustomPayload && (packet = (CPacketCustomPayload)event.getPacket()).getChannelName().equals("MC|Brand")) {
            packet.data = new PacketBuffer(Unpooled.buffer()).writeString("vanilla");
        }
    }
}
