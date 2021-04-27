package me.travis.wurstplusthree.gui;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.gui.components.Rainbow;
import me.travis.wurstplusthree.util.RenderUtil;
import me.travis.wurstplusthree.util.SoundUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ElytraSound;
import net.minecraft.client.audio.GuardianSound;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class CustomSplashScreen extends GuiScreen {

    private final ResourceLocation background = new ResourceLocation("textures/pitbull1.jpg");
    private int y;
    private int x;
    private float watermarkX;

    public static void drawCompleteImage(float posX, float posY, float width, float height) {
        GL11.glPushMatrix();
        GL11.glTranslatef(posX, posY, 0.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(width, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(width, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public static boolean isHovered(int x, int y, int width, int height, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY < y + height;
    }

    public void initGui() {
        //mc.soundHandler.playSound(SoundUtil.sound);
        //WurstplusThree.LOGGER.info(mc.soundHandler.isSoundPlaying(SoundUtil.sound));
        this.x = this.width / 4;
        this.y = this.height / 4 + 48;
        this.watermarkX = this.width + 80;
        this.buttonList.add(new TextButton(0, this.x, this.y + 20, "singleplayer"));
        this.buttonList.add(new TextButton(1, this.x, this.y + 44, "the_fellas"));
        this.buttonList.add(new TextButton(2, this.x, this.y + 66, "settings"));
        this.buttonList.add(new TextButton(2, this.x, this.y + 88, "log"));
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public void updateScreen() {
        super.updateScreen();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (CustomSplashScreen.isHovered(this.x, this.y + 20, WurstplusThree.MENU_FONT_MANAGER.getTextWidth("singleplayer"), WurstplusThree.MENU_FONT_MANAGER.getTextHeight(), mouseX, mouseY)) {
            this.mc.displayGuiScreen(new GuiWorldSelection(this));
        } else if (CustomSplashScreen.isHovered(this.x, this.y + 44, WurstplusThree.MENU_FONT_MANAGER.getTextWidth("the_fellas"), WurstplusThree.MENU_FONT_MANAGER.getTextHeight(), mouseX, mouseY)) {
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        } else if (CustomSplashScreen.isHovered(this.x, this.y + 66, WurstplusThree.MENU_FONT_MANAGER.getTextWidth("settings"), WurstplusThree.MENU_FONT_MANAGER.getTextHeight(), mouseX, mouseY)) {
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        } else if (CustomSplashScreen.isHovered(this.x, this.y + 88, WurstplusThree.MENU_FONT_MANAGER.getTextWidth("log"), WurstplusThree.MENU_FONT_MANAGER.getTextHeight(), mouseX, mouseY)) {
            this.mc.shutdown();
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        float xOffset = -1.0f * (((float) mouseX - (float) this.width / 2.0f) / ((float) this.width / 32.0f));
        float yOffset = -1.0f * (((float) mouseY - (float) this.height / 2.0f) / ((float) this.height / 18.0f));
        this.x = this.width / 4;
        this.y = this.height / 4 + 48;
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        // this.mc.getTextureManager().bindTexture(this.backgrounds.get(random.nextInt(this.backgrounds.size())));
        this.mc.getTextureManager().bindTexture(this.background);
        CustomSplashScreen.drawCompleteImage(-16.0f + xOffset, -9.0f + yOffset, this.width + 32, this.height + 18);
        String watermark = WurstplusThree.MODNAME + " v" + WurstplusThree.MODVER + " : made by travis#0001";
        WurstplusThree.GUI_FONT_MANAGER.drawStringRainbow(watermark, watermarkX, this.height - WurstplusThree.GUI_FONT_MANAGER.getTextHeight() - 2, true);
        watermarkX -= .05f;
        if (watermarkX < -WurstplusThree.GUI_FONT_MANAGER.getTextWidth(watermark) - 10) {
            this.watermarkX = this.width + 40;
        }
        WurstplusThree.GUI_FONT_MANAGER.drawStringBig("WurstPlus 3", (float) this.x, (float) this.y - 20, Color.white.getRGB(), true);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private static class TextButton extends GuiButton {

        public TextButton(int buttonId, int x, int y, String buttonText) {
            super(buttonId, x, y, WurstplusThree.MENU_FONT_MANAGER.getTextWidth(buttonText), WurstplusThree.MENU_FONT_MANAGER.getTextHeight(), buttonText);
        }

        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (this.visible) {
                this.enabled = true;
                this.hovered = (float) mouseX >= (float) this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
                WurstplusThree.MENU_FONT_MANAGER.drawStringWithShadow(this.displayString, (float) this.x + 1f, this.y, Color.WHITE.getRGB());
                if (this.hovered) {
                    RenderUtil.drawLine(this.x - 5f, this.y + 2 + WurstplusThree.MENU_FONT_MANAGER.getTextHeight(), this.x - 5f, this.y - 2, 2f, Rainbow.rgb);
                }
            }
        }

        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
            return this.enabled && this.visible && (float) mouseX >= (float) this.x - (float) WurstplusThree.MENU_FONT_MANAGER.getTextWidth(this.displayString) / 2.0f && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        }
    }

}
