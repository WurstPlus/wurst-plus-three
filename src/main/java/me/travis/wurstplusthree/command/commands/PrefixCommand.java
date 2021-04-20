package me.travis.wurstplusthree.command.commands;

import me.travis.wurstplusthree.command.Command;
import me.travis.wurstplusthree.command.Commands;
import me.travis.wurstplusthree.util.ClientMessage;

public class PrefixCommand extends Command {

    public PrefixCommand() {
        super("Prefix");
    }

    @Override
    public void execute(String[] message) {
        if (message.length == 0) {
            ClientMessage.sendErrorMessage("Specify prefix");
            return;
        }
        if (message.length == 1) {
            String prefix = message[0];
            Commands.prefix = prefix;
            ClientMessage.sendMessage("Prefix set to " + prefix);
        }
    }
}
