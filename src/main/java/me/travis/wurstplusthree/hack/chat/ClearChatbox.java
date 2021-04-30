package me.travis.wurstplusthree.hack.chat;

import me.travis.wurstplusthree.hack.Hack;

@Hack.Registration(name = "Clear Chatbox", description = "makes the chatbox clear", category = Hack.Category.CHAT, isListening = false)
public class ClearChatbox extends Hack {

    public static ClearChatbox INSTANCE;

    public ClearChatbox() {
        INSTANCE = this;
    }

}