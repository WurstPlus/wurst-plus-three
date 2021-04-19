package me.travis.wurstplusthree.hack.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.IntSetting;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;

public class ExtraTab extends Hack {

    public static ExtraTab INSTANCE;

    public ExtraTab() {
        super("Extra Tab", "this one doesnt crash ur game promise", Category.RENDER, false, false);
        INSTANCE = this;
    }

    public IntSetting count = new IntSetting("Count", 250, 0, 1000, this);

    public static String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn) {
        String name = networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
        if (WurstplusThree.FRIEND_MANAGER.isFriend(name)) {
            return ChatFormatting.AQUA + name;
        }
        if (WurstplusThree.ENEMY_MANAGER.isEnemy(name)) {
            return ChatFormatting.RED + name;
        }
        if (name.equalsIgnoreCase("TrvsF")) {
            return ChatFormatting.GOLD + name;
        }
        return name;
    }

}
