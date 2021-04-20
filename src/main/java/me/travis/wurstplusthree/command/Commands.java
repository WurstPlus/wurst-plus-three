package me.travis.wurstplusthree.command;

import me.travis.wurstplusthree.command.commands.*;
import me.travis.wurstplusthree.util.ClientMessage;
import me.travis.wurstplusthree.util.Globals;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Commands implements Globals {

    public static String prefix = ".";
    private final List<Command> commands = new ArrayList<>();

    public Commands() {
        this.commands.add(new FriendCommand());
        this.commands.add(new EnemyCommand());
        this.commands.add(new PrefixCommand());
    }

    public static String[] removeElement(String[] input, int indexToDelete) {
        LinkedList<String> result = new LinkedList<>();
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
        String[] split = command.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        String command1 = split[0];
        String args = command.substring(command1.length()).trim();
        for (Command c : this.commands) {
            try {
                if (c.getName().equalsIgnoreCase(command1)) {
                    c.execute(args.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"));
                    return;
                }
            } catch (Exception e) {
                ClientMessage.sendErrorMessage("fucked it while doing command");
                e.printStackTrace();
                return;
            }
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
