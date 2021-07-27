package me.travis.wurstplusthree.setting;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hud.HudComponent;
import me.travis.wurstplusthree.setting.type.ParentSetting;

import java.util.function.Predicate;

public class Setting<T> {

    private final String name;
    private final Feature parent;
    private final ParentSetting parentSetting;
    public T value;
    public Predicate<T> shown;
    public T defaultValue;


    public Setting(String name, T value, Feature parent) {
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

    public Setting(String name, T value, Feature parent, Predicate<T> shown) {
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

    public Feature getParent() {
        return this.parent;
    }

    public void setValue(T value) {
        this.value = value;
        if (parent instanceof Hack) {
            if (((Hack) parent).isEnabled())
                ((Hack) parent).onSettingChange();
        }
        if (parent instanceof HudComponent) {
            //todo moment
        }
    }

    public ParentSetting getParentSetting() {
        return parentSetting;
    }

    public boolean isChild() {
        return parentSetting != null;
    }

    public boolean isShown() {
        boolean shown =  this.shown == null ? true : this.shown.test(this.getValue());
        if (shown) {
            return true;
        }
        return false;
    }
}
