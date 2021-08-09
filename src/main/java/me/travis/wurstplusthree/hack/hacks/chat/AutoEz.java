package me.travis.wurstplusthree.hack.hacks.chat;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.HackPriority;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import net.minecraft.network.play.client.CPacketChatMessage;


@Hack.Registration(name = "Auto Ez", description = "lets people know ur clouted", category = Hack.Category.CHAT, priority = HackPriority.Lowest)
public class AutoEz extends Hack {
    public static AutoEz INSTANCE;
    public AutoEz() {
        INSTANCE = this;
    }
    BooleanSetting discord = new BooleanSetting("Discord", false, this);

    private int delayCount;

    @Override
    public void onEnable() {
        this.delayCount = 0;
    }

    @Override
    public void onUpdate() {
        delayCount++;
    }

    public void announceDeath() {
        if (this.delayCount < 150 || !this.isEnabled()) return;
        delayCount = 0;
        mc.player.connection.sendPacket(new CPacketChatMessage("you just got nae nae'd by wurst+3" + (discord.getValue() ? " | discord.gg/wurst" : "")));
    }

}
