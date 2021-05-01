package me.travis.wurstplusthree.setting.type;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.Setting;

public class BooleanSetting extends Setting<Boolean> {

    public BooleanSetting(String name, Boolean value, Hack parent) {
        super(name, value, parent);
    }

    public void toggle() {
        value = !value;
    }

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
