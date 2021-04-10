package me.travis.wurstplusthree.command.commands;

import ca.weblite.objc.Client;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.command.Command;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.manager.ClientMessage;
import org.lwjgl.input.Keyboard;

import java.util.Locale;

public class BindCommand extends Command {

    public BindCommand() {
        super("Bind");
    }

    @Override
    public void execute(String[] message) {
        if (message.length == 1) {
            ClientMessage.sendErrorMessage("please specify module");
        }
        String hackName = message[0];
        String keyName = message[1];
        Hack hack = WurstplusThree.HACKS.getHackByName(hackName);
        if (hack == null) {
            ClientMessage.sendErrorMessage("cannot find module by name '" + hackName + "'");
            return;
        }
        if (keyName == null) {
            ClientMessage.sendMessage(hack.getName() + " is bound to key '" + hack.getBindName() + "'");
            return;
        }
        int key = Keyboard.getKeyIndex(keyName.toUpperCase());
        if (key > 0 && key < 255) {
            hack.setBind(key);
            ClientMessage.sendMessage(hack.getName() + " has been bound to key '" + hack.getBindName() + "'");
            return;
        }
        ClientMessage.sendErrorMessage("unknown key");
    }

}
