package me.travis.wurstplusthree.hack.chat;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.util.ClientMessage;

public class TotemPopCounter extends Hack {

    public TotemPopCounter() {
        super("Totem Pop Counter", "counts totems that people have popped", Category.CHAT, false);
    }

    @Override
    public void onUpdate() {
        if (nullCheck()) return;
        if (!WurstplusThree.POP_MANAGER.toAnnouce.isEmpty()) {
            for (String string : WurstplusThree.POP_MANAGER.toAnnouce) {
                ClientMessage.sendOverwriteClientMessage(string);
            }
            WurstplusThree.POP_MANAGER.toAnnouce.clear();
        }
    }
}
