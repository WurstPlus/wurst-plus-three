package me.travis.wurstplusthree.mixin.mixins;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.PerspectiveEvent;
import me.travis.wurstplusthree.hack.hacks.client.Gui;
import me.travis.wurstplusthree.hack.hacks.render.CameraClip;
import me.travis.wurstplusthree.hack.hacks.render.NoRender;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityRenderer.class})
public abstract class MixinEntityRenderer {
    private boolean injection = true;
    @Shadow
    public ItemStack itemActivationItem;
    @Shadow
    @Final
    public Minecraft mc;
    @Shadow
    public abstract void getMouseOver(float var1);

    @Inject(method={"getMouseOver(F)V"}, at={@At(value="HEAD")}, cancellable=true)
    public void getMouseOverHook(float partialTicks, CallbackInfo info) {
        if (this.injection) {
            info.cancel();
            this.injection = false;
            try {
                this.getMouseOver(partialTicks);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.injection = true;
        }
    }

    @Redirect(method={"setupFog"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/ActiveRenderInfo;getBlockStateAtEntityViewpoint(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;F)Lnet/minecraft/block/state/IBlockState;"))
    public IBlockState getBlockStateAtEntityViewpointHook(World worldIn, Entity entityIn, float p_186703_2_) {
        return ActiveRenderInfo.getBlockStateAtEntityViewpoint(worldIn, entityIn, p_186703_2_);
    }

    @Inject(method={"hurtCameraEffect"}, at={@At(value="HEAD")}, cancellable=true)
    public void hurtCameraEffectHook(float ticks, CallbackInfo info) {
        if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.hurtcam.getValue()) {
            info.cancel();
        }
    }

    @Inject(method={"updateLightmap"}, at={@At(value="HEAD")}, cancellable=true)
    private void updateLightmap(float partialTicks, CallbackInfo info) {
        if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.skylight.getValue()) {
            info.cancel();
        }
    }

    @ModifyVariable(method={"orientCamera"}, ordinal=3, at=@At(value="STORE", ordinal=0), require=1)
    public double changeCameraDistanceHook(double range) {
        if (CameraClip.INSTANCE.isEnabled()) {
            return CameraClip.INSTANCE.distance.getValue();
        } else {
            return range;
        }
    }

    @ModifyVariable(method={"orientCamera"}, ordinal=7, at=@At(value="STORE", ordinal=0), require=1)
    public double orientCameraHook(double range) {
        if (CameraClip.INSTANCE.isEnabled()) {
            return CameraClip.INSTANCE.distance.getValue();
        } else {
            return range;
        }
    }

    @Redirect(method = "setupCameraTransform", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onSetupCameraTransform(float fovy, float aspect, float zNear, float zFar) {
        PerspectiveEvent event = new PerspectiveEvent((float) this.mc.displayWidth / (float) this.mc.displayHeight);
        WurstplusThree.EVENT_PROCESSOR.postEvent(event);
        Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
    }

    @Redirect(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderWorldPass(float fovy, float aspect, float zNear, float zFar) {
        PerspectiveEvent event = new PerspectiveEvent((float) this.mc.displayWidth / (float) this.mc.displayHeight);
        WurstplusThree.EVENT_PROCESSOR.postEvent(event);
        Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
    }

    @Redirect(method = "renderCloudsCheck", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderCloudsCheck(float fovy, float aspect, float zNear, float zFar) {
        PerspectiveEvent event = new PerspectiveEvent((float) this.mc.displayWidth / (float) this.mc.displayHeight);
        WurstplusThree.EVENT_PROCESSOR.postEvent(event);
        Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
    }

    @Redirect(method = "drawNameplate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I"))
    private static int drawString(FontRenderer fontRenderer, String text, int x, int y, int color){
        WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(text, x, y, Gui.INSTANCE.fontColor.getValue().hashCode());
        return 0;
    }

    @Redirect(method = "drawNameplate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;getStringWidth(Ljava/lang/String;)I"))
    private static int getStringWidth(FontRenderer fontRenderer, String text){
        return WurstplusThree.GUI_FONT_MANAGER.getTextWidth(text);
    }

}

