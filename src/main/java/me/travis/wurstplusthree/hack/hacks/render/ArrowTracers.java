



package me.earth.phobos.features.modules.render;

import me.earth.phobos.features.modules.*;
import me.earth.phobos.features.setting.*;
import me.earth.phobos.event.events.*;
import net.minecraft.entity.player.*;
import java.awt.*;
import me.earth.phobos.features.modules.client.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.*;
import org.lwjgl.opengl.*;
import com.google.common.collect.*;
import me.earth.phobos.util.*;
import java.util.*;

public class ArrowESP extends Module
{
    private final Setting<Boolean> colorSync;
    private final Setting<Boolean> invisibles;
    private final Setting<Boolean> offscreenOnly;
    private final Setting<Boolean> outline;
    private final Setting<Float> outlineWidth;
    private final Setting<Integer> fadeDistance;
    private final Setting<Integer> radius;
    private final Setting<Float> size;
    private final Setting<Integer> red;
    private final Setting<Integer> green;
    private final Setting<Integer> blue;
    private final EntityListener entityListener;
    
    public ArrowESP() {
        super("ArrowESP",  "Shows the direction players are in with cool little triangles :3",  Module.Category.RENDER,  true,  false,  false);
        this.colorSync = (Setting<Boolean>)this.register(new Setting("Sync", false));
        this.invisibles = (Setting<Boolean>)this.register(new Setting("Invisibles", false));
        this.offscreenOnly = (Setting<Boolean>)this.register(new Setting("Offscreen-Only", true));
        this.outline = (Setting<Boolean>)this.register(new Setting("Outline", true));
        this.outlineWidth = (Setting<Float>)this.register(new Setting("Outline-Width", 1.0f, 0.1f, 3.0f));
        this.fadeDistance = (Setting<Integer>)this.register(new Setting("Fade-Distance", 100, 10, 200));
        this.radius = (Setting<Integer>)this.register(new Setting("Radius", 45, 10, 200));
        this.size = (Setting<Float>)this.register(new Setting("Size", 10.0f, 5.0f, 25.0f));
        this.red = (Setting<Integer>)this.register(new Setting("Red", 255, 0, 255));
        this.green = (Setting<Integer>)this.register(new Setting("Green", 0, 0, 255));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", 255, 0, 255));
        this.entityListener = new EntityListener();
    }
    
    public void onRender2D(final Render2DEvent event) {
        this.entityListener.render();
        EntityPlayer entity;
        Vec3d pos;
        Color color2 = null;
        Color color;
        int x;
        int y;
        float yaw;
        ArrowESP.mc.world.loadedEntityList.forEach(o -> {
            if (o instanceof EntityPlayer && this.isValid(o)) {
                entity = o;
                pos = this.entityListener.getEntityLowerBounds().get(entity);
                if (pos != null && !this.isOnScreen(pos) && (!RenderUtil.isInViewFrustrum((Entity)entity) || !this.offscreenOnly.getValue())) {
                    if (this.colorSync.getValue()) {
                        // new(java.awt.Color.class)
                        new Color(Colors.INSTANCE.getCurrentColor().getRed(),  Colors.INSTANCE.getCurrentColor().getGreen(),  Colors.INSTANCE.getCurrentColor().getBlue(),  (int)MathHelper.clamp(255.0f - 255.0f / this.fadeDistance.getValue() * ArrowESP.mc.player.getDistance((Entity)entity),  100.0f,  255.0f));
                    }
                    else {
                        color2 = EntityUtil.getColor((Entity)entity,  this.red.getValue(),  this.green.getValue(),  this.blue.getValue(),  (int)MathHelper.clamp(255.0f - 255.0f / this.fadeDistance.getValue() * ArrowESP.mc.player.getDistance((Entity)entity),  100.0f,  255.0f),  true);
                    }
                    color = color2;
                    x = Display.getWidth() / 2 / ((ArrowESP.mc.gameSettings.guiScale == 0) ? 1 : ArrowESP.mc.gameSettings.guiScale);
                    y = Display.getHeight() / 2 / ((ArrowESP.mc.gameSettings.guiScale == 0) ? 1 : ArrowESP.mc.gameSettings.guiScale);
                    yaw = this.getRotations((EntityLivingBase)entity) - ArrowESP.mc.player.rotationYaw;
                    GL11.glTranslatef((float)x,  (float)y,  0.0f);
                    GL11.glRotatef(yaw,  0.0f,  0.0f,  1.0f);
                    GL11.glTranslatef((float)(-x),  (float)(-y),  0.0f);
                    RenderUtil.drawTracerPointer((float)x,  (float)(y - this.radius.getValue()),  this.size.getValue(),  2.0f,  1.0f,  this.outline.getValue(),  this.outlineWidth.getValue(),  color.getRGB());
                    GL11.glTranslatef((float)x,  (float)y,  0.0f);
                    GL11.glRotatef(-yaw,  0.0f,  0.0f,  1.0f);
                    GL11.glTranslatef((float)(-x),  (float)(-y),  0.0f);
                }
            }
        });
    }
    
    private boolean isOnScreen(final Vec3d pos) {
        if (pos.x <= -1.0) {
            return false;
        }
        if (pos.y >= 1.0) {
            return false;
        }
        final int n = (ArrowESP.mc.gameSettings.guiScale == 0) ? 1 : ArrowESP.mc.gameSettings.guiScale;
        if (pos.x / n < 0.0) {
            return false;
        }
        final int n2 = (ArrowESP.mc.gameSettings.guiScale == 0) ? 1 : ArrowESP.mc.gameSettings.guiScale;
        if (pos.x / n2 > Display.getWidth()) {
            return false;
        }
        final int n3 = (ArrowESP.mc.gameSettings.guiScale == 0) ? 1 : ArrowESP.mc.gameSettings.guiScale;
        if (pos.y / n3 < 0.0) {
            return false;
        }
        final int n4 = (ArrowESP.mc.gameSettings.guiScale == 0) ? 1 : ArrowESP.mc.gameSettings.guiScale;
        return pos.y / n4 <= Display.getHeight();
    }
    
    private boolean isValid(final EntityPlayer entity) {
        return entity != ArrowESP.mc.player && (!entity.isInvisible() || this.invisibles.getValue()) && entity.isEntityAlive();
    }
    
    private float getRotations(final EntityLivingBase ent) {
        final double x = ent.posX - ArrowESP.mc.player.posX;
        final double z = ent.posZ - ArrowESP.mc.player.posZ;
        return (float)(-(Math.atan2(x,  z) * 57.29577951308232));
    }
    
    private static class EntityListener
    {
        private final Map<Entity,  Vec3d> entityUpperBounds;
        private final Map<Entity,  Vec3d> entityLowerBounds;
        
        private EntityListener() {
            this.entityUpperBounds = (Map<Entity,  Vec3d>)Maps.newHashMap();
            this.entityLowerBounds = (Map<Entity,  Vec3d>)Maps.newHashMap();
        }
        
        private void render() {
            if (!this.entityUpperBounds.isEmpty()) {
                this.entityUpperBounds.clear();
            }
            if (!this.entityLowerBounds.isEmpty()) {
                this.entityLowerBounds.clear();
            }
            for (final Entity e : Util.mc.world.loadedEntityList) {
                final Vec3d bound = this.getEntityRenderPosition(e);
                bound.add(new Vec3d(0.0,  e.height + 0.2,  0.0));
                final Vec3d upperBounds = RenderUtil.to2D(bound.x,  bound.y,  bound.z);
                final Vec3d lowerBounds = RenderUtil.to2D(bound.x,  bound.y - 2.0,  bound.z);
                if (upperBounds != null) {
                    if (lowerBounds == null) {
                        continue;
                    }
                    this.entityUpperBounds.put(e,  upperBounds);
                    this.entityLowerBounds.put(e,  lowerBounds);
                }
            }
        }
        
        private Vec3d getEntityRenderPosition(final Entity entity) {
            final double partial = Util.mc.timer.renderPartialTicks;
            final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partial - Util.mc.getRenderManager().viewerPosX;
            final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partial - Util.mc.getRenderManager().viewerPosY;
            final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partial - Util.mc.getRenderManager().viewerPosZ;
            return new Vec3d(x,  y,  z);
        }
        
        public Map<Entity,  Vec3d> getEntityLowerBounds() {
            return this.entityLowerBounds;
        }
    }
}
