package me.travis.wurstplusthree;

import me.travis.wurstplusthree.manager.FontManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

@Mod(modid = "wurstplusthree", name = "Wurst+3", version = "0.0.1")
public class WurstplusThree {

    public static final String MODID = "wurstplusthree";
    public static final String MODNAME = "Wurst+3";
    public static final String MODVER = "0.0.1";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    // managers
    public static FontManager FONTMANAGER;

    @Mod.Instance
    public static WurstplusThree INSTANCE;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("loading " + MODNAME);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        FONTMANAGER = new FontManager();
        LOGGER.info(MODNAME + " : " + MODVER + " has been loaded");
        Display.setTitle(MODNAME + " v" + MODVER);
    }
}

