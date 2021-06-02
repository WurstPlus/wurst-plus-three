package me.travis.wurstplusthree.command.commands;

import me.travis.wurstplusthree.command.Command;
import me.travis.wurstplusthree.util.ClientMessage;
import me.travis.wurstplusthree.util.logview.Threads;

/**
 * @author Madmegsox1
 * @since 02/06/2021
 */

public class Debug extends Command {
    public Debug(){
        super("debug");
    }

    @Override
    public void execute(String[] message) {
        if(message[0].equals("logview")){
            Threads log = new Threads();
            log.start();
        }
        else {
            ClientMessage.sendErrorMessage(message[0] + " inst supported!");
        }
    }
}
