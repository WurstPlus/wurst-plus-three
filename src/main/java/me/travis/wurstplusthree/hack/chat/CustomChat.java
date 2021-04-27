package me.travis.wurstplusthree.hack.chat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.gui.chat.GuiChat;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.DoubleSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class CustomChat extends Hack {

    public static CustomChat INSTANCE;

    public CustomChat() {
        super("Custom Chat", "customises chat", Category.CHAT, false);
        INSTANCE = this;
    }

    public BooleanSetting customFont = new BooleanSetting("Custom Font", true, this);
    public BooleanSetting rainbow = new BooleanSetting("Rainbow", false, this);
    public BooleanSetting nameHighlight = new BooleanSetting("Name Highlight", true, this);
    public BooleanSetting timeStamps = new BooleanSetting("Time Stamps", true, this);
    public BooleanSetting suffix = new BooleanSetting("Suffix", false, this);
    public BooleanSetting infinite = new BooleanSetting("Infinite", true, this);
    public BooleanSetting smoothChat = new BooleanSetting("SmoothChat", false, this);
    public DoubleSetting xOffset = new DoubleSetting("XOffset", 0.0, 0.0, 600.0,this);
    public DoubleSetting yOffset = new DoubleSetting("YOffset", 0.0, 0.0, 30.0, this);
    public DoubleSetting vSpeed = new DoubleSetting("VSpeed", 30.0, 1.0, 100.0, this);
    public DoubleSetting vLength = new DoubleSetting("VLength",10.0, 5.0, 100.0, this);
    public DoubleSetting vIncrements = new DoubleSetting("VIncrements", 1.0, 1.0, 5.0, this);
    public EnumSetting type = new EnumSetting("Type", "Horizontal",Arrays.asList("Horizontal", "Vertical"), this);

    public static GuiChat guiChatSmooth;
    public static GuiNewChat guiChat;

    // has to be minecraft.getminecraft() bc module is from china town 
    @Override
    public void onEnable() {
        if(nullCheck()) return;
        guiChatSmooth = new GuiChat(Minecraft.getMinecraft());
        ObfuscationReflectionHelper.setPrivateValue(GuiIngame.class, (Minecraft.getMinecraft()).ingameGUI, guiChatSmooth, "field_73840_e");
    }

    @Override
    public void onDisable() {
        if(nullCheck()) return;
        guiChat = new GuiNewChat(Minecraft.getMinecraft());
        ObfuscationReflectionHelper.setPrivateValue(GuiIngame.class, (Minecraft.getMinecraft()).ingameGUI, guiChat, "field_73840_e");
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getStage() == 0 && event.getPacket() instanceof CPacketChatMessage) {
            CPacketChatMessage packet = event.getPacket();
            String s = packet.getMessage();
            if (s.startsWith("/")) {
                return;
            }
            if (this.suffix.getValue()) {
                s += " \u25e6 \u0461\u2795\u01B7";
            }
            if (s.length() >= 256) {
                s = s.substring(0, 256);
            }
            packet.message = s;
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketChat) {
            if (((SPacketChat) event.getPacket()).getType() == ChatType.GAME_INFO) {
                return;
            }
            String originalMessage = ((SPacketChat) event.getPacket()).chatComponent.getFormattedText();
            String message = this.getTimeString(originalMessage);
            if (nameHighlight.getValue()) {
                try {
                    message = message.replace(mc.player.getName(), ChatFormatting.GOLD + mc.player.getName() + ChatFormatting.RESET);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ((SPacketChat) event.getPacket()).chatComponent = new TextComponentString(message);
        }
    }

    private String getTimeString(String message) {
        if (timeStamps.getValue()) {
            String date = new SimpleDateFormat("k:mm").format(new Date());
            return  "[" + date + "] " + message;
        }
        return message;
    }

}
