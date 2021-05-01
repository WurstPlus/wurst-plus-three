package me.travis.wurstplusthree.manager.fonts;

import me.travis.wurstplusthree.gui.font.CustomFont;
import me.travis.wurstplusthree.util.Globals;

import java.awt.*;

public class DonatorFont implements Globals {

    private final String fontName = "Tahoma";

    private final int smallSize = 15;
    private final int mediumSize = 19;
    private final int largeSize = 24;

    private final CustomFont smallFont = new CustomFont(new Font(fontName, Font.PLAIN, smallSize), true, false);
    private final CustomFont mediumFont = new CustomFont(new Font(fontName, Font.PLAIN, mediumSize), true, false);
    private final CustomFont largeFont = new CustomFont(new Font(fontName, Font.PLAIN, largeSize), true, false);

    public void drawSmallStringRainbow(String string, float x, float y, int colour) {
        this.smallFont.drawStringWithShadow(string, x, y, colour);
    }

    public void drawMediumStringRainbow(String string, float x, float y, int colour) {
        this.mediumFont.drawStringWithShadow(string, x, y, colour);
    }

    public void drawLargeStringRainbow(String string, float x, float y, int colour) {
        this.largeFont.drawStringWithShadow(string, x, y, colour);
    }

}
