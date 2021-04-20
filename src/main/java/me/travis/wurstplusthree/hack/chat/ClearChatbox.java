package me.travis.wurstplusthree.hack.chat;

import me.travis.wurstplusthree.hack.Hack;

public class ClearChatbox extends Hack {

    public static ClearChatbox INSTANCE;

    public ClearChatbox() {
        super("Clear Chatbox", "makes chat clear", Category.CHAT, false);
        INSTANCE = this;
    }

}