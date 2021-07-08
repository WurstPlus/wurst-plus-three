package me.travis.wurstplusthree.setting.type;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.Setting;

import java.util.function.Predicate;

public class BooleanSetting extends Setting<Boolean> {

    public BooleanSetting(String name, Boolean value, Hack parent) {
        super(name, value, parent);
    }

    public BooleanSetting(String name, Boolean value, ParentSetting parent) {
        super(name, value, parent);
    }

    public BooleanSetting(String name, Boolean value, Hack parent, Predicate shown) {
        super(name, value, parent, shown);
    }

    public BooleanSetting(String name, Boolean value, ParentSetting parent, Predicate shown) {
        super(name, value, parent, shown);
    }

    public void toggle() {
        value = !value;
        if (this.getParent().isEnabled())
            this.getParent().onSettingChange();
    }

    public Boolean getValue() {
        return value;
    }

    @Override
    public String getType() {
        return "boolean";
    }
}
