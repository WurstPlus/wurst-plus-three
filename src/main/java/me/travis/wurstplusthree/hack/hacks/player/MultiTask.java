package me.travis.wurstplusthree.hack.hacks.player;

import me.travis.wurstplusthree.hack.Hack;
@Hack.Registration(name = "Multi Task", description = "eat n shit", category = Hack.Category.PLAYER, isListening = false)
public class MultiTask extends Hack {

    public static MultiTask INSTANCE;

    public MultiTask() {
        INSTANCE = this;
    }

}
