package me.travis.wurstplusthree.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.command.Command;
import me.travis.wurstplusthree.util.ClientMessage;
import me.travis.wurstplusthree.util.elements.WurstplusPlayer;

public class FriendCommand extends Command {

    public FriendCommand() {
        super("Friend", "F");
    }

    @Override
    public void execute(String[] message) {
        if (message.length == 0) {
            ClientMessage.sendErrorMessage("Need more info than that mate");
            return;
        }
        if (message.length == 1) {
            if (message[0].equalsIgnoreCase("list")) {
                if (WurstplusThree.FRIEND_MANAGER.hasFriends()) {
                    ClientMessage.sendMessage(ChatFormatting.BOLD + "Listing friends");
                    for (WurstplusPlayer player : WurstplusThree.FRIEND_MANAGER.getFriends()) {
                        ClientMessage.sendMessage(player.getName());
                    }
                } else {
                    ClientMessage.sendMessage("u got no friends :(");
                }
            } else if (message[0].equalsIgnoreCase("clear")) {
                WurstplusThree.FRIEND_MANAGER.clear();
                ClientMessage.sendMessage("Cleared friends list");
            } else {
                ClientMessage.sendErrorMessage("Friend <add/del/list/clear>");
            }
            return;
        }
        String word1 = message[0];
        String word2 = message[1];
        if (word1 == null) return;
        switch (word1) {
            case "add":
                if (word2 == null) {
                    ClientMessage.sendErrorMessage("need name");
                    return;
                }
                WurstplusThree.FRIEND_MANAGER.addFriend(word2);
                ClientMessage.sendMessage(ChatFormatting.GREEN + word2 + ChatFormatting.RESET + " is now your friend");
                break;
            case "del":
                WurstplusThree.FRIEND_MANAGER.removeFriend(word2);
                ClientMessage.sendMessage(ChatFormatting.RED + word2 + ChatFormatting.RESET + " is no longer your friend");
                if (word2 == null) {
                    ClientMessage.sendErrorMessage("need name");
                    return;
                }
                break;
            default:
                ClientMessage.sendErrorMessage("friend <add/del/list/clear>");
        }
    }
}
