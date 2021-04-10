package me.travis.wurstplusthree.setting.type;

import com.lukflug.panelstudio.settings.NumberSetting;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.Setting;

public class DoubleSetting extends Setting<Double> implements NumberSetting {

    private final double min;
    private final double max;

    public DoubleSetting(String name, Double value, Double min, Double max, Hack parent) {
        super(name, value, parent);
        this.min = min;
        this.max = max;
    }

    public Double getValue() {
        return this.value;
    }

    public Double getMax() {
        return this.max;
    }

    public Double getMin() {
        return this.min;
    }

    @Override
    public double getNumber() {
        return this.value;
    }

    @Override
    public void setNumber(double value) {
        this.value = value;
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
        return 2;
    }
}
