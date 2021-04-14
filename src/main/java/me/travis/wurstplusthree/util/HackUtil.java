package me.travis.wurstplusthree.util;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.Hack;

public class HackUtil implements Globals {

    public static boolean shouldPause(Hack hack) {
        if (WurstplusThree.HACKS.ishackEnabled("Surround") && !hack.getName().equalsIgnoreCase("Surround")) {
            return true;
        }
        return false;
    }

}
