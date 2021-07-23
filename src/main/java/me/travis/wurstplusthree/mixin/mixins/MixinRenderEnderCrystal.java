package me.travis.wurstplusthree.mixin.mixins;

import me.travis.wurstplusthree.event.events.RenderEntityModelEvent;
import me.travis.wurstplusthree.hack.hacks.render.CrystalRender;
import me.travis.wurstplusthree.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(value={RenderEnderCrystal.class})
public class MixinRenderEnderCrystal {

    @Shadow @Final private ModelBase modelEnderCrystalNoBase;
    @Shadow @Final private ModelBase modelEnderCrystal;

    private static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    @Redirect(method={"doRender"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    public void renderModelBaseHook(ModelBase model, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (CrystalRender.INSTANCE.isEnabled()) {
            GlStateManager.scale(CrystalRender.INSTANCE.scale.getValue().floatValue(), CrystalRender.INSTANCE.scale.getValue().floatValue(), CrystalRender.INSTANCE.scale.getValue().floatValue());
        }
        if (CrystalRender.INSTANCE.isEnabled() && CrystalRender.INSTANCE.wireframe.getValue()) {
            RenderEntityModelEvent event = new RenderEntityModelEvent(0, model, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            CrystalRender.INSTANCE.onRenderModel(event);
        }
        if (CrystalRender.INSTANCE.isEnabled() && CrystalRender.INSTANCE.chams.getValue()) {
            GL11.glPushAttrib(1048575);
            GL11.glDisable(3008);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glLineWidth(1.5f);
            GL11.glEnable(2960);
            if (CrystalRender.INSTANCE.xqz.getValue() && CrystalRender.INSTANCE.throughwalls.getValue()) {
                Color visibleColor;
                Color hiddenColor = EntityUtil.getColor(entity, CrystalRender.INSTANCE.hiddenColour.getValue().getRed(), CrystalRender.INSTANCE.hiddenColour.getValue().getGreen(), CrystalRender.INSTANCE.hiddenColour.getValue().getBlue(), CrystalRender.INSTANCE.hiddenColour.getValue().getAlpha(), true);
                visibleColor = EntityUtil.getColor(entity, CrystalRender.INSTANCE.colour.getValue().getRed(), CrystalRender.INSTANCE.colour.getValue().getGreen(), CrystalRender.INSTANCE.colour.getValue().getBlue(), CrystalRender.INSTANCE.colour.getValue().getAlpha(), true);
                if (CrystalRender.INSTANCE.throughwalls.getValue()) {
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                }
                GL11.glEnable(10754);
                GL11.glColor4f(((float)hiddenColor.getRed() / 255.0f), ((float)hiddenColor.getGreen() / 255.0f), (float)hiddenColor.getBlue() / 255.0f, (float)visibleColor.getAlpha() / 255.0f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                if (CrystalRender.INSTANCE.throughwalls.getValue()) {
                    GL11.glEnable(2929);
                    GL11.glDepthMask(true);
                }
                GL11.glColor4f((float)visibleColor.getRed() / 255.0f, (float)visibleColor.getGreen() / 255.0f, (float)visibleColor.getBlue() / 255.0f, (float)visibleColor.getAlpha() / 255.0f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            } else {
                Color visibleColor;
                visibleColor = EntityUtil.getColor(entity, CrystalRender.INSTANCE.colour.getValue().getRed(), CrystalRender.INSTANCE.colour.getValue().getGreen(), CrystalRender.INSTANCE.colour.getValue().getBlue(), CrystalRender.INSTANCE.colour.getValue().getAlpha(), true);
                if (CrystalRender.INSTANCE.throughwalls.getValue()) {
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                }
                GL11.glEnable(10754);
                GL11.glColor4f(((float)visibleColor.getRed() / 255.0f), ((float)visibleColor.getGreen() / 255.0f), ((float)visibleColor.getBlue() / 255.0f), (float)visibleColor.getAlpha() / 255.0f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                if (CrystalRender.INSTANCE.throughwalls.getValue()) {
                    GL11.glEnable(2929);
                    GL11.glDepthMask(true);
                }
            }
            GL11.glEnable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
            GL11.glEnable(3008);
            GL11.glPopAttrib();
            if (CrystalRender.INSTANCE.glint.getValue()) {
                GL11.glDisable(2929);
                GL11.glDepthMask(false);
                GlStateManager.enableAlpha();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 0.13f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                GlStateManager.disableAlpha();
                GL11.glEnable(2929);
                GL11.glDepthMask(true);
            }

        } else {
            model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
        if (CrystalRender.INSTANCE.isEnabled()) {
            GlStateManager.scale((1.0f / CrystalRender.INSTANCE.scale.getValue().floatValue()), (1.0f / CrystalRender.INSTANCE.scale.getValue().floatValue()), 1.0f / CrystalRender.INSTANCE.scale.getValue().floatValue());
        }
    }


    @Inject(
            method = "doRender",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GlStateManager;popMatrix()V"
            )
    )
    public void renderEffect(EntityEnderCrystal entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci){
        if(CrystalRender.INSTANCE.effect.getValue()){
            float f = (float) entity.ticksExisted + partialTicks;

            float r = (float)entity.innerRotation + partialTicks;
            float r1 = MathHelper.sin(r * 0.2F) / 2.0F + 0.5F;
            r1 = r1 * r1 + r1;

            Minecraft.getMinecraft().renderEngine.bindTexture(ENCHANTED_ITEM_GLINT_RES);
            Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
            GlStateManager.enableBlend();
            GlStateManager.depthFunc(514);
            GlStateManager.depthMask(false);
            GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);

            for (int i = 0; i < 2; ++i)
            {
                GlStateManager.disableLighting();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
                GlStateManager.color(0.38F, 0.19F, 0.608F, 1.0F);
                GlStateManager.matrixMode(5890);
                GlStateManager.loadIdentity();
                GlStateManager.scale(0.33333334F, 0.33333334F, 0.33333334F);
                GlStateManager.rotate(30.0F - (float)i * 60.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.translate(0.0F, f * (0.001F + (float)i * 0.003F) * 20.0F, 0.0F);
                GlStateManager.matrixMode(5888);
                /*
                if(entity.shouldShowBottom()){
                    modelEnderCrystal.render(entity, 0.0F, f * 3.0F, r1 * 0.2F, 0.0F, 0.0F, 0.0625F);
                }else {
                    modelEnderCrystalNoBase.render(entity, 0.0F, f * 3.0F, r1 * 0.2F, 0.0F, 0.0F, 0.0625F);
                }
                 */
                GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            }

            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.enableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.depthFunc(515);
            GlStateManager.disableBlend();
            Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
        }
    }
}

