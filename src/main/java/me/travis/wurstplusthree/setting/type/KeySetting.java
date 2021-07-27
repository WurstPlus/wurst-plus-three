package me.travis.wurstplusthree.setting.type;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.Feature;
import me.travis.wurstplusthree.setting.Setting;
import org.lwjgl.input.Keyboard;

import java.util.function.Predicate;

/**
 * @author Madmegsox1
 * @since 30/04/2021
 */

public class KeySetting extends Setting<Integer> {

    public KeySetting(String name, int value, Feature parent) {
        super(name, value, parent);
    }

    public KeySetting(String name, int value, ParentSetting parent) {
        super(name, value, parent);
    }

    public KeySetting(String name, int value, Feature parent, Predicate shown) {
        super(name, value, parent, shown);
    }

    public KeySetting(String name, int value, ParentSetting parent, Predicate shown) {
        super(name, value, parent, shown);
    }

    public int getKey() {
        return this.value;
    }

    public void setKey(int key) {
        this.value = key;
        if (getParent() instanceof Hack) {
            if (((Hack) getParent()).isEnabled())
                ((Hack) getParent()).onSettingChange();
        }
    }

    public boolean isDown() {
        if (this.value <= 0) return false;
        return Keyboard.isKeyDown(value);
    }

    public String getKeyName() {
        return Keyboard.getKeyName(this.getKey());
    }

    @Override
    public String getType() {
        return "key";
    }
}
