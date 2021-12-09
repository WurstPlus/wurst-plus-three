package me.travis.wurstplusthree.hack.hacks.render;

import com.mojang.authlib.GameProfile;
import me.travis.wurstplusthree.event.events.TotemPopEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.event.processor.EventPriority;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.*;
import me.travis.wurstplusthree.util.ClientMessage;
import me.travis.wurstplusthree.util.EntityUtil;
import me.travis.wurstplusthree.util.InventoryUtil;
import me.travis.wurstplusthree.util.elements.Colour;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

//made by linus touch tips
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;


@Hack.Registration(name = "Chams", description = "draws people as colours/through walls", category = Hack.Category.RENDER, isListening = false)
public class Chams extends Hack {
    public static Chams INSTANCE;

    public Chams() {
        INSTANCE = this;
    }

    public EnumSetting mode = new EnumSetting("Mode", "Wire", Arrays.asList("Model", "Wire", "Shine", "WireModel"), this);
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
    public BooleanSetting popChams = new BooleanSetting("PopChams", false, playerParent);
    public ColourSetting popChamsColors = new ColourSetting("PlayerColor", new Colour(255, 255, 255, 200), playerParent);


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

    public ConcurrentHashMap<Integer, Integer> pops = new ConcurrentHashMap<>();

    @CommitEvent(priority = EventPriority.LOW)
    public void onPopLol(TotemPopEvent event) {
        if (popChams.getValue() && event.getEntity() != mc.player) {
            Entity ee = event.getEntity();
            ClientMessage.sendMessage("PopEventLol");
            ArrayList<Integer> idList = new ArrayList<>();
            for (Entity e : mc.world.loadedEntityList) {
                idList.add(e.entityId);
            }
            EntityOtherPlayerMP popCham = new EntityOtherPlayerMP(mc.world, event.getEntity().getGameProfile());
            popCham.copyLocationAndAnglesFrom(ee);
            popCham.rotationYawHead = ee.getRotationYawHead();
            popCham.rotationYaw = ee.rotationYaw;
            popCham.rotationPitch = ee.rotationPitch;
            popCham.setGameType(GameType.CREATIVE);
            popCham.setHealth(20);
            for (int i = 0; i > -10000; i--) {
                if (!idList.contains(i)) {
                    mc.world.addEntityToWorld(i, popCham);
                    pops.put(i, popChamsColors.getValue().getAlpha());
                    break;
                }
            }
        }
    }
}

