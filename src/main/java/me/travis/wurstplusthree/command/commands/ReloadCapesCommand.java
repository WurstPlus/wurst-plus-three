package me.travis.wurstplusthree.command.commands;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.command.Command;
import me.travis.wurstplusthree.util.ClientMessage;

/**
 * @author Madmegsox1
 * @since 01/05/2021
 */

public class ReloadCapesCommand extends Command {

    public ReloadCapesCommand(){
        super("ReloadCapes", "RC", "ReloadCape");
    }

    @Override
    public void execute(String[] message) {
        WurstplusThree.CAPE_MANAGER.reload();
        ClientMessage.sendMessage("Reloaded Capes!");
    }
}
