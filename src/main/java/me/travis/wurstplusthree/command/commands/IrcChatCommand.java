package me.travis.wurstplusthree.command.commands;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.command.Command;
import me.travis.wurstplusthree.util.ClientMessage;

/**
 * @author Madmegsox1
 * @since 30/05/2021
 */

public class IrcChatCommand extends Command {

    public IrcChatCommand(){
        super("irc");
    }
    @Override
    public void execute(String[] message) {
        if(message[0].equals("set")){
            switch (message[1]) {
                case "global":
                    WurstplusThree.CLIENT_HANDLING.newClient();
                    WurstplusThree.CHAT_HANDLING.setRunning(true);
                    WurstplusThree.CHAT_HANDLING.setToGlobal();
                    WurstplusThree.CHAT_HANDLING.start();
                    ClientMessage.sendIRCMessage("");
                    break;
                case "direct":
                case "dm":
                    if (message.length > 2) {
                        WurstplusThree.CLIENT_HANDLING.newClient();
                        WurstplusThree.CHAT_HANDLING.setRunning(true);
                        WurstplusThree.CHAT_HANDLING.setToDirect(message[2]);
                        WurstplusThree.CHAT_HANDLING.start();
                        ClientMessage.sendIRCMessage("");
                    }
                    break;
                case "server":
                    WurstplusThree.CHAT_HANDLING.setRunning(false);
                    WurstplusThree.CHAT_HANDLING.setToServer();
                    ClientMessage.sendIRCMessage("");
                    break;
            }
        }
    }
}
