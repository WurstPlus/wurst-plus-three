package me.travis.wurstplusthree.gui.component;

import me.travis.wurstplusthree.gui.WurstplusGuiNew;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.Setting;
import me.travis.wurstplusthree.util.Globals;

/**
 * @author Madmegsox1
 * @since 27/04/2021
 */

public abstract class Component implements Globals {
    private Hack hack;
    private Setting setting;
    public Component(Hack hack) {
        this.hack = hack;
        this.setting = null;
    }

    public Component(Setting s) {
        this.hack = null;
        this.setting = s;
    }

    public Setting getSetting() {
        return this.setting;
    }

    public Hack getHack() {
        return this.hack;
    }

    public void renderComponent(int mouseX, int mouseY, int x, int y) {
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    }

    public void keyTyped(char typedChar, int key) {
    }

    public int getHeight() {
        return WurstplusGuiNew.HEIGHT;
    }

    public HackButton getParent() {
        return null;
    }


    public void renderToolTip(int mouseX, int mouseY){
    }
}
