package me.travis.wurstplusthree.util

import net.minecraft.block.Block

/**
 * @author Madmegsox1
 * @since 08/05/2021
 */


class WhitelistUtil {
    fun findBlock(name: String): Block {
        return Block.getBlockFromName(name)!!
    }
}