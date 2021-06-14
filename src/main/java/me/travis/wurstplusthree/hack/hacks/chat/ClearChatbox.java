package me.travis.wurstplusthree.hack.hacks.chat;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.HackPriority;

@Hack.Registration(name = "Clear Chatbox", description = "makes the chatbox clear", category = Hack.Category.CHAT, priority = HackPriority.Lowest)
public class ClearChatbox extends Hack {

    public static ClearChatbox INSTANCE;

    public ClearChatbox() {
        INSTANCE = this;
    }

}