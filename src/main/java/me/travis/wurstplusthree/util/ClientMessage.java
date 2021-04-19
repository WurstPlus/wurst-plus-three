package me.travis.wurstplusthree.util;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.Hack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientMessage implements Globals {

    private static String opener = ChatFormatting.GOLD + WurstplusThree.MODNAME + ChatFormatting.WHITE + " : " + ChatFormatting.RESET;

    public static void sendToggleMessage(Hack hack) {
        if (hack.getName().equalsIgnoreCase("gui")) return;
        ChatFormatting open = (hack.isEnabled() ? ChatFormatting.GREEN : ChatFormatting.RED);
        if (hack.getName().equalsIgnoreCase("crystal aura")) {
            if (open == ChatFormatting.GREEN) {
                sendMessage("we " + open + "gaming");
            } else {
                sendMessage("we aint " + open + "gaming " + ChatFormatting.RESET + "no more");
            }
        } else {
            sendMessage(open + hack.getName());
        }
    }

    public static void sendMessage(String message) {
        sendClientMessage(opener + message);
    }

    public static void sendErrorMessage(String message) {
        sendClientMessage(opener + ChatFormatting.RED + message);
    }

    private static void sendClientMessage(String message) {
        if (mc.player != null) {
            mc.player.sendMessage(new ChatMessage(message));
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

}
