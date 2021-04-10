package me.travis.wurstplusthree.command;

import me.travis.wurstplusthree.manager.ClientMessage;
import me.travis.wurstplusthree.util.Globals;

public abstract class Command implements Globals {

    protected String name;
    protected String[] commands;

    public Command(String name) {
        this.name = name;
        this.commands = new String[]{""};
    }

    public Command(String name, String[] commands) {
        this.name = name;
        this.commands = commands;
    }

    public abstract void execute(String[] message);

    public String getName() {
        return this.name;
    }

    public String[] getCommands() {
        return this.commands;
    }

}
