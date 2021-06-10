package me.travis.wurstplusthree.setting.type;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.Setting;

import java.util.List;
import java.util.function.Predicate;

public class EnumSetting extends Setting<String> {

    private final List<String> modes;

    public EnumSetting(String name, String value, List<String> modes, Hack parent) {
        super(name, value, parent);

        this.modes = modes;
    }

    public EnumSetting(String name, String value, List<String> modes, Hack parent, Predicate<String> shown) {
        super(name, value, parent, shown);

        this.modes = modes;
    }

    public List<String> getModes() {
        return this.modes;
    }

    public void setValue(String value) {
        this.value = (this.modes.contains(value) ? value : this.value);
    }

    public boolean is(String name) {
        return name.equalsIgnoreCase(this.getValue());
    }

    public void increment() {
        value = modes.get((modes.indexOf(this.value) + 1) % modes.size());
    }

    public String getValue() {
        return this.value;
    }

    public boolean isShown(){
        if(shown == null){
            return true;
        }
        return shown.test(this.getValue());
    }

    @Override
    public String getType() {
        return "enum";
    }
}
