package me.travis.wurstplusthree.setting;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.ParentSetting;

import java.util.function.Predicate;

public class Setting<T> {

    private final String name;
    private final Hack parent;
    private final ParentSetting parentSetting;
    public T value;
    public Predicate<T> shown;
    public T defaultValue;


    public Setting(String name, T value, Hack parent) {
        this.name = name;
        this.value = value;
        this.defaultValue = value;
        this.parent = parent;
        this.parentSetting = null;
        WurstplusThree.SETTINGS.addSetting(this);
    }

    public Setting(String name, T value, ParentSetting parent) {
        this.name = name;
        this.value = value;
        this.defaultValue = value;
        this.parent = parent.getParent();
        this.parentSetting = parent;
        WurstplusThree.SETTINGS.addSetting(this);
    }

    public Setting(String name, T value, Hack parent, Predicate<T> shown) {
        this.name = name;
        this.value = value;
        this.defaultValue = value;
        this.parent = parent;
        this.parentSetting = null;
        this.shown = shown;
        WurstplusThree.SETTINGS.addSetting(this);
    }

    public Setting(String name, T value, ParentSetting parent, Predicate<T> shown) {
        this.name = name;
        this.value = value;
        this.defaultValue = value;
        this.parent = parent.getParent();
        this.parentSetting = parent;
        this.shown = shown;
        WurstplusThree.SETTINGS.addSetting(this);
    }

    public String getName() {
        return this.name;
    }

    public T getValue() {
        return this.value;
    }

    public String getType() {
    	return "";
    }

    public Hack getParent() {
        return this.parent;
    }

    public void setValue(T value) {
        this.value = value;
        if (this.getParent().isEnabled())
            this.getParent().onSettingChange();
    }

    public boolean isChild() {
        return parentSetting != null;
    }

    public boolean isShown() {
        boolean parent = parentSetting == null ? true : parentSetting.getValue();
        boolean shown =  this.shown == null ? true : this.shown.test(this.getValue());
        if (parent && shown) {
            return true;
        }
        return false;
    }
}
