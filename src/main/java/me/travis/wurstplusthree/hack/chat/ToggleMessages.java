package me.travis.wurstplusthree.hack.chat;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;

@Hack.Registration(name = "ToggleMsgs", description = "Says in chat when you toggle something", category = Hack.Category.CHAT, isListening = false)
public class ToggleMessages extends Hack {

    public static ToggleMessages INSTANCE;

    public ToggleMessages() {
        INSTANCE = this;
    }

    public BooleanSetting compact = new BooleanSetting("Compact", true, this);
}
