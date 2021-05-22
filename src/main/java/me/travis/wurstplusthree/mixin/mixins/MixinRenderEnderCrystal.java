package me.travis.wurstplusthree.mixin.mixins;

import me.travis.wurstplusthree.event.events.RenderEntityModelEvent;
import me.travis.wurstplusthree.hack.render.CrystalRender;
import me.travis.wurstplusthree.util.EntityUtil;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.awt.*;

@Mixin(value={RenderEnderCrystal.class})
public class MixinRenderEnderCrystal {
    @Shadow
    @Final
    private static ResourceLocation ENDER_CRYSTAL_TEXTURES;
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
                GlStateManager.color(1.0f, 0.0f, 0.0f, 0.13f);
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

    static {
        new ResourceLocation("textures/glint.png");
    }
}

