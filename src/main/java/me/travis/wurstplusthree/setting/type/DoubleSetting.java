package me.travis.wurstplusthree.setting.type;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.Feature;
import me.travis.wurstplusthree.setting.Setting;

import java.util.function.Predicate;

public class DoubleSetting extends Setting<Double> {

    private final double min;
    private final double max;
    private boolean open = false;

    public DoubleSetting(String name, Double value, Double min, Double max, Feature parent) {
        super(name, value, parent);
        this.min = min;
        this.max = max;
    }

    public DoubleSetting(String name, Double value, Double min, Double max, ParentSetting parent) {
        super(name, value, parent);
        this.min = min;
        this.max = max;
    }

    public DoubleSetting(String name, Double value, Double min, Double max, Feature parent, Predicate shown) {
        super(name, value, parent, shown);
        this.min = min;
        this.max = max;
    }

    public DoubleSetting(String name, Double value, Double min, Double max, ParentSetting parent, Predicate shown) {
        super(name, value, parent, shown);
        this.min = min;
        this.max = max;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
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

    public double getNumber() {
        return this.value;
    }

    public void setNumber(double value) {
        this.value = value;
        if (getParent() instanceof Hack) {
            if (((Hack) getParent()).isEnabled())
                ((Hack) getParent()).onSettingChange();
        }
    }

    public Float getAsFloat(){
        return (float) (double) this.value;
    }

    public double getMaximumValue() {
        return this.max;
    }

    public double getMinimumValue() {
        return this.min;
    }

    public int getPrecision() {
        return 2;
    }


    @Override
    public String getType() {
        return "double";
    }
}
