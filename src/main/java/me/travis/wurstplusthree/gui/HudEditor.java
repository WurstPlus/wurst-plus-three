package me.travis.wurstplusthree.gui;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.ColorCopyEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.gui.component.CategoryComponent;
import me.travis.wurstplusthree.gui.component.Component;
import me.travis.wurstplusthree.gui.component.HudListComponent;
import me.travis.wurstplusthree.gui.particles.ParticleSystem;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.hacks.client.Gui;
import me.travis.wurstplusthree.util.elements.Colour;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Wallhacks0
 */

//I rewrote gui lol
public class HudEditor extends GuiScreen {
    int screenHeight;
    int screenWidth;
    public static final int WIDTH = 120;
    public static final int HEIGHT = 16;
    public boolean shouldShow;
    public ParticleSystem particleSystem;

    private static HudListComponent component;

    public HudEditor() {
        HudListComponent categoryComponent = new HudListComponent();
        component = categoryComponent;
        component.setX(200);
        component.setY(300);
    }

    @Override
    public void initGui() {
        WurstplusThree.EVENT_PROCESSOR.addEventListener(this);
        shouldShow = false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (particleSystem != null)
            particleSystem.render(mouseX, mouseY);
        else
            this.particleSystem = new ParticleSystem(new ScaledResolution(mc));
        WurstplusThree.HUD.onGuiRender(mouseX, mouseY, partialTicks);
        ScaledResolution sr = new ScaledResolution(mc);
        screenHeight = sr.getScaledHeight();
        screenWidth = sr.getScaledWidth();
        component.renderFrame(mouseX, mouseY);
        component.updatePosition(mouseX, mouseY);
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        WurstplusThree.HUD.onMouseClicked(mouseX, mouseY, mouseButton);
        if (component.isWithinHeader(mouseX, mouseY) && mouseButton == 0) {
            component.setDrag(true);
            component.dragX = mouseX - component.getX();
            component.dragY = mouseY - component.getY();
        }
        if (component.isWithinHeader(mouseX, mouseY) && mouseButton == 1) {
            component.setOpen(!component.isOpen());
            mc.soundHandler.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        if (component.isOpen()) {
            if (!component.getComponents().isEmpty()) {
                for (Component component : component.getComponents()) {
                    component.mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }

    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (component.isOpen() && keyCode != 1) {
            if (!component.getComponents().isEmpty()) {
                for (Component component : component.getComponents()) {
                    component.keyTyped(typedChar, keyCode);
                }
            }
        }
        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        component.setDrag(false);
        WurstplusThree.HUD.onMouseReleased(mouseX, mouseY, state);
        if (component.isOpen()) {
            if (!component.getComponents().isEmpty()) {
                for (Component component : component.getComponents()) {
                    component.mouseReleased(mouseX, mouseY, state);
                }
            }
        }
    }

    @Override
    public void onGuiClosed() {
        WurstplusThree.CONFIG_MANAGER.saveConfig();
        WurstplusThree.EVENT_PROCESSOR.removeEventListener(this);
        component.onClose();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void updateScreen() {
        if (particleSystem != null)
            particleSystem.update();
    }
}
