package me.travis.wurstplusthree.setting.type;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.Setting;

import java.util.function.Predicate;

public class BooleanSetting extends Setting<Boolean> {

    public BooleanSetting(String name, Boolean value, Hack parent) {
        super(name, value, parent);
    }

    public BooleanSetting(String name, boolean value, Hack parent, Predicate<Boolean> shown) {
        super(name, value, parent, shown);
    }

    public void toggle() {
        value = !value;
    }

    public Boolean getValue() {
        return value;
    }

    public boolean isShown(){
        if(shown == null){
            return true;
        }
        return shown.test(this.getValue());
    }

    @Override
    public String getType() {
        return "boolean";
    }
}
