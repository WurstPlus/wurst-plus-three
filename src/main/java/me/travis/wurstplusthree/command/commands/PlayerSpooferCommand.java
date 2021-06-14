package me.travis.wurstplusthree.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.command.Command;
import me.travis.wurstplusthree.hack.hacks.player.PlayerSpoofer;
import me.travis.wurstplusthree.util.ClientMessage;

/**
 * @author Madmegsox1
 * @since 28/04/2021
 * FIXED UR SHIT ¬ travis
 */

public class PlayerSpooferCommand extends Command {

    public PlayerSpooferCommand(){
        super("PlayerSpoof", "PlayerSpoofer", "PS");
    }

    @Override
    public void execute(String[] message) {
        if (message.length == 0){
            ClientMessage.sendErrorMessage("Enter a name dumbass!");
            return;
        }
        if (message.length == 1){
            if (message[0].isEmpty()){
                ClientMessage.sendErrorMessage("Enter a name dumbass!");
                return;
            }
            String name = message[0];
            PlayerSpoofer.INSTANCE.name = name;
            // reset skin
            PlayerSpoofer.INSTANCE.disable();
            PlayerSpoofer.INSTANCE.enable();
            // goods
            ClientMessage.sendMessage("Set skin to " + ChatFormatting.BOLD + name);
        }
    }
}
