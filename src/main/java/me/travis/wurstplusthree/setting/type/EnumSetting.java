package me.travis.wurstplusthree.setting.type;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.Setting;

import java.util.List;

public class EnumSetting extends Setting<String> implements com.lukflug.panelstudio.settings.EnumSetting {

    private final List<String> modes;

    public EnumSetting(String name, String value, List<String> modes, Hack parent) {
        super(name, value, parent);

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

    @Override
    public void increment() {
        value = modes.get((modes.indexOf(this.value) + 1) % modes.size());
    }

    @Override
    public String getValueName() {
        return this.value;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String getType() {
        return "enum";
    }
}
