package me.travis.wurstplusthree.setting.type;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.Setting;
import me.travis.wurstplusthree.util.elements.Colour;

import java.awt.*;
import java.util.function.Predicate;

public class ColourSetting extends Setting<Colour> {

    private boolean rainbow;

    public ColourSetting(String name, Colour value, Hack parent) {
        super(name, value, parent);
    }

    public ColourSetting(String name, Colour value, ParentSetting parent) {
        super(name, value, parent);
    }

    public ColourSetting(String name, Colour value, Hack parent, Predicate shown) {
        super(name, value, parent, shown);
    }

    public ColourSetting(String name, Colour value, ParentSetting parent, Predicate shown) {
        super(name, value, parent, shown);
    }

    @Override
    public Colour getValue() {
        this.doRainBow();
        return this.value;
    }

    private void doRainBow() {
        if (rainbow) {
            Color c = Colour.fromHSB((System.currentTimeMillis() % (360 * 32)) / (360f * 32), value.getSaturation(), value.getBrightness());
            setValue(new Colour(c.getRed(), c.getGreen(), c.getBlue(), value.getAlpha()));
        }
    }


    public void setValue(Color value) {
        this.value = new Colour(value);
        if (this.getParent().isEnabled())
          this.getParent().onSettingChange();
    }

    public void setValue(int red, int green, int blue, int alpha) {
        this.value = new Colour(red, green, blue, alpha);
        if (this.getParent().isEnabled())
            this.getParent().onSettingChange();
    }

    public Color getColor() {
        return this.value;
    }

    public boolean getRainbow() {
        return this.rainbow;
    }

    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
        if (this.getParent().isEnabled())
          this.getParent().onSettingChange();
    }

    @Override
    public String getType() {
        return "colour";
    }
}
