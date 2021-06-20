package me.travis.wurstplusthree.gui.hud.element;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.gui.component.Component;
import me.travis.wurstplusthree.hack.hacks.client.HudEditor;
import me.travis.wurstplusthree.util.RenderUtil2D;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class HudDragComponent extends Component {
    public HudElement element;
    public int width;
    public int height;
    public boolean dragging;
    public boolean hovered;
    public int dragX;
    public int dragY;


    public HudDragComponent(HudElement element, int width, int height){
        this.element = element;
        this.width = width;
        this.height = height;
        this.dragging = false;
        this.hovered = false;
        this.dragX = 0;
        this.dragY = 0;
    }

    @Override
    public void renderComponent(int mouseX, int mouseY) {
        RenderUtil2D.drawRectMC(element.getX(), element.getY(), element.getX() + width,element.getY() +  height, (!hovered) ? new Color(255,255,255, 40).hashCode() : new Color(255,255,255, 75).hashCode());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(!element.isEnabled())return;
        if(isMouseOn(mouseX, mouseY) && button == 0){
            this.dragging = true;
            this.dragX = mouseX - element.getX();
            this.dragY = mouseY - element.getY();
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        this.dragging = false;
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        if(!element.isEnabled())return;

        width = element.getWidth();
        height = element.getHeight();


        hovered = isMouseOn(mouseX, mouseY);

        if(this.dragging){
            for(HudElement el : WurstplusThree.HUD_MANAGER.getHudElements()){
                if(!HudEditor.INSTANCE.alignment.getValue())break;
                if(el == element || !el.isEnabled())continue;
                ScaledResolution sr = new ScaledResolution(mc);
                if(this.element.getX() == el.getX()){
                    RenderUtil2D.drawVLine(this.element.getX(), 0, sr.getScaledHeight(), HudEditor.INSTANCE.alignmentColor.getValue().hashCode());
                }if(this.element.getX() + width == el.getX() + el.getWidth()){
                    RenderUtil2D.drawVLine(this.element.getX() + width, 0, sr.getScaledHeight(), HudEditor.INSTANCE.alignmentColor.getValue().hashCode());
                }

                if(this.element.getY() == el.getY()){
                    RenderUtil2D.drawHLine(0 ,this.element.getY(), sr.getScaledWidth(), HudEditor.INSTANCE.alignmentColor.getValue().hashCode());
                }if(this.element.getY() + height == el.getY() + el.getHeight()){
                    RenderUtil2D.drawHLine(0, this.element.getY() + height, sr.getScaledWidth(), HudEditor.INSTANCE.alignmentColor.getValue().hashCode());
                }
            }

            double size = WurstplusThree.GUI_FONT_MANAGER.getTextWidth("X: " + this.element.getX() + ", Y: " + this.element.getY());
            RenderUtil2D.drawRectMC(this.element.getX(), this.element.getY() - 5, this.element.getX() + (int)Math.round(size-10/0.7) , this.element.getY() - 10, new Color(0, 0, 0, 100).hashCode());
            this.element.setX(mouseX - this.dragX);
            this.element.setY(mouseY - this.dragY);
            GL11.glPushMatrix();
            GL11.glScaled(0.7, 0.7, 0.7);
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow("X: " + this.element.getX() + ", Y: " + this.element.getY(), Math.round(this.element.getX() / 0.7) , Math.round(this.element.getY() / 0.7) - 12, new Color(255,255,255).hashCode());
            GL11.glPopMatrix();
        }
    }

    private boolean isMouseOn(int mouseX, int mouseY){
        return mouseX > element.getX() && mouseX < element.getX() + width && mouseY > element.getY() && mouseY < element.getY() + height;
    }
}
