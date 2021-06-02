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

    // TODO : SETTINGS COMMAND
    // TODO : CLEAR CHAT COMMAND
    public Commands() {
        this.commands.add(new FriendCommand());
        this.commands.add(new EnemyCommand());
        this.commands.add(new PrefixCommand());
        this.commands.add(new TestCommand());
        this.commands.add(new ToggleCommand());
        this.commands.add(new BindCommand());
        this.commands.add(new PlayerSpooferCommand());
        this.commands.add(new HelpCommand());
        this.commands.add(new ListCommand());
        this.commands.add(new DrawnCommand());
        this.commands.add(new FontCommand());
        this.commands.add(new ReloadCapesCommand());
        this.commands.add(new ReloadCosmeticsCommand());
        this.commands.add(new NameMcCommand());
        this.commands.add(new BurrowBlockCommand());
        this.commands.add(new ConfigCommand());
        this.commands.add(new IrcChat());
        this.commands.add(new ClipBind());
        this.commands.add(new Debug());
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
        for (Command c : this.commands) {
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
