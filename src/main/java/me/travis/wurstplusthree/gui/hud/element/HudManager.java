package me.travis.wurstplusthree.gui.hud.element;

import java.util.ArrayList;

/**
 * @author Madmegsox1
 * @since 17/06/2021
 */

public final class HudManager {
    ArrayList<HudElement> hudElements;

    public HudManager(){
        hudElements = new ArrayList<>();
    }

    public ArrayList<HudElement> getHudElements() {
        return hudElements;
    }
}
