package me.travis.wurstplusthree.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.command.Command;
import me.travis.wurstplusthree.hack.client.Gui;
import me.travis.wurstplusthree.util.ClientMessage;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("Help", "H");
    }

    @Override
    public void execute(String[] message) {
        ClientMessage.sendMessage(ChatFormatting.BOLD + "Command list");
        ClientMessage.sendMessage("Your gui is currently bound to " + ChatFormatting.BOLD + Gui.INSTANCE.getBindName());
        for (Command command : WurstplusThree.COMMANDS.getCommands()) {
            ClientMessage.sendMessage(command.getNames().get(0));
        }
    }

}
