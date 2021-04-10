package me.travis.wurstplusthree.command;

import me.travis.wurstplusthree.command.commands.BindCommand;
import me.travis.wurstplusthree.command.commands.ListCommand;
import me.travis.wurstplusthree.command.commands.PrefixCommand;
import me.travis.wurstplusthree.command.commands.ToggleCommand;
import me.travis.wurstplusthree.manager.ClientMessage;
import me.travis.wurstplusthree.util.Globals;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Commands implements Globals {

    private String prefix = ".";
    private final List<Command> commands = new ArrayList<>();

    public Commands() {
        this.commands.add(new PrefixCommand());
        this.commands.add(new ListCommand());
        this.commands.add(new ToggleCommand());
        this.commands.add(new BindCommand());
    }

    public static String[] removeElement(String[] input, int indexToDelete) {
        LinkedList<String> result = new LinkedList<String>();
        for (int i = 0; i < input.length; ++i) {
            if (i == indexToDelete) continue;
            result.add(input[i]);
        }
        return result.toArray(input);
    }

    private static String strip(String str, String key) {
        if (str.startsWith(key) && str.endsWith(key)) {
            return str.substring(key.length(), str.length() - key.length());
        }
        return str;
    }

    public void executeCommand(String command) {
        String[] parts = command.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        String name = parts[0].substring(1);
        String[] args = this.removeElement(parts, 0);
        for (int i = 0; i < args.length; ++i) {
            if (args[i] == null) continue;
            args[i] = this.strip(args[i], "\"");
        }
        for (Command c : this.commands) {
            if (!c.getName().equalsIgnoreCase(name)) continue;
            c.execute(parts);
            return;
        }
        ClientMessage.sendErrorMessage("unknown command");
    }

    public Command getCommandByName(String name) {
        for (Command command : this.commands) {
            if (!command.getName().equals(name)) continue;
            return command;
        }
        return null;
    }

    public List<Command> getCommands() {
        return this.commands;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

}
