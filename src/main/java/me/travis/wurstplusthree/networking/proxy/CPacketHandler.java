package me.travis.wurstplusthree.networking.proxy;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.PacketEvent;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.CPacketEncryptionResponse;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.network.play.client.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.Socket;

/**
 * @author Madmegsox1
 * @since 14/07/2021
 */

public final class CPacketHandler {

    private final PacketEvent.Send event;
    public boolean didSend;

    public CPacketHandler(final @NotNull PacketEvent.Send event) {
        this.event = event;
        didSend = false;
        sendCPacketToProxy();
    }

    private void sendCPacketToProxy() {
        try {
            Socket s = WurstplusThree.PROXY.bind();
            String toSend = createProxyPacket();
            if(toSend.equals("CLIENT")){
                return;
            }
            event.setCancelled(true);

            WurstPlusProxy.sendData(s, toSend);
            String[] data = WurstPlusProxy.getData(s);
            if(data[0].equals("SERVER") && data[1].equals("DONE")){
                didSend = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createProxyPacket(){
        String packet = "CLIENT";

        if(event.getPacket() instanceof CPacketAnimation){
            packet += ":Animation:" + ((CPacketAnimation) event.getPacket()).getHand();
        }
        else if(event.getPacket() instanceof CPacketPlayer){
            CPacketPlayer p = event.getPacket();
            packet += ":Player:" + p.moving + ":" + p.onGround + ":" + p.rotating + ":" + p.pitch + ":" + p.yaw + ":" +p.x + ":" + p.y + ":" + p.z;
        }
        else if(event.getPacket() instanceof CPacketChatMessage){
            packet += ":ChatMessage:" + ((CPacketChatMessage) event.getPacket()).message;
        }
        else if(event.getPacket() instanceof CPacketConfirmTeleport){
            packet += ":ConfirmTeleport:" + ((CPacketConfirmTeleport) event.getPacket()).getTeleportId();
        }
        else if(event.getPacket() instanceof CPacketClientStatus){
            packet += ":ClientStatus:" + ((CPacketClientStatus) event.getPacket()).getStatus();
        }
        else if(event.getPacket() instanceof CPacketClickWindow){
            CPacketClickWindow p = event.getPacket();
            packet += ":ClickWindow:" + p.getWindowId() + ":" + p.getClickType() + ":" + p.getClickedItem().getDisplayName() + ":" + p.getActionNumber() + ":" + p.getSlotId() + ":" + p.getUsedButton();
        }
        else if(event.getPacket() instanceof CPacketCloseWindow){
            packet += ":CloseWindow:" + ((CPacketCloseWindow) event.getPacket()).windowId;
        }
        else if(event.getPacket() instanceof CPacketConfirmTransaction){
            packet += ":ConfirmTransaction:" + ((CPacketConfirmTransaction) event.getPacket()).getWindowId() + ":" + ((CPacketConfirmTransaction) event.getPacket()).getUid();
        }
        else if(event.getPacket() instanceof CPacketLoginStart){
            packet += ":LoginStart:" + ((CPacketLoginStart) event.getPacket()).getProfile().getName() + ":" + ((CPacketLoginStart) event.getPacket()).getProfile().getId();
        }
        else if(event.getPacket() instanceof C00Handshake){
            packet += ":Handshake:" + ((C00Handshake) event.getPacket()).hasFMLMarker() + ":" + ((C00Handshake) event.getPacket()).getRequestedState();
        }
        else if(event.getPacket() instanceof CPacketEncryptionResponse){
            packet += ":EncryptionResponse";
        }
        else if(event.getPacket() instanceof CPacketKeepAlive){
            packet += ":KeepAlive";
        }


        // Just realised this is pointless lol


        return packet;
    }

}
