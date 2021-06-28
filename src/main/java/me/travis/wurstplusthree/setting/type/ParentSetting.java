package me.travis.wurstplusthree.setting.type;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.Setting;

import java.util.function.Predicate;

public class ParentSetting extends Setting<Boolean> {

    public ParentSetting(String name, Hack parent) {
        super(name, false, parent);
    }

    public void toggle() {
        value = !value;
    }

    public boolean isShown(){
        return true;
    }

    public Boolean getValue() {
        return value;
    }

    @Override
    public String getType() {
        return "boolean";
    }
}
