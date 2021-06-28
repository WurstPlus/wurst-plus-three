package me.travis.wurstplusthree.hack.hacks.render;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.DoubleSetting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

@Hack.Registration(name = "Tracers", description = "drawslines", category = Hack.Category.RENDER, isListening = false)
public class Tracers extends Hack {

    DoubleSetting width = new DoubleSetting("Width", 2.0, 0.0, 10.0, this);
    DoubleSetting range = new DoubleSetting("Range", 100.0, 0.0, 500.0, this);
    BooleanSetting friends = new BooleanSetting("Friends", true, this);

    @Override
    public void onRender3D(Render3DEvent event) {
        if (nullCheck()) {
            return;
        }
        GlStateManager.pushMatrix();
        mc.world.loadedEntityList.forEach(entity -> {
            if (!(entity instanceof EntityPlayer) || entity == mc.player) return;
            EntityPlayer player = (EntityPlayer) entity;
            if (!friends.getValue() && WurstplusThree.FRIEND_MANAGER.isFriend(player.getName())) return;
            if (mc.player.getDistance(player) > range.getValue()) return;
            float[] colour = this.getColorByDistance(entity);
            this.drawLineToEntity(entity, colour[0], colour[1], colour[2], colour[3]);
        });
        GlStateManager.popMatrix();
    }

    public double interpolate(double now, double then) {
        return then + (now - then) * (double) mc.getRenderPartialTicks();
    }

    public double[] interpolate(Entity entity) {
        double posX = this.interpolate(entity.posX, entity.lastTickPosX) -mc.getRenderManager().renderPosX;
        double posY = this.interpolate(entity.posY, entity.lastTickPosY) -mc.getRenderManager().renderPosY;
        double posZ = this.interpolate(entity.posZ, entity.lastTickPosZ) -mc.getRenderManager().renderPosZ;
        return new double[]{posX, posY, posZ};
    }

    public void drawLineToEntity(Entity e, float red, float green, float blue, float opacity) {
        double[] xyz = this.interpolate(e);
        this.drawLine(xyz[0], xyz[1], xyz[2], e.height, red, green, blue, opacity);
    }

    public void drawLine(double posx, double posy, double posz, double up, float red, float green, float blue, float opacity) {
        Vec3d eyes = new Vec3d(0.0, 0.0, 1.0).rotatePitch(-((float) Math.toRadians(mc.player.rotationPitch))).rotateYaw(-((float) Math.toRadians(mc.player.rotationYaw)));
        this.drawLineFromPosToPos(eyes.x, eyes.y + (double)mc.player.getEyeHeight(), eyes.z, posx, posy, posz, up, red, green, blue, opacity);
    }

    public void drawLineFromPosToPos(double posx, double posy, double posz, double posx2, double posy2, double posz2, double up, float red, float green, float blue, float opacity) {
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(this.width.getValue().floatValue());
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glColor4f(red, green, blue, opacity);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        GL11.glLoadIdentity();
        final boolean bobbing = mc.gameSettings.viewBobbing;
        mc.gameSettings.viewBobbing = false;
        mc.entityRenderer.orientCamera(mc.getRenderPartialTicks());
        GL11.glBegin(GL11.GL_LINES); {
            GL11.glVertex3d(posx, posy, posz);
            GL11.glVertex3d(posx2, posy2, posz2);
        }
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        GL11.glColor3d(1d, 1d, 1d);
        mc.gameSettings.viewBobbing = bobbing;
    }

    public float[] getColorByDistance(Entity entity) {
        if (entity instanceof EntityPlayer && WurstplusThree.FRIEND_MANAGER.isFriend(entity.getName())) {
            return new float[]{0.0f, 0.5f, 1.0f, 1.0f};
        }
        final Color col = new Color(Color.HSBtoRGB((float)(Math.max(0.0, Math.min(mc.player.getDistanceSq(entity), 2500.0f) / 2500.0f) / 3.0), 1.0f, 0.8f) | 0xFF000000);
        return new float[]{(float) col.getRed() / 255.0f, (float) col.getGreen() / 255.0f, (float) col.getBlue() / 255.0f, 1.0f};
    }

}
