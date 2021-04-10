package me.travis.wurstplusthree.hack.client;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.Colour;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;

public class Gui extends Hack {

    public static Gui INSTANCE = new Gui();

    public IntSetting opacity = new IntSetting("Opactiy", 150, 50, 255, this);
    public IntSetting scrollSpeed = new IntSetting("Scroll Speed", 10, 1, 20, this);
    public ColourSetting outlineColor = new ColourSetting("Outline", new Colour(255, 0, 0, 255), this);
    public ColourSetting enabledColor = new ColourSetting("Outline", new Colour(255, 0, 0, 255), this);
    public ColourSetting backgroundColor = new ColourSetting("Outline", new Colour(255, 165, 0, 255), this);
    public ColourSetting settingBackgroundColor = new ColourSetting("Outline", new Colour(255, 0, 0, 255), this);
    public ColourSetting fontColor = new ColourSetting("Outline", new Colour(255, 0, 0, 255), this);
    public IntSetting animationSpeed = new IntSetting("Animation Speed", 200, 0, 1000, this);
    public EnumSetting scrolling = new EnumSetting("Scrolling", "Screen", Arrays.asList("Screen", "Container"), this);

    public Gui() {
        super("GUI", "Displays a skidded GUI", Category.CLIENT, false, true);
        this.setBind(Keyboard.KEY_RSHIFT);
    }

    @Override
    public void onEnable() {
        WurstplusThree.GUI.enterGUI();
        this.disable();
    }
}
