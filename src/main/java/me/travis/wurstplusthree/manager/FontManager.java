package me.travis.wurstplusthree.manager;

import me.travis.wurstplusthree.gui.Rainbow;
import me.travis.wurstplusthree.gui.font.CustomFont;
import me.travis.wurstplusthree.util.Globals;
import me.travis.wurstplusthree.util.Maths;

import java.awt.*;

public class FontManager implements Globals {

    private CustomFont customFont = new CustomFont(new Font("Tahoma", 1, 21), true, false);

    public void drawStringWithShadow(String string, float x, float y, int colour) {
        this.drawString(string, x, y, colour, true);
    }

    public float drawString(String string, float x, float y, int colour, boolean shadow) {
        if (shadow) {
            return this.customFont.drawStringWithShadow(string, x, y, colour);
        } else {
            return this.customFont.drawString(string, x, y, colour);
        }
    }

    public float drawStringRainbow(String string, float x, float y, boolean shadow) {
        if (shadow) {
            return this.customFont.drawStringWithShadow(string, x, y, Rainbow.rgb);
        } else {
            return this.customFont.drawString(string, x, y, Rainbow.rgb);
        }
    }

    public int getTextHeight() {
        return this.customFont.getStringHeight();
    }

    public int getTextWidth(String string) {
        return this.customFont.getStringWidth(string);
    }

}
