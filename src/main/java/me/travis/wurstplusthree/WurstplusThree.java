package me.travis.wurstplusthree;

import me.travis.wurstplusthree.command.Commands;
import me.travis.wurstplusthree.event.Events;
import me.travis.wurstplusthree.guirewrite.WurstplusGuiNew;
import me.travis.wurstplusthree.hack.Hacks;
import me.travis.wurstplusthree.manager.*;
import me.travis.wurstplusthree.manager.fonts.DonatorFont;
import me.travis.wurstplusthree.manager.fonts.GuiFont;
import me.travis.wurstplusthree.manager.fonts.MenuFont;
import me.travis.wurstplusthree.setting.Settings;
import me.travis.wurstplusthree.util.RenderUtil2D;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

/**
 * @author travis - began work on 8th april 2021
 */
@Mod(modid = WurstplusThree.MODID, name = WurstplusThree.MODNAME, version = WurstplusThree.MODVER)
public class WurstplusThree {

    public static final String MODID = "wurstplusthree";
    public static final String MODNAME = "Wurst+3";
    public static final String MODVER = "0.5.0";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    // events
    public static Events EVENTS;

    // commands
    public static Commands COMMANDS;

    // hacks
    public static Hacks HACKS;

    // settings
    public static Settings SETTINGS;

    // gui
    public static WurstplusGuiNew GUI2;

    // managers
    public static MenuFont MENU_FONT_MANAGER;
    public static GuiFont GUI_FONT_MANAGER;
    public static DonatorFont DONATOR_FONT_MANAGER;
    public static FriendManager FRIEND_MANAGER;
    public static EnemyManager ENEMY_MANAGER;
    public static PopManager POP_MANAGER;
    public static ServerManager SERVER_MANAGER;
    public static PositionManager POS_MANAGER;
    public static RotationManager ROTATION_MANAGER;
    public static ConfigManager CONFIG_MANAGER;
    public static SongManager SONG_MANAGER;
    public static CapeManager CAPE_MANAGER;
    public static CosmeticManager COSMETIC_MANAGER;
    public static AltManager ALT_MANAGER;

    // megs weird thingy
    public static RenderUtil2D RENDER_UTIL_2D;

    @Mod.Instance
    public static WurstplusThree INSTANCE;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("loading " + MODNAME);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        this.load();
        LOGGER.info(MODNAME + " : " + MODVER + " has been loaded");
        Display.setTitle("Wurst+3 | v" + MODVER);
    }

    public void load() {
        EVENTS = new Events();
        SETTINGS = new Settings();
        RENDER_UTIL_2D = new RenderUtil2D();
        COMMANDS = new Commands();
        HACKS = new Hacks();
        this.loadManagers();
        CONFIG_MANAGER.loadConfig();
        GUI2 = new WurstplusGuiNew();
    }

    public static void unLoad() {
        CONFIG_MANAGER.saveConfig();
    }

    public void loadManagers() {
        MENU_FONT_MANAGER = new MenuFont();
        GUI_FONT_MANAGER = new GuiFont();
        FRIEND_MANAGER = new FriendManager();
        ENEMY_MANAGER = new EnemyManager();
        POP_MANAGER = new PopManager();
        SERVER_MANAGER = new ServerManager();
        POS_MANAGER = new PositionManager();
        ROTATION_MANAGER = new RotationManager();
        CONFIG_MANAGER = new ConfigManager();
        SONG_MANAGER = new SongManager();
        CAPE_MANAGER = new CapeManager();
        DONATOR_FONT_MANAGER = new DonatorFont();
        COSMETIC_MANAGER = new CosmeticManager();
        ALT_MANAGER = new AltManager();
    }

}

