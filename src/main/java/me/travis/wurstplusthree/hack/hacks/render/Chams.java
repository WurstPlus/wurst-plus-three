package me.travis.wurstplusthree.hack.hacks.render;

import me.travis.wurstplusthree.event.events.RenderLivingEntityEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.event.processor.EventPriority;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.Setting;
import me.travis.wurstplusthree.setting.type.*;
import me.travis.wurstplusthree.util.ColorUtil;
import me.travis.wurstplusthree.util.EntityUtil;
import me.travis.wurstplusthree.util.elements.Colour;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

//made by linus touch tips
import java.awt.*;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.GL_ALL_ATTRIB_BITS;
import static org.lwjgl.opengl.GL11.glPushAttrib;

@Hack.Registration(name = "Chams", description = "draws people as colours/through walls", category = Hack.Category.RENDER, isListening = false)
public class Chams extends Hack {
    public static Chams INSTANCE;
    public Chams() {
        INSTANCE = this;
    }
    public EnumSetting mode = new EnumSetting("Mode", "Wire", Arrays.asList("Model", "Wire", "Shine"), this);
    public DoubleSetting width = new DoubleSetting("Width", 2.0, 0.0, 5.0, this, v -> mode.is("Wire"));
    public BooleanSetting texture = new BooleanSetting("Texture", false, this);
    public BooleanSetting lighting = new BooleanSetting("Lighting", false, this);
    public BooleanSetting blend = new BooleanSetting("Blend", false, this);
    public BooleanSetting transparent = new BooleanSetting("Transparent", false, this);
    public BooleanSetting depth = new BooleanSetting("Depth", false, this);
    public BooleanSetting xqz = new BooleanSetting("xqz", false, this);

    //players
    public ParentSetting playerParent = new ParentSetting("Players", this);
    public BooleanSetting players = new BooleanSetting("RenderPlayers", false, playerParent);
    public BooleanSetting local = new BooleanSetting("Self", false, playerParent);
    public ColourSetting highlightColorPlayer = new ColourSetting("PlayerColor", new Colour(250, 0, 250, 50), playerParent);
    public ColourSetting xqzColorPlayer = new ColourSetting("PlayerXqz", new Colour(0, 70, 250, 50), playerParent, v -> xqz.getValue());

    //mobs
    public ParentSetting mobsParent = new ParentSetting("PassiveMobs", this);
    public BooleanSetting mobs = new BooleanSetting("RenderMobs", false, mobsParent);
    public ColourSetting highlightColorMobs = new ColourSetting("MobColor", new Colour(80, 200, 0, 50), mobsParent);
    public ColourSetting xqzColorPlayerMobs = new ColourSetting("MobXqz", new Colour(0, 59, 200, 50), mobsParent, v -> xqz.getValue());

    //monsters
    public ParentSetting monstersParent = new ParentSetting("Monsters", this);
    public BooleanSetting monsters = new BooleanSetting("RenderMonsters", false, monstersParent);
    public ColourSetting highlightColorMonster = new ColourSetting("MonsterColor", new Colour(140, 200, 250, 50), monstersParent);
    public ColourSetting xqzColorMonster = new ColourSetting("MonsterXqz", new Colour(190, 0, 90, 50), monstersParent, v -> xqz.getValue());
}

