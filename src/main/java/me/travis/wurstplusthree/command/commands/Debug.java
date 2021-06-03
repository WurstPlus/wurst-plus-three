package me.travis.wurstplusthree.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
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
        else if(message[0].equals("ram")){
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long allocatedMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            ClientMessage.sendMessage(
                    "\n - MAX MEMORY: "+ ChatFormatting.AQUA + maxMemory/1024/1024 + " Mb"+ ChatFormatting.RESET +
                    "\n - ALLOCATED MEMORY: "+ ChatFormatting.AQUA + allocatedMemory/1024/1024 + " Mb"+ ChatFormatting.RESET +
                    "\n - FREE MEMORY: " + ChatFormatting.AQUA + freeMemory/1024/1024 +" Mb"    + ChatFormatting.RESET+
                    "\n - TOTAL FREE MEMORY: " + ChatFormatting.AQUA  + ((freeMemory + (maxMemory - allocatedMemory))/1024/1024)+ " Mb" + ChatFormatting.RESET);
        }
        else {
            ClientMessage.sendErrorMessage(message[0] + " inst supported!");
        }
    }
}
