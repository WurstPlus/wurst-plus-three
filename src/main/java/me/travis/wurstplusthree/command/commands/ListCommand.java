package me.travis.wurstplusthree.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.command.Command;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.util.ClientMessage;

public class ListCommand extends Command {

    public ListCommand() {
        super("List", "L");
    }

    @Override
    public void execute(String[] message) {
        ClientMessage.sendMessage(ChatFormatting.BOLD + "Hack list");
        String cat = "";
        if (message.length >= 1) {
            cat = message[0];
        }
        for (Hack.Category category : Hack.Category.values()) {
            if (!cat.equalsIgnoreCase("") && !cat.equalsIgnoreCase(category.getName())) continue;
            ClientMessage.sendMessage(ChatFormatting.BOLD + category.getName());
            for (Hack hack : WurstplusThree.HACKS.getHacksByCategory(category)) {
                ClientMessage.sendMessage(hack.getName() + " : " + hack.getDescription());
            }
        }
    }
}
