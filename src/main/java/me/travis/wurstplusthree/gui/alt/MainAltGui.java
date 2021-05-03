package me.travis.wurstplusthree.gui.alt;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.manager.AltManager;
import me.travis.wurstplusthree.util.RenderUtil2D;
import me.travis.wurstplusthree.util.elements.Alt;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Madmegsox1
 * @since 02/05/2021
 */

public class MainAltGui extends GuiScreen {
    AltManager altManager = WurstplusThree.ALT_MANAGER;
    ArrayList<Alt> alts;
    ArrayList<AltComponent> altComponents;
    int offset;
    public static final int x = 40;
    public static final int y = 20;
    public static final int width = 100;
    public static final int height = 40;
    public static final int GUI_TRANSPARENCY = 0x99000000;
    public MainAltGui() {
        alts = altManager.getAlts();
    }

    @Override
    public void initGui(){
        alts.add(new Alt("Madmegsox", "123"));
        altComponents = new ArrayList<>();
        //offset = 10;
        for(Alt alt : alts){
            altComponents.add(new AltComponent(alt, offset, this));
            offset += 50;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        RenderUtil2D.drawRectMC(0, 0, mc.displayWidth, mc.displayWidth, new Color(28, 28, 28).hashCode());
        for(AltComponent alt : altComponents){
            alt.render();
        }
    }



}
