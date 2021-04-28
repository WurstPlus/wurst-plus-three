package me.travis.wurstplusthree.command.commands;

import me.travis.wurstplusthree.command.Command;
import me.travis.wurstplusthree.util.ClientMessage;

public class TestCommand extends Command {

    public TestCommand() {
        super("Test");
    }

    @Override
    public void execute(String[] message) {
        if (message.length == 0) {
            ClientMessage.sendErrorMessage("empty message");
            return;
        }
        for (String s : message) {
            ClientMessage.sendMessage(s);
        }
    }

}
