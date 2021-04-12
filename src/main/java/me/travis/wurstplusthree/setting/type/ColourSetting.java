package me.travis.wurstplusthree.setting.type;

import com.lukflug.panelstudio.settings.ColorSetting;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.Setting;
import me.travis.wurstplusthree.util.elements.Colour;

import java.awt.*;

public class ColourSetting extends Setting<Colour> implements ColorSetting {

    private boolean rainbow;

    public ColourSetting(String name, Colour value, Hack parent) {
        super(name, value, parent);

        this.rainbow = rainbow;
    }

    @Override
    public Colour getValue() {
        if (rainbow) return Colour.fromHSB((System.currentTimeMillis() % (360 * 32)) / (360f * 32), 1, 1);
        else return super.getValue();
    }

    @Override
    public void setValue(Color value) {
        this.value = new Colour(value);
    }

    @Override
    public Color getColor() {
        return this.value;
    }

    @Override
    public boolean getRainbow() {
        return this.rainbow;
    }

    @Override
    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
    }

    @Override
    public String getType() {
        return "colour";
    }
}
