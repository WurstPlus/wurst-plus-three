package me.travis.wurstplusthree.gui.hud.element.elements;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.Render2DEvent;
import me.travis.wurstplusthree.gui.hud.element.HudElement;
import me.travis.wurstplusthree.hack.hacks.client.HudEditor;
import me.travis.wurstplusthree.util.HudUtil;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

@HudElement.Element(name = "Friends", posX = 100, posY = 100)
public class HudFriends extends HudElement {

    int biggest = 0;
    int yVal = 0;

    @Override
    public int getHeight(){
        return yVal;
    }

    @Override
    public int getWidth(){
        return biggest;
    }

    @Override
    public void onRender2D(Render2DEvent event){
        yVal = WurstplusThree.GUI_FONT_MANAGER.getTextHeight();
        List<String> friends = new ArrayList<>();
        for (EntityPlayer player : mc.world.playerEntities) {
            if (WurstplusThree.FRIEND_MANAGER.isFriend(player.getName())) {
                friends.add(player.getName());
            }
        }
        int y = getY();

        if (friends.isEmpty()) {
            HudUtil.drawHudString(ChatFormatting.BOLD + "U got no friends", getX(), y, HudEditor.INSTANCE.fontColor.getValue().hashCode());
            biggest = WurstplusThree.GUI_FONT_MANAGER.getTextWidth("U got no friends");
        } else {
            HudUtil.drawHudString(ChatFormatting.BOLD + "the_fellas", getX(), y, HudEditor.INSTANCE.fontColor.getValue().hashCode());
            y += 12;
            int temp = 0;
            for (String friend : friends) {
                HudUtil.drawHudString(friend, getX(), y, HudEditor.INSTANCE.fontColor.getValue().hashCode());
                if(temp < WurstplusThree.GUI_FONT_MANAGER.getTextWidth(friend)){
                    temp = WurstplusThree.GUI_FONT_MANAGER.getTextWidth(friend);
                    if(temp < WurstplusThree.GUI_FONT_MANAGER.getTextWidth("the_fellas")){
                        temp = WurstplusThree.GUI_FONT_MANAGER.getTextWidth("the_fellas");
                    }
                }
                y += 12;
                yVal += 12;
            }
            biggest = temp;
        }
    }
}
