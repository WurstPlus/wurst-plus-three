package me.travis.wurstplusthree.hack.misc;

import me.travis.wurstplusthree.RPC;
import me.travis.wurstplusthree.hack.Hack;



@Hack.Registration(name = "DiscordRPC", description = "It is discordrpc !", category = Hack.Category.MISC, isListening = false)
public class DiscordRPC extends Hack {


    public void onEnable() {
        RPC.startRPC();
    }

    public void onDisable() {
        RPC.stopRPC();
    }
}
