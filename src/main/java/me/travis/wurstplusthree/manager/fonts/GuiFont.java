package me.travis.wurstplusthree.manager.fonts;

import me.travis.wurstplusthree.gui.Rainbow;
import me.travis.wurstplusthree.gui.font.CustomFont;
import me.travis.wurstplusthree.util.Globals;

import java.awt.*;

public class GuiFont implements Globals {

    private CustomFont menuFont = new CustomFont(new Font("Tahoma", 0, 16), true, false);
    private CustomFont headerFont = new CustomFont(new Font("Tahoma", 1, 41), true, false);

    public void setMenuFontSize(int size) {
        menuFont = new CustomFont(new Font("Tahoma", 0, size), true, false);
    }

    public void drawStringWithShadow(String string, float x, float y, int colour) {
        this.drawString(string, x, y, colour, true);
    }

    public float drawStringBig(String string, float x, float y, int colour, boolean shadow) {
        if (shadow) {
            return this.headerFont.drawStringWithShadow(string, x, y, colour);
        } else {
            return this.headerFont.drawString(string, x, y, colour);
        }
    }

    public float drawString(String string, float x, float y, int colour, boolean shadow) {
        if (shadow) {
            return this.menuFont.drawStringWithShadow(string, x, y, colour);
        } else {
            return this.menuFont.drawString(string, x, y, colour);
        }
    }

    public float drawStringRainbow(String string, float x, float y, boolean shadow) {
        Rainbow.updateRainbow();
        if (shadow) {
            return this.menuFont.drawStringWithShadow(string, x, y, Rainbow.rgb);
        } else {
            return this.menuFont.drawString(string, x, y, Rainbow.rgb);
        }
    }

    public int getTextHeight() {
        return this.menuFont.getStringHeight();
    }

    public int getTextWidth(String string) {
        return this.menuFont.getStringWidth(string);
    }

}
