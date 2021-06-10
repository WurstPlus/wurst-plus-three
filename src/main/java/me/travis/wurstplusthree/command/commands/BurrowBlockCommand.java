package me.travis.wurstplusthree.command.commands;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.command.Command;
import me.travis.wurstplusthree.hack.combat.Burrow;
import me.travis.wurstplusthree.util.ClientMessage;
import me.travis.wurstplusthree.util.WhitelistUtil;
import net.minecraft.block.Block;

import java.io.IOException;

/**
 * @author Madmegsox1
 * @since 08/05/2021
 */

public class BurrowBlockCommand extends Command {
    public BurrowBlockCommand(){
        super("BurrowBlock", "bb");
    }
    String bBlock = "";

    @Override
    public void execute(String[] message) {
        Burrow bClass = (Burrow) WurstplusThree.HACKS.getHackByName("Burrow");
        Block b = WhitelistUtil.findBlock(message[0]);

        if(b.equals(null)){
            ClientMessage.sendMessage("Cannot set Block to " + message[0]);
            return;
        }
        bClass.setBlock(b);
        ClientMessage.sendMessage("Set Block to " + message[0]);
        bBlock = message[0];
        try {
            WurstplusThree.CONFIG_MANAGER.saveBurrowBlock();
        }catch (IOException e){
        }
    }

    public String getBBlock(){
        return bBlock;
    }

    public void setBBlock(String b){
        bBlock = b;
    }
}
