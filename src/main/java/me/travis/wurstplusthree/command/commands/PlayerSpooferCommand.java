package me.travis.wurstplusthree.command.commands;

import me.travis.wurstplusthree.command.Command;
import me.travis.wurstplusthree.util.ClientMessage;

/**
 * @author Madmegsox1
 * @since 28/04/2021
 */

public class PlayerSpooferCommand extends Command {
    public PlayerSpooferCommand(){
        super("PlayerSpoof", "PS");
    }

    public static String name;

    @Override
    public void execute(String[] message) {
        if(message.length == 0){
            ClientMessage.sendErrorMessage("enter a name dumbass!");
            return;
        }if(message.length == 1){
            if(message[0].isEmpty()){
                ClientMessage.sendErrorMessage("enter a name dumbass!");
                return;
            }
            name = message[0];
            ClientMessage.sendMessage("Set skin to " + name);
        }
    }
}
