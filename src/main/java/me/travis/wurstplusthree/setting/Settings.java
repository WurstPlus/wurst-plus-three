package me.travis.wurstplusthree.setting;

import me.travis.wurstplusthree.hack.Hack;

import java.util.ArrayList;
import java.util.List;

public class Settings {

    private final List<Setting> settings;

    public Settings() {
        this.settings = new ArrayList<>();
    }

    public void addSetting(Setting setting) {
        this.settings.add(setting);
    }

    public List<Setting> getSettings() {
        return this.settings;
    }

    public List<Setting> getSettingFromHack(Hack hack) {
        List<Setting> settings = new ArrayList<>();
        for (Setting setting : this.settings) {
            if(setting.getParent() == hack) {
                settings.add(setting);
            }
        }
        return settings;
    }

    public List<Setting> getSettingFromHack(String hack) {
        List<Setting> settings = new ArrayList<>();
        for (Setting setting : this.settings) {
            if(setting.getParent().getName().equalsIgnoreCase(hack)) {
                settings.add(setting);
            }
        }
        return settings;
    }

}
