package me.travis.wurstplusthree.command;

import me.travis.wurstplusthree.command.commands.*;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.util.ClientMessage;
import me.travis.wurstplusthree.util.Globals;
import me.travis.wurstplusthree.util.ReflectionUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Commands implements Globals {

    public static String prefix = ".";
    private final List<Command> commands = new ArrayList<>();

    // TODO : SETTINGS COMMAND
    // TODO : CLEAR CHAT COMMAND
    public Commands() {

        try {
            ArrayList<Class<?>> modClasses = ReflectionUtil.getClassesForPackage("me.travis.wurstplusthree.command.commands");
            modClasses.spliterator().forEachRemaining(aClass -> {
                if(Command.class.isAssignableFrom(aClass)) {
                    try {
                        Command module = (Command) aClass.getConstructor().newInstance();
                        commands.add(module);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String[] removeElement(String[] input, int indexToDelete) {
        LinkedList<String> result = new LinkedList<>();
        for (int i = 0; i < input.length; ++i) {
            if (i == indexToDelete) continue;
            result.add(input[i]);
        }
        return result.toArray(input);
    }

    public void executeCommand(String command) {
        String[] split = command.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        String command1 = split[0];
        String args = command.substring(command1.length()).trim();
        for(Command c : commands){
            try {
                if (c.isName(command1)) {
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

    public List<Command> getCommands() {
        return this.commands;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        Commands.prefix = prefix;
    }

    public BurrowBlockCommand getBurrowCommand(){
        for(Command c : commands){
            if(c.isName("BurrowBlock")){
                return (BurrowBlockCommand) c;
            }
        }
        return null;
    }

}
