package me.travis.wurstplusthree.manager.fonts;

import me.travis.wurstplusthree.gui.font.CustomFont;
import me.travis.wurstplusthree.util.Globals;

import java.awt.*;

public class MenuFont implements Globals {

    private final CustomFont menuFont = new CustomFont(new Font("Tahoma", Font.BOLD, 21), true, false);
    private final CustomFont headerFont = new CustomFont(new Font("Tahoma", Font.BOLD, 41), true, false);

    public void drawStringWithShadow(String string, float x, float y, int colour) {
        this.drawString(string, x, y, colour, true);
    }

    public float drawString(String string, float x, float y, int colour, boolean shadow) {
        if (shadow) {
            return this.menuFont.drawStringWithShadow(string, x, y, colour);
        } else {
            return this.menuFont.drawString(string, x, y, colour);
        }
    }

    public float drawStringBig(String string, float x, float y, int colour, boolean shadow) {
        if (shadow) {
            return this.headerFont.drawStringWithShadow(string, x, y, colour);
        } else {
            return this.headerFont.drawString(string, x, y, colour);
        }
    }

    public int getTextHeight() {
        return this.menuFont.getStringHeight();
    }

    public int getTextWidth(String string) {
        return this.menuFont.getStringWidth(string);
    }

}
