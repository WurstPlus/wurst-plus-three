package me.travis.wurstplusthree.hack.player;

import me.travis.wurstplusthree.hack.Hack;

public class MultiTask extends Hack {

    public static MultiTask INSTANCE;

    public MultiTask() {
        super("MultiTask", "eat n shit", Category.PLAYER, false, false);
        INSTANCE = this;
    }

}
