package me.travis.wurstplusthree.setting.type;

import com.lukflug.panelstudio.settings.KeybindSetting;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.Setting;
import org.lwjgl.input.Keyboard;

/**
 * @author Madmegsox1
 * @since 30/04/2021
 */

public class KeySetting extends Setting<Integer> implements KeybindSetting {

    public KeySetting(String name, int value, Hack parent) {
        super(name, value, parent);
    }

    @Override
    public int getKey() {
        return this.value;
    }

    @Override
    public void setKey(int key) {
        this.value = key;
    }

    @Override
    public String getKeyName() {
        return Keyboard.getKeyName(this.getKey());
    }

    @Override
    public String getType() {
        return "key";
    }
}
