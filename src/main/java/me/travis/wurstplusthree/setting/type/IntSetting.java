package me.travis.wurstplusthree.setting.type;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.Setting;

import java.util.function.Predicate;

public class IntSetting extends Setting<Integer> {

    private final int min;
    private final int max;

    public IntSetting(String name, int value, int min, int max, Hack parent) {
        super(name, value, parent);

        this.min = min;
        this.max = max;
    }

    public IntSetting(String name, int value, int min, int max, ParentSetting parent) {
        super(name, value, parent);

        this.min = min;
        this.max = max;
    }

    public IntSetting(String name, int value, int min, int max, Hack parent, Predicate shown) {
        super(name, value, parent, shown);

        this.min = min;
        this.max = max;
    }

    public IntSetting(String name, int value, int min, int max, ParentSetting parent, Predicate shown) {
        super(name, value, parent, shown);

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

    public double getNumber() {
        return this.value;
    }

    public void setNumber(double value) {
        this.value = Math.toIntExact(Math.round(value));
        if (this.getParent().isEnabled())
            this.getParent().onSettingChange();
    }

    public double getMaximumValue() {
        return this.max;
    }

    public double getMinimumValue() {
        return this.min;
    }

    public int getPrecision() {
        return 0;
    }


    @Override
    public String getType() {
        return "int";
    }
}
