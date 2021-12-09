package me.travis.wurstplusthree.hack.hacks.render;

import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.EntityUtil;
import me.travis.wurstplusthree.util.PlayerUtil;
import me.travis.wurstplusthree.util.RenderUtil;
import me.travis.wurstplusthree.util.elements.Colour;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSourceImpl;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@Hack.Registration(name = "Esp", description = "draws box around shit", category = Hack.Category.RENDER, isListening = false)
public class Esp extends Hack {

    BooleanSetting items = new BooleanSetting("Items", true, this);
    BooleanSetting orbs = new BooleanSetting("Orbs", true, this);
    BooleanSetting bottles = new BooleanSetting("Bottles", true, this);
    BooleanSetting pearl = new BooleanSetting("Pearl", true, this);
    BooleanSetting sources = new BooleanSetting("Sources", false, this);
    IntSetting sourceRange = new IntSetting("Range", 7, 1, 25, this, s -> sources.getValue());
    ColourSetting colour = new ColourSetting("Colour", new Colour(255, 50, 50, 150), this);
    ColourSetting colour2 = new ColourSetting("SourceColour", new Colour(100, 50, 200, 150), this);


    @Override
    public void onRender3D(Render3DEvent event) {
        if(nullCheck())return;
        AxisAlignedBB bb;
        Vec3d interp;
        int i;
        if (this.items.getValue()) {
            i = 0;
            for (Entity entity : mc.world.loadedEntityList) {
                if (!(entity instanceof EntityItem) || !(mc.player.getDistanceSq(entity) < 2500.0)) continue;
                interp = EntityUtil.getInterpolatedRenderPos(entity, mc.getRenderPartialTicks());
                bb = new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().minY - 0.0 - entity.posY + interp.y, entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ + interp.z, entity.getEntityBoundingBox().maxX + 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().maxY + 0.1 - entity.posY + interp.y, entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ + interp.z);
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.disableDepth();
                GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
                GlStateManager.disableTexture2D();
                GlStateManager.depthMask(false);
                GL11.glEnable(2848);
                GL11.glHint(3154, 4354);
                GL11.glLineWidth(1.0f);
                RenderGlobal.renderFilledBox(bb, (float) this.colour.getValue().getRed() / 255.0f, (float) this.colour.getValue().getGreen() / 255.0f, (float) this.colour.getValue().getBlue() / 255.0f, (float) this.colour.getValue().getAlpha() / 255.0f);
                GL11.glDisable(2848);
                GlStateManager.depthMask(true);
                GlStateManager.enableDepth();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                RenderUtil.drawBlockOutline(bb, new Color(this.colour.getValue().getRed(), this.colour.getValue().getGreen(), this.colour.getValue().getBlue(), this.colour.getValue().getAlpha()), 1.0f);
                if (++i < 50) continue;
                break;
            }
        }
        if (this.orbs.getValue()) {
            i = 0;
            for (Entity entity : mc.world.loadedEntityList) {
                if (!(entity instanceof EntityXPOrb) || !(mc.player.getDistanceSq(entity) < 2500.0)) continue;
                interp = EntityUtil.getInterpolatedRenderPos(entity, mc.getRenderPartialTicks());
                bb = new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().minY - 0.0 - entity.posY + interp.y, entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ + interp.z, entity.getEntityBoundingBox().maxX + 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().maxY + 0.1 - entity.posY + interp.y, entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ + interp.z);
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.disableDepth();
                GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
                GlStateManager.disableTexture2D();
                GlStateManager.depthMask(false);
                GL11.glEnable(2848);
                GL11.glHint(3154, 4354);
                GL11.glLineWidth(1.0f);
                RenderGlobal.renderFilledBox(bb, (float) this.colour.getValue().getRed() / 255.0f, (float) this.colour.getValue().getGreen() / 255.0f, (float) this.colour.getValue().getBlue() / 255.0f, (float) this.colour.getValue().getAlpha() / 255.0f);
                GL11.glDisable(2848);
                GlStateManager.depthMask(true);
                GlStateManager.enableDepth();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                RenderUtil.drawBlockOutline(bb, new Color(this.colour.getValue().getRed(), this.colour.getValue().getGreen(), this.colour.getValue().getBlue(), this.colour.getValue().getAlpha()), 1.0f);
                if (++i < 50) continue;
                break;
            }
        }
        if (this.pearl.getValue()) {
            i = 0;
            for (Entity entity : mc.world.loadedEntityList) {
                if (!(entity instanceof EntityEnderPearl) || !(mc.player.getDistanceSq(entity) < 2500.0)) continue;
                interp = EntityUtil.getInterpolatedRenderPos(entity, mc.getRenderPartialTicks());
                bb = new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().minY - 0.0 - entity.posY + interp.y, entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ + interp.z, entity.getEntityBoundingBox().maxX + 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().maxY + 0.1 - entity.posY + interp.y, entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ + interp.z);
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.disableDepth();
                GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
                GlStateManager.disableTexture2D();
                GlStateManager.depthMask(false);
                GL11.glEnable(2848);
                GL11.glHint(3154, 4354);
                GL11.glLineWidth(1.0f);
                RenderGlobal.renderFilledBox(bb, (float) this.colour.getValue().getRed() / 255.0f, (float) this.colour.getValue().getGreen() / 255.0f, (float) this.colour.getValue().getBlue() / 255.0f, (float) this.colour.getValue().getAlpha() / 255.0f);
                GL11.glDisable(2848);
                GlStateManager.depthMask(true);
                GlStateManager.enableDepth();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                RenderUtil.drawBlockOutline(bb, new Color(this.colour.getValue().getRed(), this.colour.getValue().getGreen(), this.colour.getValue().getBlue(), this.colour.getValue().getAlpha()), 1.0f);
                if (++i < 50) continue;
                break;
            }
        }
        if (this.bottles.getValue()) {
            i = 0;
            for (Entity entity : mc.world.loadedEntityList) {
                if (!(entity instanceof EntityExpBottle) || !(mc.player.getDistanceSq(entity) < 2500.0)) continue;
                interp = EntityUtil.getInterpolatedRenderPos(entity, mc.getRenderPartialTicks());
                bb = new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().minY - 0.0 - entity.posY + interp.y, entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ + interp.z, entity.getEntityBoundingBox().maxX + 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().maxY + 0.1 - entity.posY + interp.y, entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ + interp.z);
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.disableDepth();
                GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
                GlStateManager.disableTexture2D();
                GlStateManager.depthMask(false);
                GL11.glEnable(2848);
                GL11.glHint(3154, 4354);
                GL11.glLineWidth(1.0f);
                RenderGlobal.renderFilledBox(bb, (float) this.colour.getValue().getRed() / 255.0f, (float) this.colour.getValue().getGreen() / 255.0f, (float) this.colour.getValue().getBlue() / 255.0f, (float) this.colour.getValue().getAlpha() / 255.0f);
                GL11.glDisable(2848);
                GlStateManager.depthMask(true);
                GlStateManager.enableDepth();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                RenderUtil.drawBlockOutline(bb, new Color(this.colour.getValue().getRed(), this.colour.getValue().getGreen(), this.colour.getValue().getBlue(), this.colour.getValue().getAlpha()), 1.0f);
                if (++i < 50) continue;
                break;
            }
        }
        if(sources.getValue()) {
            for (BlockPos pos : EntityUtil.getSphere(PlayerUtil.getPlayerPos(), sourceRange.getValue(), sourceRange.getValue(), false, true, 0)) {
                if (mc.world.getBlockState(pos).getBlock().getMetaFromState(mc.world.getBlockState(pos)) == 0 && mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid) {
                    RenderUtil.drawBoxESP(pos, colour2.getColor(), colour2.getColor(), 1.0f, true, true, false);
                }
            }
        }
    }

}
