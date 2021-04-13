package me.travis.wurstplusthree.command.commands;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.command.Command;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.manager.ClientMessage;

public class ToggleCommand extends Command {

    public ToggleCommand() {
        super("Toggle");
    }

    @Override
    public void execute(String[] message) {
        if (message.length == 1) {
            ClientMessage.sendErrorMessage("specify a hack");
            return;
        }
        Hack hack = WurstplusThree.HACKS.getHackByName(message[0]);
        if (hack == null) {
            ClientMessage.sendErrorMessage("cannot find hack called " + message[0]);
            return;
        }
        hack.toggle();
    }
}
