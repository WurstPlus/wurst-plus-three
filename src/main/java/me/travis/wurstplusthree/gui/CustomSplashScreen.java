package me.travis.wurstplusthree.gui;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.manager.FontManager;
import me.travis.wurstplusthree.util.Render;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class CustomSplashScreen extends GuiScreen {

    private List<ResourceLocation> backgrounds = new ArrayList<>();
    private final ResourceLocation background = new ResourceLocation("textures/pitbull1.jpg");
    private final ResourceLocation banner = new ResourceLocation("textures/banner.png");
    private int y;
    private int x;
    private float xOffset;
    private float yOffset;

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
        this.x = this.width / 2;
        this.y = this.height / 4 + 48;
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
        if (CustomSplashScreen.isHovered(this.x - WurstplusThree.FONTMANAGER.getTextWidth("singleplayer") / 2, this.y + 20, WurstplusThree.FONTMANAGER.getTextWidth("singleplayer"), WurstplusThree.FONTMANAGER.getTextHeight(), mouseX, mouseY)) {
            this.mc.displayGuiScreen(new GuiWorldSelection(this));
        } else if (CustomSplashScreen.isHovered(this.x - WurstplusThree.FONTMANAGER.getTextWidth("the_fellas") / 2, this.y + 44, WurstplusThree.FONTMANAGER.getTextWidth("the_fellas"), WurstplusThree.FONTMANAGER.getTextHeight(), mouseX, mouseY)) {
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        } else if (CustomSplashScreen.isHovered(this.x - WurstplusThree.FONTMANAGER.getTextWidth("settings") / 2, this.y + 66, WurstplusThree.FONTMANAGER.getTextWidth("settings"), WurstplusThree.FONTMANAGER.getTextHeight(), mouseX, mouseY)) {
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        } else if (CustomSplashScreen.isHovered(this.x - WurstplusThree.FONTMANAGER.getTextWidth("log") / 2, this.y + 88, WurstplusThree.FONTMANAGER.getTextWidth("log"), WurstplusThree.FONTMANAGER.getTextHeight(), mouseX, mouseY)) {
            this.mc.shutdown();
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.xOffset = -1.0f * (((float) mouseX - (float) this.width / 2.0f) / ((float) this.width / 32.0f));
        this.yOffset = -1.0f * (((float) mouseY - (float) this.height / 2.0f) / ((float) this.height / 18.0f));
        this.x = this.width / 2;
        this.y = this.height / 4 + 48;
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        // this.mc.getTextureManager().bindTexture(this.backgrounds.get(random.nextInt(this.backgrounds.size())));
        this.mc.getTextureManager().bindTexture(this.background);
        CustomSplashScreen.drawCompleteImage(-16.0f + this.xOffset, -9.0f + this.yOffset, this.width + 32, this.height + 18);
        // this.mc.getTextureManager().bindTexture(this.banner);
        // CustomSplashScreen.drawCompleteImage(this.width / 2 - 225, this.height / 5 - 125, 450, 250);
        String watermark = WurstplusThree.MODNAME + " v" + WurstplusThree.MODVER + " : made by travis#0001";
        WurstplusThree.FONTMANAGER.drawStringWithShadow(watermark, (float) this.width - (float) WurstplusThree.FONTMANAGER.getTextWidth(watermark) * 1.1f, this.height - WurstplusThree.FONTMANAGER.getTextHeight() - 2, Color.WHITE.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public BufferedImage parseBackground(BufferedImage background) {
        int height;
        int width = 1920;
        int srcWidth = background.getWidth();
        int srcHeight = background.getHeight();
        for (height = 1080; width < srcWidth || height < srcHeight; width *= 2, height *= 2) {
        }
        BufferedImage imgNew = new BufferedImage(width, height, 2);
        Graphics g = imgNew.getGraphics();
        g.drawImage(background, 0, 0, null);
        g.dispose();
        return imgNew;
    }

    private static class TextButton extends GuiButton {

        public TextButton(int buttonId, int x, int y, String buttonText) {
            super(buttonId, x, y, WurstplusThree.FONTMANAGER.getTextWidth(buttonText), WurstplusThree.FONTMANAGER.getTextHeight(), buttonText);
        }

        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (this.visible) {
                this.enabled = true;
                this.hovered = (float) mouseX >= (float) this.x - (float) WurstplusThree.FONTMANAGER.getTextWidth(this.displayString) / 2.0f && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
                WurstplusThree.FONTMANAGER.drawStringWithShadow(this.displayString, (float) this.x - (float) WurstplusThree.FONTMANAGER.getTextWidth(this.displayString) / 2.0f + 1f, this.y, Color.WHITE.getRGB());
                if (this.hovered) {
                    Render.drawLine(this.x - 1f - (float) WurstplusThree.FONTMANAGER.getTextWidth(this.displayString) / 2.0f, this.y + 2 + WurstplusThree.FONTMANAGER.getTextHeight(), this.x + 2.0f + (float) WurstplusThree.FONTMANAGER.getTextWidth(this.displayString) / 2.0f, this.y + 2 + WurstplusThree.FONTMANAGER.getTextHeight(), 1.0f, Color.WHITE.getRGB());
                }
            }
        }

        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
            return this.enabled && this.visible && (float) mouseX >= (float) this.x - (float) WurstplusThree.FONTMANAGER.getTextWidth(this.displayString) / 2.0f && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        }
    }

}
