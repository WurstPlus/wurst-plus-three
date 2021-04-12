package me.travis.wurstplusthree.setting.type;

import com.lukflug.panelstudio.settings.NumberSetting;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.Setting;

public class IntSetting extends Setting<Integer> implements NumberSetting {

    private final int min;
    private final int max;

    public IntSetting(String name, int value, int min, int max, Hack parent) {
        super(name, value, parent);

        this.min = min;
        this.max = max;
    }

    public Integer getValue() {
        return this.value;
    }

    public int getMax() {
        return this.max;
    }

    public int getMin() {
        return this.min;
    }

    @Override
    public double getNumber() {
        return this.value;
    }

    @Override
    public void setNumber(double value) {
        this.value = Math.toIntExact(Math.round(value));
    }

    @Override
    public double getMaximumValue() {
        return this.max;
    }

    @Override
    public double getMinimumValue() {
        return this.min;
    }

    @Override
    public int getPrecision() {
        return 0;
    }

    @Override
    public String getType() {
        return "int";
    }
}
