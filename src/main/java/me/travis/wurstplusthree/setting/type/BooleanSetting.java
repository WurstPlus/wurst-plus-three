package me.travis.wurstplusthree.setting.type;

import com.lukflug.panelstudio.settings.Toggleable;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.Setting;

public class BooleanSetting extends Setting<Boolean> implements Toggleable {

    public BooleanSetting(String name, Boolean value, Hack parent) {
        super(name, value, parent);
    }

    public void toggle() {
        value = !value;
    }

    @Override
    public boolean isOn() {
        return this.value;
    }

    public Boolean getValue() {
        return value;
    }

    @Override
    public String getType() {
        return "boolean";
    }
}
