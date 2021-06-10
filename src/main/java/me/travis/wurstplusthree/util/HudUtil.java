package me.travis.wurstplusthree.util;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.player.PlayerSpoofer;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class HudUtil implements Globals {

    public static String getWelcomerLine() {
        int time = TimeUtil.get_hour();
        String line;

        if (time >= 0 && time < 12) {
            line = "Morning, " + ChatFormatting.GOLD + ChatFormatting.BOLD + ((!PlayerSpoofer.INSTANCE.isEnabled() || PlayerSpoofer.INSTANCE.name == null) ? mc.player.getName() : PlayerSpoofer.INSTANCE.name) + ChatFormatting.RESET + " you smell good today :)";
        } else if (time >= 12 && time < 16) {
            line = "Afternoon, " + ChatFormatting.GOLD + ChatFormatting.BOLD +  ((!PlayerSpoofer.INSTANCE.isEnabled() || PlayerSpoofer.INSTANCE.name == null) ? mc.player.getName() : PlayerSpoofer.INSTANCE.name) + ChatFormatting.RESET + " you're looking good today :)";
        } else if (time >= 16 && time < 24) {
            line = "Evening, " + ChatFormatting.GOLD + ChatFormatting.BOLD +  ((!PlayerSpoofer.INSTANCE.isEnabled() || PlayerSpoofer.INSTANCE.name == null) ? mc.player.getName() : PlayerSpoofer.INSTANCE.name) + ChatFormatting.RESET + " you smell good today :)";
        } else {
            line = "Welcome, " + ChatFormatting.GOLD + ChatFormatting.BOLD +  ((!PlayerSpoofer.INSTANCE.isEnabled() || PlayerSpoofer.INSTANCE.name == null) ? mc.player.getName() : PlayerSpoofer.INSTANCE.name) + ChatFormatting.RESET + " you're looking fine today :)";
        }

        return line;
    }

    public static String getTotems() {
        String totems = "";
        int totemCount = mc.player.inventory.mainInventory.stream().filter(stack -> stack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum() +(mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING ? 1 : 0);
        if (totemCount > 2) {
            totems += ChatFormatting.GREEN;
        } else {
            totems += ChatFormatting.RED;
        }
        return totems + totemCount;
    }

    public static String getPingLine() {
        String line = "";
        int ping = WurstplusThree.SERVER_MANAGER.getPing();
        if (ping > 150) {
            line += ChatFormatting.RED;
        } else if (ping > 100) {
            line += ChatFormatting.YELLOW;
        } else {
            line += ChatFormatting.GREEN;
        }
        return line + " " + ping;
    }

    public static String getTpsLine() {
        String line = "";
        double tps = MathsUtil.round(WurstplusThree.SERVER_MANAGER.getTPS(), 1);
        if (tps > 16) {
            line += ChatFormatting.GREEN;
        } else if (tps > 10) {
            line += ChatFormatting.YELLOW;
        } else {
            line += ChatFormatting.RED;
        }
        return line + " " + tps;
    }

    public static String getFpsLine() {
        String line = "";
        int fps = Minecraft.getDebugFPS();
        if (fps > 120) {
            line += ChatFormatting.GREEN;
        } else if (fps > 60) {
            line += ChatFormatting.YELLOW;
        } else {
            line += ChatFormatting.RED;
        }
        return line + " " + fps;
    }

    public static String getAnaTimeLine() {
        String line = "";
        line += TimeUtil.get_hour() < 10 ? "0" + TimeUtil.get_hour() : TimeUtil.get_hour();
        line += ":";
        line += TimeUtil.get_minuite() < 10 ? "0" + TimeUtil.get_minuite() : TimeUtil.get_minuite();
        line += ":";
        line += TimeUtil.get_second() < 10 ? "0" + TimeUtil.get_second() : TimeUtil.get_second();
        return line;
    }

    public static String getDate() {
        return TimeUtil.get_year() + "/" + (TimeUtil.get_month() + 1) + "/" + TimeUtil.get_day();
    }

}

