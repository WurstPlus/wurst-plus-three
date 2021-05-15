package me.travis.wurstplusthree.mixin.mixins;

import me.travis.wurstplusthree.hack.render.ItemPhysics;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

/**
 * @author Madmegsox1
 * @since 03/05/2021
 */

@Mixin(RenderEntityItem.class)
public abstract class MixinRenderEntityItem extends MixinRenderer<EntityItem>{
    @Shadow
    @Final
    private RenderItem itemRenderer;
    @Shadow
    @Final
    private Random random;

    @Shadow
    public abstract int getModelCount(ItemStack stack);

    @Shadow
    public abstract boolean shouldSpreadItems();

    @Shadow
    public abstract boolean shouldBob();

    @Shadow
    protected abstract ResourceLocation getEntityTexture(EntityItem entity);

    private Minecraft mc = Minecraft.getMinecraft();
    private long tick;
    private double rotation;
    private double formPositiv(final float rotationPitch)
    {
        return (rotationPitch > 0.0f) ? rotationPitch : ((double)(-rotationPitch));
    }


    @Overwrite
    private int transformModelCount(EntityItem itemIn, double x, double y, double z, float p_177077_8_, IBakedModel p_177077_9_)
    {
        if (ItemPhysics.INSTANCE.isEnabled())
        {
            final ItemStack itemstack = itemIn.getItem();
            final Item item2 = itemstack.getItem();
            if (item2 == null)
                return 0;

            final boolean flag = p_177077_9_.isAmbientOcclusion();
            final int i = getModelCount(itemstack);
            final float f2 = 0.0f;
            GlStateManager.translate((float) x, (float) y + f2 + 0.1f, (float) z);
            float f3 = 0.0f;
            if (flag || (mc.getRenderManager().options != null && mc.getRenderManager().options.fancyGraphics))
            {
                GlStateManager.rotate(f3, 0.0f, 1.0f, 0.0f);
            }
            if (!flag)
            {
                f3 = -0.0f * (i - 1) * 0.5f;
                final float f4 = -0.0f * (i - 1) * 0.5f;
                final float f5 = -0.046875f * (i - 1) * 0.5f;
                GlStateManager.translate(f3, f4, f5);
            }
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            return i;
        }

        ItemStack itemstack = itemIn.getItem();
        Item item = itemstack.getItem();

        if (item == null)
        {
            return 0;
        }
        else
        {
            boolean flag = p_177077_9_.isGui3d();
            int i = this.getModelCount(itemstack);
            float f1 = shouldBob() ? MathHelper.sin(((float)itemIn.getAge() + p_177077_8_) / 10.0F + itemIn.hoverStart) * 0.1F + 0.1F : 0;
            float f2 = p_177077_9_.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
            GlStateManager.translate((float)x, (float)y + f1 + 0.25F * f2, (float)z);

            if (flag || this.renderManager.options != null)
            {
                float f3 = (((float)itemIn.getAge() + p_177077_8_) / 20.0F + itemIn.hoverStart) * (180F / (float)Math.PI);
                GlStateManager.rotate(f3, 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            return i;
        }
    }

    @Overwrite
    public void doRender(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if (ItemPhysics.INSTANCE.isEnabled())
        {
            rotation = (System.nanoTime() - tick) / 3000000.0 * 1.0;
            if (!mc.inGameHasFocus) {
                rotation = 0.0;
            }
            final EntityItem entityitem = (EntityItem)entity;
            final ItemStack itemstack = entityitem.getItem();
            if (itemstack.getItem() != null) {
                random.setSeed(187L);
                boolean flag = false;
                if (TextureMap.LOCATION_BLOCKS_TEXTURE != null) {
                    mc.getRenderManager().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                    mc.getRenderManager().renderEngine.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
                    flag = true;
                }
                GlStateManager.enableRescaleNormal();
                GlStateManager.alphaFunc(516, 0.1f);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.pushMatrix();
                final IBakedModel ibakedmodel = itemRenderer.getItemModelMesher().getItemModel(itemstack);
                final int i = transformModelCount(entityitem, x, y, z, partialTicks, ibakedmodel);
                final BlockPos blockpos = new BlockPos(entityitem);
                if (entityitem.rotationPitch > 360.0f) {
                    entityitem.rotationPitch = 0.0f;
                }
                if (entityitem != null && !Double.isNaN(entityitem.posX) && !Double.isNaN(entityitem.posY) && !Double.isNaN(entityitem.posZ) && entityitem.world != null) {
                    if (entityitem.onGround) {
                        if (entityitem.rotationPitch != 0.0f && entityitem.rotationPitch != 90.0f && entityitem.rotationPitch != 180.0f && entityitem.rotationPitch != 270.0f) {
                            final double d0 = formPositiv(entityitem.rotationPitch);
                            final double d2 = formPositiv(entityitem.rotationPitch - 90.0f);
                            final double d3 = formPositiv(entityitem.rotationPitch - 180.0f);
                            final double d4 = formPositiv(entityitem.rotationPitch - 270.0f);
                            if (d0 <= d2 && d0 <= d3 && d0 <= d4) {
                                if (entityitem.rotationPitch < 0.0f) {
                                    entityitem.rotationPitch += (float)rotation;
                                }
                                else {
                                    entityitem.rotationPitch -= (float)rotation;
                                }
                            }
                            if (d2 < d0 && d2 <= d3 && d2 <= d4) {
                                if (entityitem.rotationPitch - 90.0f < 0.0f) {
                                    entityitem.rotationPitch += (float)rotation;
                                }
                                else {
                                    entityitem.rotationPitch -= (float)rotation;
                                }
                            }
                            if (d3 < d2 && d3 < d0 && d3 <= d4) {
                                if (entityitem.rotationPitch - 180.0f < 0.0f) {
                                    entityitem.rotationPitch += (float)rotation;
                                }
                                else {
                                    entityitem.rotationPitch -= (float)rotation;
                                }
                            }
                            if (d4 < d2 && d4 < d3 && d4 < d0) {
                                if (entityitem.rotationPitch - 270.0f < 0.0f) {
                                    entityitem.rotationPitch += (float)rotation;
                                }
                                else {
                                    entityitem.rotationPitch -= (float)rotation;
                                }
                            }
                        }
                    }
                    else {
                        final BlockPos blockpos2 = new BlockPos(entityitem);
                        blockpos2.add(0, 1, 0);
                        final Material material = entityitem.world.getBlockState(blockpos2).getMaterial();
                        final Material material2 = entityitem.world.getBlockState(blockpos).getMaterial();
                        final boolean flag2 = entityitem.isInsideOfMaterial(Material.WATER);
                        final boolean flag3 = entityitem.isInWater();
                        if (flag2 | material == Material.WATER | material2 == Material.WATER | flag3) {
                            entityitem.rotationPitch += (float)(rotation / 4.0);
                        }
                        else {
                            entityitem.rotationPitch += (float)(rotation * 2.0);
                        }
                    }
                }
                GL11.glRotatef(entityitem.rotationYaw, 0.0f, 1.0f, 0.0f);
                GL11.glRotatef(entityitem.rotationPitch + 90.0f, 1.0f, 0.0f, 0.0f);
                for (int j = 0; j < i; ++j) {
                    if (ibakedmodel.isAmbientOcclusion()) {
                        GlStateManager.pushMatrix();
                        GlStateManager.scale(ItemPhysics.INSTANCE.Scaling.getValue(), ItemPhysics.INSTANCE.Scaling.getValue(), ItemPhysics.INSTANCE.Scaling.getValue());
                        itemRenderer.renderItem(itemstack, ibakedmodel);
                        GlStateManager.popMatrix();
                    }
                    else {
                        GlStateManager.pushMatrix();
                        if (j > 0 && shouldSpreadItems()) {
                            GlStateManager.translate(0.0f, 0.0f, 0.046875f * j);
                        }
                        itemRenderer.renderItem(itemstack, ibakedmodel);
                        if (!shouldSpreadItems()) {
                            GlStateManager.translate(0.0f, 0.0f, 0.046875f);
                        }
                        GlStateManager.popMatrix();
                    }
                }
                GlStateManager.popMatrix();
                GlStateManager.disableRescaleNormal();
                GlStateManager.disableBlend();
                mc.getRenderManager().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                if (flag) {
                    mc.getRenderManager().renderEngine.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
                }
            }

            return;
        }

        ItemStack itemstack = entity.getItem();
        int i = itemstack.isEmpty() ? 187 : Item.getIdFromItem(itemstack.getItem()) + itemstack.getMetadata();
        this.random.setSeed((long)i);
        boolean flag = false;

        if (this.bindEntityTexture(entity))
        {
            this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).setBlurMipmap(false, false);
            flag = true;
        }

        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        IBakedModel ibakedmodel = this.itemRenderer.getItemModelWithOverrides(itemstack, entity.world, (EntityLivingBase)null);
        int j = this.transformModelCount(entity, x, y, z, partialTicks, ibakedmodel);
        boolean flag1 = ibakedmodel.isGui3d();

        if (!flag1)
        {
            float f3 = -0.0F * (float)(j - 1) * 0.5F;
            float f4 = -0.0F * (float)(j - 1) * 0.5F;
            float f5 = -0.09375F * (float)(j - 1) * 0.5F;
            GlStateManager.translate(f3, f4, f5);
        }

        if (this.renderOutlines)
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        for (int k = 0; k < j; ++k)
        {
            if (flag1)
            {
                GlStateManager.pushMatrix();

                if (k > 0)
                {
                    float f7 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f9 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f6 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    GlStateManager.translate(shouldSpreadItems() ? f7 : 0, shouldSpreadItems() ? f9 : 0, f6);
                }

                IBakedModel transformedModel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(ibakedmodel, ItemCameraTransforms.TransformType.GROUND, false);
                this.itemRenderer.renderItem(itemstack, transformedModel);
                GlStateManager.popMatrix();
            }
            else
            {
                GlStateManager.pushMatrix();

                if (k > 0)
                {
                    float f8 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    float f10 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    GlStateManager.translate(f8, f10, 0.0F);
                }

                IBakedModel transformedModel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(ibakedmodel, ItemCameraTransforms.TransformType.GROUND, false);
                this.itemRenderer.renderItem(itemstack, transformedModel);
                GlStateManager.popMatrix();
                GlStateManager.translate(0.0F, 0.0F, 0.09375F);
            }
        }

        if (this.renderOutlines)
        {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        this.bindEntityTexture(entity);

        if (flag)
        {
            this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).restoreLastBlurMipmap();
        }
    }
}
