package me.travis.wurstplusthree.command.commands;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.command.Command;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.manager.ClientMessage;
import me.travis.wurstplusthree.setting.Setting;
import me.travis.wurstplusthree.setting.Settings;

public class ListCommand extends Command {

    public ListCommand() {
        super("List");
    }

    @Override
    public void execute(String[] message) {
        if (message.length == 1) {
            ClientMessage.sendMessage("listing all commands");
            for (Command command : WurstplusThree.COMMANDS.getCommands()) {
                ClientMessage.sendMessage(command.getName());
            }
            return;
        }
        if (message.length == 2) {
            if (message[0].equalsIgnoreCase("settings")) {
                ClientMessage.sendMessage("listing all settings");
                for (Setting setting : WurstplusThree.SETTINGS.getSettings()) {
                    ClientMessage.sendMessage(setting.getName());
                }
                return;
            }
            if (message[0].equalsIgnoreCase("commands")) {
                ClientMessage.sendMessage("listing all commands");
                for (Command command : WurstplusThree.COMMANDS.getCommands()) {
                    ClientMessage.sendMessage(command.getName());
                }
                return;
            }
            if (message[0].equalsIgnoreCase("hacks")) {
                ClientMessage.sendMessage("listing all hacks");
                for (Hack hack : WurstplusThree.HACKS.getHacks()) {
                    ClientMessage.sendMessage(hack.getName());
                }
                return;
            }
            ClientMessage.sendErrorMessage("Cannot find list for argument '" + message[1] + "'");
        }
    }
}
