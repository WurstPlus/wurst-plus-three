package me.travis.wurstplusthree.hack.client;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.elements.Colour;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;

@Hack.Registration(name = "Gui Old", description = "displays a skidded GUI", category = Hack.Category.CLIENT, isListening = true, bind = Keyboard.KEY_0)
public class GuiOld extends Hack {

    public static GuiOld INSTANCE;

    public IntSetting opacity = new IntSetting("Opactiy", 150, 50, 255, this);
    public IntSetting scrollSpeed = new IntSetting("Scroll Speed", 10, 1, 20, this);
    public ColourSetting outlineColor = new ColourSetting("Outline", new Colour(255, 255, 255, 255), this);
    public ColourSetting enabledColor = new ColourSetting("Enabled", new Colour(20, 220, 20, 255), this);
    public ColourSetting backgroundColor = new ColourSetting("Background", new Colour(255, 165, 0, 255), this);
    public ColourSetting settingBackgroundColor = new ColourSetting("Setting", new Colour(255, 165, 0, 255), this);
    public ColourSetting fontColor = new ColourSetting("Font Colour", new Colour(0, 0, 0, 255), this);
    public IntSetting animationSpeed = new IntSetting("Animation Speed", 200, 0, 1000, this);
    public EnumSetting scrolling = new EnumSetting("Scrolling", "Screen", Arrays.asList("Screen", "Container"), this);

    public GuiOld() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        WurstplusThree.GUI.enterGUI();
        this.disable();
    }
}
