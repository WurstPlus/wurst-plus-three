package me.travis.wurstplusthree.util;

import net.minecraft.client.Minecraft;

import java.util.Random;

public interface Globals {
    Minecraft mc = Minecraft.getMinecraft();
    Random random = new Random();

    default public boolean nullCheck(){
        return mc.player == null || mc.world == null;
    }
}
