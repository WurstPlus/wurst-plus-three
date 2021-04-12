package me.travis.wurstplusthree.event.events;

import me.travis.wurstplusthree.event.Event;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class ClientEvent extends Event {
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

