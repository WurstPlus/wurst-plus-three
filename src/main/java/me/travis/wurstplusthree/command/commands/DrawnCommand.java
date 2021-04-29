package me.travis.wurstplusthree.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.command.Command;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.util.ClientMessage;

public class DrawnCommand extends Command {

    public DrawnCommand() {
        super("Drawn");
    }

    @Override
    public void execute(String[] message) {
        Hack hack = WurstplusThree.HACKS.getHackByName(message[0].replaceAll("_", " "));
        if (hack == null) {
            ClientMessage.sendErrorMessage("Cannot find hack by name " + ChatFormatting.BOLD + message[0]);
            return;
        }
        if (WurstplusThree.HACKS.isDrawHack(hack)) {
            WurstplusThree.HACKS.removeDrawnHack(hack);
            ClientMessage.sendMessage("Removed " + ChatFormatting.BOLD + hack.getName() + ChatFormatting.RESET + " from drawn list");
        } else {
            WurstplusThree.HACKS.addDrawHack(hack);
            ClientMessage.sendMessage("Added " + ChatFormatting.BOLD + hack.getName() + ChatFormatting.RESET + " to drawn list");
        }
    }
}
