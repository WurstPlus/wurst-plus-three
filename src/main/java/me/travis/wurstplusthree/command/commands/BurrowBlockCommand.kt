package me.travis.wurstplusthree.command.commands

import me.travis.wurstplusthree.WurstplusThree
import me.travis.wurstplusthree.command.Command
import me.travis.wurstplusthree.hack.combat.Burrow
import me.travis.wurstplusthree.manager.ConfigManager
import me.travis.wurstplusthree.util.ClientMessage
import me.travis.wurstplusthree.util.WhitelistUtil

/**
 * @author Madmegsox1
 * @since 08/05/2021
 */


class BurrowBlockCommand : Command("BurrowBlock", "bb") {

    var bBlock: String = "";

    override fun execute(message: Array<String>) {
        val bClass = WurstplusThree.HACKS.getHackByName("Burrow") as Burrow
        val b = WhitelistUtil().findBlock(message[0])
        if(b.equals(null)){
            ClientMessage.sendMessage("Cannot set Block to ${message[0]}")
            return
        }
        bClass.block = b
        ClientMessage.sendMessage("Set Block to ${message[0]}")
        bBlock = message[0]
        ConfigManager().saveBurrowBlock()
    }

}