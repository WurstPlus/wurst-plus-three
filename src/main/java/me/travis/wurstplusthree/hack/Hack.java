package me.travis.wurstplusthree.hack;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.Render2DEvent;
import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.setting.Setting;
import me.travis.wurstplusthree.util.ClientMessage;
import me.travis.wurstplusthree.util.Globals;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

public class Hack implements Globals {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Registration {
        String name();
        String description();
        Category category();
        boolean isListening();
        int bind() default Keyboard.KEY_NONE;
        boolean enabled() default false;
        boolean shown() default true;
        boolean hold() default false;
    }

    private Registration getMod(){
        return getClass().getAnnotation(Registration.class);
    }

    private final String name = getMod().name();
    private final String description = getMod().description();
    private final Category category = getMod().category();
    private int bind = getMod().bind();
    private boolean hold = getMod().hold();
    private boolean shown = getMod().shown();
    private boolean isEnabled = getMod().enabled();
    private int isListening = (getMod().isListening() ? 0 : 1);

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

    public boolean isHold() {return this.hold;}

    public void toggleHold() {this.hold = !this.hold;}

    public void setHold(boolean hold) {
        this.hold = hold;
    }

    public void stopListening() {
        this.isListening = -1;
    }

    public void enable() {
        this.isEnabled = true;
        this.onEnable();
        if (this.isEnabled() && this.isListening()) {
            MinecraftForge.EVENT_BUS.register(this);
            WurstplusThree.EVENT_PROCESSOR.addEventListener(this);
        }
        if(this.shown) ClientMessage.sendToggleMessage(this, true);
    }

    public void disable() {
        if (this.isListening != 0) {
            MinecraftForge.EVENT_BUS.unregister(this);
            WurstplusThree.EVENT_PROCESSOR.removeEventListener(this);
        }
        this.isEnabled = false;
        this.onDisable();
        if(this.shown) ClientMessage.sendToggleMessage(this, false);
    }

    public void toggle() {
        if (this.isEnabled()) {
            this.disable();
        } else {
            this.enable();
        }
    }

    public void setEnabled(boolean enabled) {
        if (enabled && !this.isEnabled()) {
            this.enable();
        }
        if (!enabled && this.isEnabled()) {
            this.disable();
        }
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
        return null;
    }

    public int getBind() {
        return this.bind;
    }

    public boolean getShown() {
        return this.shown;
    }

    public String getBindName() {
        return Keyboard.getKeyName(this.bind);
    }

    public void setBind(int bind) {
        this.bind = bind;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
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

    public String getFullArrayString() {
        return this.name + (this.getDisplayInfo() != null ? ChatFormatting.GOLD + "[" + this.getDisplayInfo().toUpperCase() + "]" : "");
    }

    public Setting getSettingByName(String name) {
        for (Setting setting : this.getSettings()) {
            if (setting.getName().equalsIgnoreCase(name)) {
                return setting;
            }
        }
        return null;
    }

    public boolean isShown() {
        return this.shown;
    }

    public enum Category {
        CHAT("Chat"),
        COMBAT("Combat"),
        MISC("Misc"),
        RENDER("Render"),
        PLAYER("Player"),
        CLIENT("Client"),
        HIDDEN("Hidden");

        private final String name;

        Category(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

}
