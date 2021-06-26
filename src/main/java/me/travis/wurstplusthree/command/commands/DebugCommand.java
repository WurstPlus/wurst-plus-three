package me.travis.wurstplusthree.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.command.Command;
import me.travis.wurstplusthree.event.events.TestEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.event.processor.EventPriority;
import me.travis.wurstplusthree.util.ClientMessage;
import me.travis.wurstplusthree.util.logview.Threads;

/**
 * @author Madmegsox1
 * @since 02/06/2021
 */

public class DebugCommand extends Command {

    public static DebugCommand INSTANCE;
    public boolean capes;

    public DebugCommand(){
        super("debug");
        INSTANCE = this;
        capes = true;
    }


    @Override
    public void execute(String[] message) {
        switch (message[0]) {
            case "logview":
                Threads log = new Threads();
                log.start();
                break;
            case "ram":
                Runtime runtime = Runtime.getRuntime();
                long maxMemory = runtime.maxMemory();
                long allocatedMemory = runtime.totalMemory();
                long freeMemory = runtime.freeMemory();
                ClientMessage.sendMessage(
                        "\n - MAX MEMORY: " + ChatFormatting.AQUA + maxMemory / 1024 / 1024 + " Mb" + ChatFormatting.RESET +
                                "\n - ALLOCATED MEMORY: " + ChatFormatting.AQUA + allocatedMemory / 1024 / 1024 + " Mb" + ChatFormatting.RESET +
                                "\n - FREE MEMORY: " + ChatFormatting.AQUA + freeMemory / 1024 / 1024 + " Mb" + ChatFormatting.RESET +
                                "\n - TOTAL FREE MEMORY: " + ChatFormatting.AQUA + ((freeMemory + (maxMemory - allocatedMemory)) / 1024 / 1024) + " Mb" + ChatFormatting.RESET);
                break;
            case "eventdelay":
                WurstplusThree.EVENT_PROCESSOR.addEventListener(this);
                TestEvent event = new TestEvent();
                WurstplusThree.EVENT_PROCESSOR.postEvent(event);
                WurstplusThree.EVENT_PROCESSOR.removeEventListener(this);
                break;
            case "hud":
                break;
            case "capes":
                capes = message[1].equals("true");
                ClientMessage.sendMessage("Capes Toggled");
                break;
            default:
                ClientMessage.sendErrorMessage(message[0] + " inst supported!");
                break;
        }
    }

    @CommitEvent(priority = EventPriority.HIGH)
    public void highEvent(TestEvent event){
        ClientMessage.sendMessage("\n - HIGH PRIORITY: " + ChatFormatting.AQUA +  (System.nanoTime() - event.startTime ) + ChatFormatting.RESET + " ns");
    }

    @CommitEvent(priority = EventPriority.NONE)
    public void normalEvent(TestEvent event){
        ClientMessage.sendMessage("\n - NORMAL PRIORITY: " + ChatFormatting.AQUA +  (System.nanoTime() - event.startTime) + ChatFormatting.RESET + " ns");
    }

    @CommitEvent(priority = EventPriority.LOW)
    public void lowEvent(TestEvent event){
        ClientMessage.sendMessage("\n - LOW PRIORITY: " + ChatFormatting.AQUA +  (System.nanoTime() - event.startTime) + ChatFormatting.RESET + " ns");
    }
}
