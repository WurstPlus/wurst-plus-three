package me.travis.wurstplusthree.event.events;

import me.travis.wurstplusthree.event.EventStage;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.Setting;


public class ClientEvent extends EventStage {
    private Hack hack;
    private Setting setting;

    public ClientEvent(int stage, Hack hack) {
        super(stage);
        this.hack = hack;
    }

    public ClientEvent(Setting setting) {
        super(2);
        this.setting = setting;
    }

    public Hack getFeature() {
        return this.hack;
    }

    public Setting getSetting() {
        return this.setting;
    }
}

