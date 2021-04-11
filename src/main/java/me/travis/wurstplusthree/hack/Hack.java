package me.travis.wurstplusthree.hack;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.Render2DEvent;
import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.manager.ClientMessage;
import me.travis.wurstplusthree.setting.Setting;
import me.travis.wurstplusthree.util.Globals;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class Hack implements Globals {

    private final String name;
    private final String description;
    private String displayInfo;
    private final Category category;
    private int bind;
    private boolean isEnabled;
    private boolean hidden;
    /**
     * -1 = not
     * 0 = always
     * 1 = yes
     */
    private int isListening;

    public Hack(String name, String desc, Category cat, boolean hidden, boolean shouldAlwaysListen) {
        this.name = name;
        this.description = desc;
        this.category = cat;
        this.isListening = (shouldAlwaysListen ? 0 : 1);
        this.hidden = hidden;
        this.bind = Keyboard.KEY_NONE;
        this.isEnabled = false;
        this.displayInfo = "";
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onToggle() {
    }

    public void onLoad() {
    }

    public void onTick() {
    }

    public void onLogin() {
    }

    public void onLogout() {
    }

    public void onUpdate() {
    }

    public void onRender2D(Render2DEvent event) {
    }

    public void onRender3D(Render3DEvent event) {
    }

    public void onUnload() {
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void stopListening() {
        this.isListening = -1;
    }

    public void enable() {
        this.isEnabled = true;
        this.onEnable();
        if (this.isEnabled() && this.isListening()) {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    public void disable() {
        if (this.isListening != 0) {
            MinecraftForge.EVENT_BUS.unregister(this);
        }
        this.isEnabled = false;
        this.onDisable();
    }

    public void toggle() {
        if (this.isEnabled()) {
            this.disable();
        } else {
            this.enable();
        }
        ClientMessage.sendToggleMessage(this);
    }

    public boolean isListening() {
        return isListening >= -1;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getDisplayInfo() {
        return this.displayInfo;
    }

    public int getBind() {
        return this.bind;
    }

    public String getBindName() {
        return Keyboard.getKeyName(this.bind);
    }

    public void setBind(int bind) {
        this.bind = bind;
    }

    public Category getCategory() {
        return this.category;
    }

    public List<Setting> getSettings() {
        List<Setting> settings = new ArrayList<>();
        for (Setting setting : WurstplusThree.SETTINGS.getSettings()) {
            if (setting.getParent() == this) {
                settings.add(setting);
            }
        }
        return settings;
    }

    public enum Category {
        CHAT("Chat"),
        COMBAT("Combat"),
        MISC("Render"),
        RENDER("Misc"),
        PLAYER("Player"),
        CLIENT("Client");

        private final String name;

        Category(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

}
