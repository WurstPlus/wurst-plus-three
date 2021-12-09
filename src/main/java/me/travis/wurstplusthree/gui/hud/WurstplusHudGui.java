package me.travis.wurstplusthree.gui.hud;

import me.travis.wurstplusthree.gui.component.CategoryComponent;
import me.travis.wurstplusthree.gui.component.Component;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.hacks.client.Gui;
import me.travis.wurstplusthree.hack.hacks.client.HudEditor;
import me.travis.wurstplusthree.util.RenderUtil2D;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.SoundEvents;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Madmegsox1
 * @since 17/06/2021
 */

public class WurstplusHudGui extends GuiScreen {

    public ArrayList<CategoryComponent> categoryComponents;

    public ArrayList<Integer> linesH;
    public ArrayList<Integer> linesV;

    public WurstplusHudGui(){
        categoryComponents = new ArrayList<>();
        categoryComponents.add(new CategoryComponent(Hack.Category.HUD));
        linesH = new ArrayList<>();
        linesV = new ArrayList<>();
    }

    @Override
    public void initGui() {
        linesH.clear();
        linesV.clear();
        ScaledResolution sr = new ScaledResolution(mc);
        int vStart = sr.getScaledWidth() / HudEditor.INSTANCE.vLines.getValue();
        int hStart = sr.getScaledHeight() / HudEditor.INSTANCE.hLines.getValue();
        for (int i = 0; i < HudEditor.INSTANCE.vLines.getValue(); i++) {
            linesV.add(vStart * (i + 1));
        }
        for (int i = 0; i < HudEditor.INSTANCE.hLines.getValue(); i++) {
            linesH.add(hStart * (i + 1));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        scrollWheelCheck();
        if(HudEditor.INSTANCE.grid.getValue()){
            ScaledResolution sr = new ScaledResolution(mc);
            for(int i : linesV){
                RenderUtil2D.drawVLine(i, 0, sr.getScaledHeight(), HudEditor.INSTANCE.gridColor.getValue().hashCode());
            }
            for(int i : linesH){
                RenderUtil2D.drawHLine(0, i, sr.getScaledWidth(), HudEditor.INSTANCE.gridColor.getValue().hashCode());
            }
        }
        for (CategoryComponent categoryComponent : categoryComponents){
            categoryComponent.renderFrame(mouseX, mouseY);
            categoryComponent.updatePosition(mouseX, mouseY);
            for (Component comp : categoryComponent.getComponents()) {
                comp.updateComponent(mouseX, mouseY);
            }
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
    public boolean doesGuiPauseGame() {
        return false;
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


}
