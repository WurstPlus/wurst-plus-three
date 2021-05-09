package me.travis.wurstplusthree.util;

import net.minecraft.block.Block;

/**
 * @author Madmegsox1
 * @since 08/05/2021
 */

public class WhitelistUtil {
    public static Block findBlock(String name){
        return Block.getBlockFromName(name);
    }
}
