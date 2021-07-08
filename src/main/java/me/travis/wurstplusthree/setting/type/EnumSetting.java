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

    public EnumSetting(String name, String value, List<String> modes, ParentSetting parent) {
        super(name, value, parent);

        this.modes = modes;
    }

    public EnumSetting(String name, String value, List<String> modes, Hack parent, Predicate shown) {
        super(name, value, parent, shown);

        this.modes = modes;
    }

    public EnumSetting(String name, String value, List<String> modes, ParentSetting parent, Predicate shown) {
        super(name, value, parent, shown);

        this.modes = modes;
    }

    public List<String> getModes() {
        return this.modes;
    }

    public void setValue(String value) {
        this.value = (this.modes.contains(value) ? value : this.value);
        if (this.getParent().isEnabled())
            this.getParent().onSettingChange();
    }

    public boolean is(String name) {
        return name.equalsIgnoreCase(this.getValue());
    }

    public String getValue() {
        return this.value;
    }


    @Override
    public String getType() {
        return "enum";
    }
}
