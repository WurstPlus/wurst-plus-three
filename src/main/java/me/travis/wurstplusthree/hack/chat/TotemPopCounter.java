package me.travis.wurstplusthree.hack.chat;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.util.ClientMessage;

@Hack.Registration(name = "Totem Pop Counter", description = "counts totems that people have popped", category = Hack.Category.CHAT, isListening = false)
public class TotemPopCounter extends Hack {

    @Override
    public void onUpdate() {
        if (nullCheck()) return;
        if (!WurstplusThree.POP_MANAGER.toAnnouce.isEmpty()) {
            try {
                for (String string : WurstplusThree.POP_MANAGER.toAnnouce) {
                    ClientMessage.sendOverwriteClientMessage(string);
                }
                WurstplusThree.POP_MANAGER.toAnnouce.clear();
            } catch (Exception e) {
                //empty catchblock goo brrrrrrrrr
            }
        }
    }
}
