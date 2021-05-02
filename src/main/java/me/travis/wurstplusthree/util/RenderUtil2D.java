package me.travis.wurstplusthree.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * @author Azrn
 * @since 29/04/2021
 * 
 */

public class RenderUtil2D {

    public static int deltaTime;

    public int getDeltaTime(){
        return deltaTime;
    }

    public void setDeltaTime(int v){
        deltaTime = v;
    }

    // Utility
    public static double getAlphaFromHex(int color) {
        return ((double) ((color >> 24 & 0xff) / 255F));
    }

    public static double getRedFromHex(int color) {
        return ((double) ((color >> 16 & 0xff) / 255F));
    }

    public static double getGreenFromHex(int color) {
        return ((double) ((color >> 8 & 0xff) / 255F));
    }

    public static double getBlueFromHex(int color) {
        return ((double) ((color & 0xff) / 255F));
    }

    public static int changeAlphaFromHex(int color, int alpha) {
        return (color & 0xFFFFFF) + (alpha << 24);
    }

    // Minecraft rendering methods
    public static void drawHorizontalLine(int x, int y, int length, int color) {
        Gui.drawRect(x, y, x + length, y + 1, color); // Line
    }

    public static void drawVerticalLine(int x, int y, int length, int color) {
        Gui.drawRect(x, y, x + 1, y + length, color); // Line
    }

    public static void drawPixel(int x, int y, int color) {
        Gui.drawRect(x, y, x + 1, y + 1, color);
    }

    public static void drawRect(int startX, int startY, int endX, int endY, int color) {
        Gui.drawRect(startX, startY, endX, endY, color); // Standard rect
    }

    public static void drawDisabledModuleRect(int startX, int startY, int endX, int endY, int color) {
        startX = startX * 2;
        startY = startY * 2;
        endX = endX * 2;
        endY = endY * 2;

        GL11.glPushMatrix(); // Push
        GL11.glScalef(0.5f, 0.5f, 0.5f); // Sets scalefactor to half of normal

        drawRect(startX, startY, endX, endY, color);

        GL11.glPopMatrix(); // Pop
    }

    public static void drawEnabledModuleRect(int startX, int startY, int endX, int endY, int activatedColor, int backgroundColor) {
        startX = startX * 2;
        startY = startY * 2;
        endX = endX * 2;
        endY = endY * 2;

        GL11.glPushMatrix(); // Push
        GL11.glScalef(0.5f, 0.5f, 0.5f); // Sets scalefactor to half of normal

        Gui.drawRect(startX, startY, endX, endY, backgroundColor); // Background box
        Gui.drawRect(startX + 4, startY, endX - 4, endY, activatedColor); // Activated box

        GL11.glPopMatrix(); // Pop
    }

    public static void drawPreciseRectangle(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, top, 0.0D).endVertex();
        bufferbuilder.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawRectangleBordered(final double x, final double y, final double x1, final double y1, final double width, final int internalColor, final int borderColor) {
        drawPreciseRectangle(x + width, y + width, x1 - width, y1 - width, internalColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        drawPreciseRectangle(x + width, y, x1 - width, y + width, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        drawPreciseRectangle(x, y, x + width, y1, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        drawPreciseRectangle(x1 - width, y, x1, y1, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        drawPreciseRectangle(x + width, y1 - width, x1 - width, y1, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void drawGradientRect(float left, float top, float right, float bottom, int startColor, int endColor) {
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double) right, (double) top, (double) 0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double) left, (double) top, (double) 0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double) left, (double) bottom, (double) 0).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double) right, (double) bottom, (double) 0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
    public static void drawSidewaysGradientRect(float left, float top, float right, float bottom, int startColor, int endColor) {
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double) right, (double) top, (double) 0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double) left, (double) top, (double) 0).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double) left, (double) bottom, (double) 0).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double) right, (double) bottom, (double) 0).color(f1, f2, f3, f).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
    public static void drawTriangle(float x, float y, float size, float theta, int color) {
        GL11.glTranslated(x, y, 0);
        GL11.glRotatef(180 + theta, 0F, 0F, 1.0F);

        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;

        GL11.glColor4f(red, green, blue, alpha);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(1);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);

        GL11.glVertex2d(0, (1.0F * size));
        GL11.glVertex2d((1 * size), -(1.0F * size));
        GL11.glVertex2d(-(1 * size), -(1.0F * size));

        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glRotatef(-180 - theta, 0F, 0F, 1.0F);
        GL11.glTranslated(-x, -y, 0);
    }

    // Modes:
    // 0 = Top left
    // 1 = Top right
    // 2 = Bottom left
    // 3 = Bottom right
    public static void drawQuarterCircle(int x, int y, int radius, int mode, int color) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4d(getRedFromHex(color), getGreenFromHex(color), getBlueFromHex(color), getAlphaFromHex(color));
        GL11.glBegin(GL11.GL_POLYGON);
        GL11.glVertex2d(x, y);
        if (mode == 0) {
            for (int i = 0; i <= 90; i++) {
                GL11.glVertex2d(x + (Math.sin((i * 3.1415926D / 180)) * (radius * -1)), y + (Math.cos((i * 3.1415926D / 180)) * (radius * -1)));
            }
        } else if (mode == 1) {
            for (int i = 90; i <= 180; i++) {
                GL11.glVertex2d(x + (Math.sin((i * 3.1415926D / 180)) * radius), y + (Math.cos((i * 3.1415926D / 180)) * radius));
            }
        } else if (mode == 2) {
            for (int i = 90; i <= 180; i++) {
                GL11.glVertex2d(x + (Math.sin((i * 3.1415926D / 180)) * (radius * -1)), y + (Math.cos((i * 3.1415926D / 180)) * (radius * -1)));
            }
        } else if (mode == 3) {
            for (int i = 0; i <= 90; i++) {
                GL11.glVertex2d(x + (Math.sin((i * 3.1415926D / 180)) * radius), y + (Math.cos((i * 3.1415926D / 180)) * radius));
            }
        }
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static void drawCircle(int x, int y, int radius, int color) {
        drawQuarterCircle(x, y, radius, 0, color); // Top left
        drawQuarterCircle(x, y, radius, 1, color); // Top right
        drawQuarterCircle(x, y, radius, 2, color); // Bottom left
        drawQuarterCircle(x, y, radius, 3, color); // Bottom right
    }

    // Modes:
    // 0 = Top left
    // 1 = Top right
    // 2 = Bottom left
    // 3 = Bottom right
    public static void drawUnfilledQuarterCircle(int x, int y, int radius, float lineWidth, int mode, int color) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4d(getRedFromHex(color), getGreenFromHex(color), getBlueFromHex(color), getAlphaFromHex(color));
        GL11.glLineWidth(lineWidth);
        // GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBegin(GL11.GL_LINE_STRIP);

        if (mode == 0) { // Top left
            for (int i = 0; i <= 90; i++) {
                GL11.glVertex2d(x + (Math.sin((i * 3.1415926D / 180)) * (radius * -1)), y + (Math.cos((i * 3.1415926D / 180)) * (radius * -1)));
            }
        } else if (mode == 1) { // Top right
            for (int i = 90; i <= 180; i++) {
                GL11.glVertex2d(x + (Math.sin((i * 3.1415926D / 180)) * radius), y + (Math.cos((i * 3.1415926D / 180)) * radius));
            }
        } else if (mode == 2) { // Bottom left
            for (int i = 90; i <= 180; i++) {
                GL11.glVertex2d(x + (Math.sin((i * 3.1415926D / 180)) * (radius * -1)), y + (Math.cos((i * 3.1415926D / 180)) * (radius * -1)));
            }
        } else if (mode == 3) { // Bottom right
            for (int i = 0; i <= 90; i++) {
                GL11.glVertex2d(x + (Math.sin((i * 3.1415926D / 180)) * radius), y + (Math.cos((i * 3.1415926D / 180)) * radius));
            }
        }

        GL11.glEnd();
        // GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void drawCircularBox(int startX, int startY, int endX, int endY, int color) {
        int radius = (endY - startY) / 2;

        drawRect(startX + radius, startY, endX - radius, endY, color); // Main box
        drawQuarterCircle(startX + radius, startY + radius, radius, 0, color); // Top left
        drawQuarterCircle(endX - radius, startY + radius, radius, 1, color); // Top right
        drawQuarterCircle(startX + radius, endY - radius, radius, 2, color); // Bottom left
        drawQuarterCircle(endX - radius, endY - radius, radius, 3, color); // Bottom right
    }

    public static void drawRoundedBox(int startX, int startY, int endX, int endY, int color) {
        int radius = (endY - startY) / 4;

        drawRect(startX + radius, startY, endX - radius, endY, color); // Main box
        drawRect(startX, startY + radius, startX + radius, endY - radius, color); // Left box
        drawRect(endX - radius, startY + radius, endX, endY - radius, color); // Right box
        drawQuarterCircle(startX + radius, startY + radius, radius, 0, color); // Top left
        drawQuarterCircle(endX - radius, startY + radius, radius, 1, color); // Top right
        drawQuarterCircle(startX + radius, endY - radius, radius, 2, color); // Bottom left
        drawQuarterCircle(endX - radius, endY - radius, radius, 3, color); // Bottom right
    }

    public static void drawArrow(float x, float y, float size, boolean arrowUp, int color) {
        int theta;
        if (arrowUp) {
            theta = 180;
        } else {
            theta = 0;
        }
        drawTriangle(x, y, size, theta, color);
    }

    public static void drawTestPoly(int x, int y, int color) {
        GL11.glTranslated(x, y, 0);

        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;

        GL11.glColor4f(red, green, blue, alpha);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(1);
        GL11.glBegin(GL11.GL_POLYGON);

        GL11.glVertex2f(0, 0);
        GL11.glVertex2f(-5, -5);
        GL11.glVertex2f(0, 5);
        GL11.glVertex2f(5, -5);

        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glTranslated(-x, -y, 0);
    }

    public static void drawObtainOne(int startX, int startY, int endX, int endY, int cornerRadius, int color) {
        startX = startX * 2;
        startY = startY * 2;
        endX = endX * 2;
        endY = endY * 2;
        cornerRadius = cornerRadius * 2;

        GL11.glPushMatrix(); // Push
        GL11.glScalef(0.5f, 0.5f, 0.5f); // Sets scalefactor to half of normal

        drawRect(startX + cornerRadius, startY, endX - cornerRadius, endY, color); // Main box
        drawRect(startX, startY + cornerRadius, startX + cornerRadius, endY, color); // Left box
        drawRect(endX - cornerRadius, startY + cornerRadius, endX, endY, color); // Right box
        drawQuarterCircle(startX + cornerRadius, startY + cornerRadius, cornerRadius, 0, color); // Left quarter circle
        drawQuarterCircle(endX - cornerRadius, startY + cornerRadius, cornerRadius, 1, color); // Right quarter circle

        drawVerticalLine(startX, startY + cornerRadius, endY - startY - cornerRadius, 0xFF000000); // Left outline
        drawVerticalLine(endX - 1, startY + cornerRadius, endY - startY - cornerRadius, 0xFF000000);  // Right outline
        drawHorizontalLine(startX + cornerRadius, startY, endX - startX - 2 * cornerRadius, 0xFF000000); // Top outline
        drawHorizontalLine(startX, endY - 1, endX - startX, 0xFF000000); // Bottom outline
        drawUnfilledQuarterCircle(startX + cornerRadius, startY + cornerRadius, cornerRadius, 1, 0, 0xFF000000); // Left quarter circle outline
        drawUnfilledQuarterCircle(endX - cornerRadius, startY + cornerRadius, cornerRadius, 1, 1, 0xFF000000); // Right quarter circle outline

        /* drawHorizontalLine(startX + cornerRadius - cornerRadius / 4, startY + 1, endX - startX - 2 * cornerRadius + 2 * (cornerRadius / 4), 0xFF404040); // Top light outline
        drawHorizontalLine(startX + 1, endY - 2, endX - startX - 2, 0xFF404040); // Bottom light outline
        drawVerticalLine(startX + 1, startY + cornerRadius - cornerRadius / 4, endY - startY - cornerRadius, 0xFF404040); // Left light outline
        drawVerticalLine(endX - 2, startY + cornerRadius - cornerRadius / 4, endY - startY - cornerRadius, 0xFF404040); // Right light outline
        drawUnfilledQuarterCircle(startX + cornerRadius, startY + cornerRadius, cornerRadius - 1, 1, 0, 0xFF404040); // Left quarter circle outline
        drawUnfilledQuarterCircle(endX - cornerRadius, startY + cornerRadius, cornerRadius - 1, 1, 1, 0xFF404040); // Right quarter circle outline */

        GL11.glPopMatrix(); // Pop
    }

    public static void drawObtainTwo(int startX, int startY, int endX, int endY, int colorOne, int colorTwo) {
        startX = startX * 2;
        startY = startY * 2;
        endX = endX * 2;
        endY = endY * 2;

        GL11.glPushMatrix(); // Push
        GL11.glScalef(0.5f, 0.5f, 0.5f); // Sets scalefactor to half of normal

        drawGradientRect(startX, startY, endX, endY, colorOne, colorTwo);

        drawVerticalLine(startX, startY, endY - startY, 0xFF000000); // Left outline
        drawVerticalLine(endX - 1, startY, endY - startY, 0xFF000000);  // Right outline
        drawHorizontalLine(startX, startY, endX - startX, 0xFF000000); // Top outline
        drawHorizontalLine(startX, endY - 1, endX - startX, 0xFF000000); // Bottom outline

        /* drawHorizontalLine(startX + 1, startY + 1, endX - startX - 2, 0xFF404040); // Top light outline
        drawHorizontalLine(startX + 1, endY - 2, endX - startX - 2, 0xFF404040); // Bottom light outline
        drawVerticalLine(startX + 1, startY + 1, endY - startY - 2, 0xFF404040); // Left light outline
        drawVerticalLine(endX - 2, startY + 1, endY - startY - 2, 0xFF404040); // Right light outline */

        GL11.glPopMatrix(); // Pop
    }

    public static void drawObtainThree(int startX, int startY, int endX, int endY, int colorOne, int colorTwo) {
        startX = startX * 2;
        startY = startY * 2;
        endX = endX * 2;
        endY = endY * 2;

        int halfDifference = (endY - startY) / 2;

        GL11.glPushMatrix(); // Push
        GL11.glScalef(0.5f, 0.5f, 0.5f); // Sets scalefactor to half of normal

        drawRect(startX, startY, endX, endY - halfDifference, 0xFF2C2C2C);
        drawGradientRect(startX, startY + halfDifference, endX, endY, colorOne, colorTwo);

        // drawHorizontalLine(startX, startY, endX - startX, 0xFF444444); // Top outline
        drawHorizontalLine(startX, endY - 1, endX - startX, 0xFF000000); // Bottom outline
        drawVerticalLine(startX, startY, endY - startY, 0xFF000000); // Left outline
        drawVerticalLine(endX - 1, startY, endY - startY, 0xFF000000);  // Right outline

        GL11.glPopMatrix(); // Pop
    }

    public static void drawObtainFour(int startX, int startY, int endX, int endY) {
        startX = startX * 2;
        startY = startY * 2;
        endX = endX * 2;
        endY = endY * 2;

        GL11.glPushMatrix(); // Push
        GL11.glScalef(0.5f, 0.5f, 0.5f); // Sets scalefactor to half of normal

        drawGradientRect(startX, startY, endX, endY, 0xFF222222, 0xFF1A1A1A);

        drawHorizontalLine(startX, startY, endX - startX, 0xFF292929); // Top outline
        drawVerticalLine(startX, startY, endY - startY, 0xFF000000); // Left outline
        drawVerticalLine(endX - 1, startY, endY - startY, 0xFF000000);  // Right outline

        GL11.glPopMatrix(); // Pop
    }

    public static void drawCog(float x, float y, int color) {
        x = x / 2;
        y = y / 2;

        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;

        int notches = 9; // num. of notches
        float radiusO = 5; // outer radius 180 old
        float radiusI = 4; // inner radius 130 old
        float radiusC = 2; // monkey circle radius
        int taperO = 0; // outer taper %
        int taperI = 30; // inner taper %

        // pre-calculate values for loop
        double pi2 = 2 * Math.PI; // cache 2xPI (360deg)
        double angle = pi2 / (notches * 2); // angle between notches
        double taperAI = angle * taperI * 0.005; // inner taper offset (100% = half notch)
        double taperAO = angle * taperO * 0.005; // outer taper offset
        double a = angle; // iterator (angle)
        boolean toggle = true; // notch radius level (i/o)

        GL11.glTranslated(x, y, 0);
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(1);
        GL11.glBegin(GL11.GL_LINE_STRIP);

        for (int i = 0; i <= 360; i++) {
            GL11.glVertex2d(x + (Math.sin((i * 3.1415926D / 180)) * (radiusC * -1)), y + (Math.cos((i * 3.1415926D / 180)) * (radiusC * -1)));
        }

        GL11.glEnd();
        GL11.glBegin(GL11.GL_LINE_LOOP);

        for (; a <= pi2; a += angle) {
            if (toggle) {
                GL11.glVertex2d(x + radiusI * Math.cos(a - taperAI), y + radiusI * Math.sin(a - taperAI));
                GL11.glVertex2d(x + radiusO * Math.cos(a + taperAO), y + radiusO * Math.sin(a + taperAO));
            } else {
                GL11.glVertex2d(x + radiusO * Math.cos(a - taperAO), y + radiusO * Math.sin(a - taperAO));
                GL11.glVertex2d(x + radiusI * Math.cos(a + taperAI), y + radiusI * Math.sin(a + taperAI));
            }
            toggle = !toggle;
        }

        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glTranslated(-x, -y, 0);
    }

    public static void drawWrench(float x, float y, int color) {
        x = x / 2;
        y = y / 2;

        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;

        GL11.glTranslated(x, y, 0);
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(1);
        GL11.glBegin(GL11.GL_LINES);

        float a = 20; // width of shaft
        float b = 5; // height of shaft
        float c = b * 1.5f; // radius of outer circle

        GL11.glVertex2d(x, y); // Top shaft line
        GL11.glVertex2d(x - a, y); // Top shaft line
        GL11.glVertex2d(x - a, y); // End shaft line
        GL11.glVertex2d(x - a, y + b); // End shaft line
        GL11.glVertex2d(x - a, y + b); // Bottom shaft line
        GL11.glVertex2d(x, y + b); // Bottom shaft line

        for (int i = 0; i <= 90; i++) {
            GL11.glVertex2d(x + c + (-Math.sin((i * 3.1415926D / 180)) * c), y + (b / 2) + (-Math.cos((i * 3.1415926D / 180)) * c));
        }

        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glTranslated(-x, -y, 0);
    }

    public static void drawDarkModuleRect(int startX, int startY, int endX, int endY) {
        // Background (0xFF2F2F2F)
        Gui.drawRect(startX + 3, startY + 3, endX - 2, endY - 2, 0xFF2F2F2F);

        // Black outline (0xFF000000)
        drawHorizontalLine(startX + 2, startY, (endX - startX) - 3, 0xFF000000); // Top black line
        drawHorizontalLine(startX + 2, endY, (endX - startX) - 3, 0xFF000000); // Bottom black line
        drawVerticalLine(startX, startY + 2, (endY - startY) - 3, 0xFF000000); // Left black line
        drawVerticalLine(endX, startY + 2, (endY - startY) - 3, 0xFF000000); // Right black line
        drawPixel(startX + 1, startY + 1, 0xFF000000); // TopL pixel
        drawPixel(endX - 1, startY + 1, 0xFF000000); // TopR pixel
        drawPixel(startX + 1, endY - 1, 0xFF000000); // BotL pixel
        drawPixel(endX - 1, endY - 1, 0xFF000000); // BotR pixel

        // Lightgray inner outline (0xFF404040)
        drawHorizontalLine(startX + 2, startY + 1, (endX - startX) - 3, 0xFF404040); // 1st top lightgray line
        drawHorizontalLine(startX + 1, startY + 2, (endX - startX) - 2, 0xFF404040); // 2nd top lightgray line
        drawVerticalLine(startX + 1, startY + 2, (endY - startY) - 3, 0xFF404040); // 1st left lightgray line
        drawVerticalLine(startX + 2, startY + 2, (endY - startY) - 3, 0xFF404040); // 2nd left lightgray line
        drawPixel(startX + 3, startY + 3, 0xFF404040);

        // Gray inner outline (0xFF1F1F1F)
        drawHorizontalLine(startX + 2, endY - 1, (endX - startX) - 3, 0xFF1F1F1F); // 1st bottom gray line
        drawHorizontalLine(startX + 2, endY - 2, (endX - startX) - 2, 0xFF1F1F1F); // 2nd bottom gray line
        drawVerticalLine(endX - 1, startY + 2, (endY - startY) - 3, 0xFF1F1F1F); // 1st right gray line
        drawVerticalLine(endX - 2, startY + 3, (endY - startY) - 3, 0xFF1F1F1F); // 2nd left lightgray line
        drawPixel(endX - 3, endY - 3, 0xFF1F1F1F);
    }

    public static void drawRectWithOutline(int startX, int startY, int endX, int endY, int color, int outlineColor, float scaleF) {
        startX = (int) (startX / scaleF);
        startY = (int) (startY / scaleF);
        endX = (int) (endX / scaleF);
        endY = (int) (endY / scaleF);

        GL11.glPushMatrix(); // Push
        GL11.glScalef(scaleF, scaleF, scaleF); // Sets scalefactor

        drawRect(startX, startY, endX, endY, color);
        drawHorizontalLine(startX, startY - 1, endX - startX, outlineColor); // Top
        drawHorizontalLine(startX, endY - 1, endX - startX, outlineColor); // Bottom
        drawVerticalLine(startX, startY, endY - startY, outlineColor); // Left
        drawVerticalLine(endX - 1, startY, endY - startY, outlineColor); // Right

        GL11.glPopMatrix(); // Pop
    }

    /*static int i = 0;
    public static void drawDynamicStringWithShadow(String text, int color, int startX, int startY, int endX, int endY, int time) {
        if (i <= time) {
            int x = startX + (endX - startX) / time * i;
            int y = startY + (endX - startX) / time * i;
            PrismFontRenderer.drawStringWithShadow(text, x, y, color);
        } else {
            PrismFontRenderer.drawStringWithShadow(text, endX, endY, color);
        }
        i++;
    }*/

    public static void drawCrosshair(int x, int y, boolean dot, int color) {
        drawRect(x - 4, y, x - 1, y + 1, color); // Left
        drawRect(x + 2, y, x + 5, y + 1, color); // Right
        drawRect(x, y - 4, x + 1, y - 1, color); // Top
        drawRect(x, y + 2, x + 1, y + 5, color); // Down
        if (dot) {
            drawPixel(x, y, color);
        }
    }

    public static void drawImage(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight, ResourceLocation rsc) {
        try {
            Minecraft.getMinecraft().getTextureManager().bindTexture(rsc);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glColor4f(1, 1, 1, 1);
            Gui.drawScaledCustomSizeModalRect(x, y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
            GL11.glDisable(GL11.GL_BLEND);
        } catch (Exception ignored) {
        }
    }

    public static void drawEntityOnScreen(float posX, float posY, float scale, EntityLivingBase ent) {
        GlStateManager.pushMatrix();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableColorMaterial();
        GlStateManager.translate(posX, posY, 50.0F);
        GlStateManager.scale(-scale, scale, scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float) Math.atan((double) (ent.rotationPitch / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, ent.rotationYaw, 1.0F, false);
        rendermanager.setRenderShadow(true);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.popMatrix();

    }
}
