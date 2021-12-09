package me.travis.wurstplusthree.hack.hacks.misc;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.HackPriority;

@Hack.Registration(name = "NoRotate", description = "Could cause desync", category = Hack.Category.MISC, priority = HackPriority.Lowest)
public class NoRotate extends Hack{
    public static NoRotate INSTANCE;

    public NoRotate(){
        INSTANCE = this;
    }
}
