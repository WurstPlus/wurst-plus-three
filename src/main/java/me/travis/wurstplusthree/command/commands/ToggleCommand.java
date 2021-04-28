package me.travis.wurstplusthree.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.command.Command;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.util.ClientMessage;

public class ToggleCommand extends Command {

    public ToggleCommand() {
        super("Toggle", "T");
    }

    @Override
    public void execute(String[] message) {
        String name = message[0].replaceAll("_", " ");
        Hack hack = WurstplusThree.HACKS.getHackByName(name);
        if (hack != null) {
            hack.toggle();
        } else {
            ClientMessage.sendErrorMessage("Cannot find hack by name " + ChatFormatting.BOLD + name);
        }
    }
}
