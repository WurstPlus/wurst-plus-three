package me.travis.wurstplusthree.mixin.mixins;

import me.travis.wurstplusthree.hack.hacks.render.Chams;
import me.travis.wurstplusthree.util.ClientMessage;
import me.travis.wurstplusthree.util.ColorUtil;
import me.travis.wurstplusthree.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import scala.collection.parallel.ParIterableLike;


import java.awt.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glPopMatrix;

@Mixin(value={RenderLivingBase.class})
public abstract class MixinRenderLivingBase {
    @Shadow
    protected ModelBase mainModel;

    @Inject(method={"renderModel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V")}, cancellable=true)
    private void renderModel(EntityLivingBase entityLivingBase, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, CallbackInfo info) {
        Chams chams = Chams.INSTANCE;
        if (!chams.isEnabled()) return;
        if (entityLivingBase instanceof EntityOtherPlayerMP && chams.players.getValue() && !chams.pops.containsKey(entityLivingBase.getEntityId()) ||  entityLivingBase instanceof EntityPlayerSP  && chams.players.getValue() && chams.local.getValue() || (EntityUtil.isPassiveMob(entityLivingBase) || EntityUtil.isNeutralMob(entityLivingBase)) && chams.mobs.getValue() || EntityUtil.isHostileMob(entityLivingBase) && chams.monsters.getValue()) {
            if (!chams.texture.getValue()) {
                info.cancel();
            }
            if (chams.transparent.getValue())
                GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);

            glPushMatrix();
            glPushAttrib(GL_ALL_ATTRIB_BITS);

            if (!chams.texture.getValue() && !chams.mode.is("Shine"))
                glDisable(GL_TEXTURE_2D);

            if (chams.blend.getValue())
                glEnable(GL_BLEND);

            if (chams.lighting.getValue())
                glDisable(GL_LIGHTING);

            if (chams.depth.getValue())
                glDepthMask(false);

            if (chams.mode.is("Wire") || chams.mode.is("WireModel")) {
                glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            } else if (chams.mode.is("Normal")) {
                glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            }

            glEnable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
            glLineWidth((float) ((double) chams.width.getValue()));

            if (chams.xqz.getValue()) {
                if (entityLivingBase instanceof EntityOtherPlayerMP || entityLivingBase instanceof EntityPlayerSP) {
                    ColorUtil.setColor(chams.xqzColorPlayer.getValue());
                } else if (EntityUtil.isPassiveMob(entityLivingBase) || EntityUtil.isNeutralMob(entityLivingBase)) {
                    ColorUtil.setColor(chams.xqzColorPlayerMobs.getValue());
                } else {
                    ColorUtil.setColor(chams.xqzColorMonster.getValue());
                }
                GL11.glDisable((int) 2929);
                GL11.glEnable((int) 10754);
                mainModel.render(entityLivingBase, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                if (chams.mode.is("WireModel")) {
                    glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                    mainModel.render(entityLivingBase, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                }
                GL11.glEnable((int) 2929);
                if (entityLivingBase instanceof EntityOtherPlayerMP || entityLivingBase instanceof EntityPlayerSP) {
                    ColorUtil.setColor(chams.highlightColorPlayer.getValue());
                } else if (EntityUtil.isPassiveMob(entityLivingBase) || EntityUtil.isNeutralMob(entityLivingBase)) {
                    ColorUtil.setColor(chams.highlightColorMobs.getValue());
                } else {
                    ColorUtil.setColor(chams.highlightColorMonster.getValue());
                }
                mainModel.render(entityLivingBase, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                if (chams.mode.is("WireModel")) {
                    glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                    mainModel.render(entityLivingBase, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                }
            } else {
                GL11.glDisable((int) 2929);
                GL11.glEnable((int) 10754);
                if (entityLivingBase instanceof EntityOtherPlayerMP || entityLivingBase instanceof EntityPlayerSP) {
                    ColorUtil.setColor(chams.highlightColorPlayer.getValue());
                } else if (EntityUtil.isPassiveMob(entityLivingBase) || EntityUtil.isNeutralMob(entityLivingBase)) {
                    ColorUtil.setColor(chams.highlightColorMobs.getValue());
                } else {
                    ColorUtil.setColor(chams.highlightColorMonster.getValue());
                }
                mainModel.render(entityLivingBase, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                if (chams.mode.is("WireModel")) {
                    glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                    mainModel.render(entityLivingBase, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                }
                GL11.glEnable((int)2929);
            }

            if (chams.lighting.getValue())
                glEnable(GL_LIGHTING);

            if (chams.depth.getValue())
                glDepthMask(true);

            if (chams.blend.getValue())
                glDisable(GL_BLEND);

            if (!chams.texture.getValue() && !chams.mode.is("Shine"))
                glEnable(GL_TEXTURE_2D);

            glPopAttrib();
            glPopMatrix();
        } else if (chams.pops.containsKey(entityLivingBase.getEntityId())) {
            if (chams.pops.get(entityLivingBase.getEntityId()) == 0) {
                Minecraft.getMinecraft().world.removeEntityFromWorld(entityLivingBase.entityId);
            } else if (chams.pops.get(entityLivingBase.getEntityId()) < 0) {
                //this is retarted but it doesnt instantly stop rendering sorry for messy code dont remove this
                if (chams.pops.get(entityLivingBase.getEntityId()) < -5)
                    chams.pops.remove(entityLivingBase.getEntityId());
                return;
            }
            info.cancel();
            GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            glPushMatrix();
            glPushAttrib(GL_ALL_ATTRIB_BITS);
            glDisable(GL_TEXTURE_2D);
            glEnable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
            GL11.glDisable((int) 2929);
            GL11.glEnable((int) 10754);
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            ColorUtil.setColor(new Color(chams.popChamsColors.getValue().getRed(), chams.popChamsColors.getValue().getGreen(), chams.popChamsColors.getValue().getBlue(), chams.pops.get(entityLivingBase.getEntityId())));
            mainModel.render(entityLivingBase, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
            GL11.glEnable((int)2929);
            glEnable(GL_TEXTURE_2D);
            glPopAttrib();
            glPopMatrix();
            chams.pops.computeIfPresent(entityLivingBase.getEntityId(), (key, oldValue) -> oldValue - 1);
        }
    }
}
