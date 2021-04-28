package me.travis.wurstplusthree.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.command.Command;
import me.travis.wurstplusthree.command.Commands;
import me.travis.wurstplusthree.util.ClientMessage;

public class PrefixCommand extends Command {

    public PrefixCommand() {
        super("Prefix");
    }

    @Override
    public void execute(String[] message) {
        if (message.length == 1) {
            String prefix = message[0];
            if (prefix.length() != 1) {
                ClientMessage.sendErrorMessage("Prefix not valid");
            } else {
                Commands.prefix = prefix;
                ClientMessage.sendMessage("Prefix set to " + ChatFormatting.BOLD + prefix);
            }
        }
    }
}
