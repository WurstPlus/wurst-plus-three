package me.travis.wurstplusthree.command.commands;

import me.travis.wurstplusthree.command.Command;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

/**
 * @author TuxIsCool
 * @since 02/05/2021
 */

public class NameMcCommand extends Command {

    public NameMcCommand(){
        super("Namemc", "nmc");
    }


    @Override
    public void execute(String[] message) {
        String name = message[0];
        try {
            Desktop.getDesktop().browse(URI.create("https://namemc.com/search?q=" + name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
