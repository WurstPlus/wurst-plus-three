package me.travis.wurstplusthree.hack.client;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.elements.Colour;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;

/**
 * @author Madmegsox1
 * @since 29/04/2021
 */

@Hack.Registration(name = "Gui", description = "swag custom gui", category = Hack.Category.CLIENT, isListening = true, bind = Keyboard.KEY_RSHIFT)
public class Gui extends Hack {
    
    public static Gui INSTANCE;

    public ColourSetting headButtonColor = new ColourSetting("Head Button", new Colour(255, 150, 90, 255), this);
    public ColourSetting buttonColor = new ColourSetting("Button", new Colour(224, 156, 96, 255), this);
    public ColourSetting fontColor = new ColourSetting("Font", new Colour(255,255,255, 255), this);
    public IntSetting rainbowDelay = new IntSetting("RainbowDelay", 100, 0, 5000, this);
    public EnumSetting type = new EnumSetting("Type", "None", Arrays.asList("None", "Rainbow", "Sin"), this);
    public EnumSetting SinMode = new EnumSetting("Sine Mode", "Special", Arrays.asList("Special", "Hue", "Saturation", "Brightness"),this);
    public IntSetting scrollSpeed = new IntSetting("ScrollSpeed", 15, 1, 100, this);
    public BooleanSetting blur = new BooleanSetting("Blur", true, this);
    public BooleanSetting gradient = new BooleanSetting("Gradient", false, this);
    public ColourSetting gradientStartColor = new ColourSetting("GradientStartColor", new Colour(255, 122, 5, 100), this);
    public ColourSetting gradientEndColor = new ColourSetting("GradientEndColor", new Colour(255, 122, 5, 100), this);
    public BooleanSetting animation = new BooleanSetting("Animation", true, this);
    public IntSetting animationStages = new IntSetting("AnimationStages", 250, 1, 1000, this);
    public EnumSetting arrowType = new EnumSetting("ArrowType", "Off", Arrays.asList("Off", "Type1", "Type2"), this);
    public IntSetting mouseDelay = new IntSetting("MouseDelay", 250, 100, 300, this);
    public BooleanSetting customScreen = new BooleanSetting("CustomMenu", true, this);
    public BooleanSetting customFont = new BooleanSetting("CustomFont", true, this);

    public Gui(){
        INSTANCE = this;
    }

    @Override
    public void onEnable(){
        mc.displayGuiScreen(WurstplusThree.GUI2);
        this.disable();
    }
}
