package me.travis.wurstplusthree.util;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.hacks.chat.ToggleMessages;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientMessage implements Globals {

    private static final String opener() {
        if (mc.player.getName().equalsIgnoreCase("wallhacks_"))
            return ChatFormatting.DARK_BLUE + "[" + ChatFormatting.BLUE + "wurst+" + ChatFormatting.DARK_BLUE + "] " + ChatFormatting.RESET;
        return ChatFormatting.GOLD + WurstplusThree.MODNAME + ChatFormatting.WHITE + " : " + ChatFormatting.RESET;
    }

    public static void sendToggleMessage(Hack hack, boolean enabled) {
        if(mc.world != null && mc.player != null) {
            if(WurstplusThree.HACKS.ishackEnabled("Toggle msgs")) {
                if (hack.getName().equalsIgnoreCase("gui")) return;
                ChatFormatting open = (enabled ? ChatFormatting.GREEN : ChatFormatting.RED);
                boolean compact = ToggleMessages.INSTANCE.compact.getValue();
                if (hack.getName().equalsIgnoreCase("crystal aura")) {
                    if (open == ChatFormatting.GREEN) {
                        sendMessage("we " + open + "gaming", !compact);
                    } else {
                        sendMessage("we aint " + open + "gaming " + ChatFormatting.RESET + "no more", !compact);
                    }
                } else {
                    sendMessage(open + hack.getName(), !compact);
                }
            }
        }
    }

    public static void sendMessage(String message) {
        sendClientMessage(opener() + message);
    }

    public static void sendIRCMessage(String message){
        sendMessage(ChatFormatting.WHITE + "["+ ChatFormatting.AQUA + "IRC - " + WurstplusThree.CHAT_HANDLING.mode + ChatFormatting.WHITE +"] " + ChatFormatting.RESET + message);
    }

    public static void sendErrorMessage(String message) {
        sendClientMessage(opener() + ChatFormatting.RED + message);
    }

    private static void sendClientMessage(String message) {
        if (mc.player != null) {
            mc.player.sendMessage(new ChatMessage(message));
        }
    }

    public static void sendOverwriteClientMessage(String message) {
        if (mc.player != null) {
            final ITextComponent itc = new TextComponentString(opener() + message).setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Travis Fitzroy"))));
            mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(itc, 5936);
        }
    }

    public static void sendRainbowMessage(String message) {
        StringBuilder stringBuilder = new StringBuilder(message);
        stringBuilder.insert(0, "\u00a7+");
        mc.player.sendMessage(new ChatMessage(stringBuilder.toString()));
    }

    public static class ChatMessage extends TextComponentBase {
        String message_input;

        public ChatMessage(String message) {
            Pattern p       = Pattern.compile("&[0123456789abcdefrlosmk]");
            Matcher m       = p.matcher(message);
            StringBuffer sb = new StringBuffer();

            while (m.find()) {
                String replacement = "\u00A7" + m.group().substring(1);
                m.appendReplacement(sb, replacement);
            }

            m.appendTail(sb);
            this.message_input = sb.toString();
        }

        public String getUnformattedComponentText() {
            return this.message_input;
        }

        @Override
        public ITextComponent createCopy() {
            return new ChatMessage(this.message_input);
        }
    }

    public static void sendMessage(String message, boolean perm){
        if(mc.player == null) return;
        try{
            TextComponentString component = new TextComponentString(opener() + message);
            int i = perm ? 0 : 12076;
            mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(component, i);
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}
