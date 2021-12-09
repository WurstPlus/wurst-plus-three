package me.travis.wurstplusthree.hack.hacks.misc;

import me.travis.wurstplusthree.RPC;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.HackPriority;


@Hack.Registration(name = "Discord RPC", description = "It is discordrpc !", category = Hack.Category.MISC, priority = HackPriority.Lowest)
public class DiscordRPC extends Hack {


    public void onEnable() {
        RPC.startRPC();
    }

    public void onDisable() {
        RPC.stopRPC();
    }
}
