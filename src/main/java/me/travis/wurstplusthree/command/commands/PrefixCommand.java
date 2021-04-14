package me.travis.wurstplusthree.command.commands;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.command.Command;
import me.travis.wurstplusthree.util.ClientMessage;

public class PrefixCommand extends Command {

    public PrefixCommand() {
        super("Prefix");
    }

    @Override
    public void execute(String[] message) {
        if (message.length == 1) {
            ClientMessage.sendErrorMessage("specify a prefix");
            return;
        }
        WurstplusThree.COMMANDS.setPrefix(message[0]);
        ClientMessage.sendMessage("prefix is now '" + WurstplusThree.COMMANDS.getPrefix() + "'");
    }
}
