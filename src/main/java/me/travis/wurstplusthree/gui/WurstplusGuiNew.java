package me.travis.wurstplusthree.gui;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.ColorCopyEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.gui.component.CategoryComponent;
import me.travis.wurstplusthree.gui.component.Component;
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
 * @author Madmegsox1
 * @since 27/04/2021
 * -> swag gui :sunglasses:
 */

public class WurstplusGuiNew extends GuiScreen {
    int screenHeight;
    int screenWidth;
    public static final int WIDTH = 120;
    public static final int HEIGHT = 16;
    public static final int MODULE_WIDTH = 5;
    public static final int SETTING_OFFSET = 5;

    public static final int FONT_HEIGHT = 4;
    public static final int MODULE_FONT_SIZE = 6;
    public static final int SUB_FONT_SIZE = 2 * MODULE_FONT_SIZE;
    public static final int COLOR_FONT_SIZE = 2 * SUB_FONT_SIZE;

    public Colour colorClipBoard;
    public ColorCopyEvent colorEvent;
    public boolean shouldShow;

    public static int GUI_MODULECOLOR() {
        return new Color(45, 45, 45, Gui.INSTANCE.buttonColor.getColor().getAlpha()).hashCode();
    }

    public static int GUI_COLOR() {
        return new Color(35, 35, 35, Gui.INSTANCE.buttonColor.getColor().getAlpha()).hashCode();
    }

    public static int GUI_CHILDBUTTON() {
        return new Color(25, 25, 25, Gui.INSTANCE.buttonColor.getColor().getAlpha()).hashCode();
    }

    public static int GUI_HOVERED_COLOR() {
        return new Color(20, 20, 20, Gui.INSTANCE.buttonColor.getColor().getAlpha()).hashCode();
    }

    private boolean flag = false;

    public static ArrayList<CategoryComponent> categoryComponents;

    public WurstplusGuiNew() {
        categoryComponents = new ArrayList<>();
        colorClipBoard = new Colour(0, 0, 0);
        int startX = 10;
        for (Hack.Category category : WurstplusThree.HACKS.getCategories()) {
            CategoryComponent categoryComponent = new CategoryComponent(category);
            categoryComponent.setX(startX);
            categoryComponents.add(categoryComponent);
            startX += WIDTH + 10;
            flag = false;
        }
    }

    @Override
    public void initGui() {
        WurstplusThree.EVENT_PROCESSOR.addEventListener(this);
        shouldShow = false;
        flag = false;
        for(CategoryComponent c : categoryComponents){
            c.animationValue = 0;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        scrollWheelCheck();
        ScaledResolution sr = new ScaledResolution(mc);
        screenHeight = sr.getScaledHeight();
        screenWidth = sr.getScaledWidth();
        boolean gradientShadow = Gui.INSTANCE.gradient.getValue();
        if (gradientShadow) {
            drawGradientRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), Gui.INSTANCE.gradientStartColor.getValue().getRGB(), Gui.INSTANCE.gradientEndColor.getValue().getRGB());
        }
        for(CategoryComponent categoryComponent : categoryComponents){
            categoryComponent.renderFrame(mouseX, mouseY);
            categoryComponent.updatePosition(mouseX, mouseY);
        }

        for(CategoryComponent categoryComponent : categoryComponents){
            for(Component component : categoryComponent.getComponents()){
                component.renderToolTip(mouseX, mouseY);
            }
        }

        if(shouldShow && colorEvent != null){

        }
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        for (CategoryComponent categoryComponent : categoryComponents) {
            if (categoryComponent.isWithinHeader(mouseX, mouseY) && mouseButton == 0) {
                categoryComponent.setDrag(true);
                categoryComponent.dragX = mouseX - categoryComponent.getX();
                categoryComponent.dragY = mouseY - categoryComponent.getY();
            }
            if (categoryComponent.isWithinHeader(mouseX, mouseY) && mouseButton == 1) {
                categoryComponent.setOpen(!categoryComponent.isOpen());
                mc.soundHandler.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
            if (categoryComponent.isOpen()) {
                if (!categoryComponent.getComponents().isEmpty()) {
                    for (Component component : categoryComponent.getComponents()) {
                        component.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                }
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        for (CategoryComponent categoryComponent : categoryComponents) {
            if (categoryComponent.isOpen() && keyCode != 1) {
                if (!categoryComponent.getComponents().isEmpty()) {
                    for (Component component : categoryComponent.getComponents()) {
                        component.keyTyped(typedChar, keyCode);
                    }
                }
            }
        }
        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for (CategoryComponent categoryComponent : categoryComponents) {
            categoryComponent.setDrag(false);
        }
        for (CategoryComponent categoryComponent : categoryComponents) {
            if (categoryComponent.isOpen()) {
                if (!categoryComponent.getComponents().isEmpty()) {
                    for (Component component : categoryComponent.getComponents()) {
                        component.mouseReleased(mouseX, mouseY, state);
                    }
                }
            }
        }
    }

    @Override
    public void onGuiClosed() {
        WurstplusThree.CONFIG_MANAGER.saveConfig();
        WurstplusThree.EVENT_PROCESSOR.removeEventListener(this);
    }

    private void scrollWheelCheck() {
         int dWheel = Mouse.getDWheel();
         if(dWheel < 0){
             for(CategoryComponent categoryComponent : categoryComponents){
                 categoryComponent.setY(categoryComponent.getY() - Gui.INSTANCE.scrollSpeed.getValue());
             }
         }
         else if(dWheel > 0){
             for(CategoryComponent categoryComponent : categoryComponents){
                 categoryComponent.setY(categoryComponent.getY() + Gui.INSTANCE.scrollSpeed.getValue());
             }
         }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public static ArrayList<CategoryComponent> getCategories() {
        return categoryComponents;
    }


    @CommitEvent
    public void ColorCopyEvent(ColorCopyEvent event){
        this.colorEvent = event;
        this.shouldShow = true;
    }

}
