package me.travis.wurstplusthree.hack.client;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.util.elements.cosmetics.GlassesModel;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

/**
 * @author Madmegsox1
 * @since 01/05/2021
 */

@Hack.Registration(name = "Cosmetics", description = "Renders swag stuff on sawg ppl", category = Hack.Category.CLIENT, isListening = false)
public class Cosmetics extends Hack {

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Post event) {
        if (!WurstplusThree.COSMETIC_MANAGER.hasCosmetics(event.getEntityPlayer())) return;
        ModelBase model = WurstplusThree.COSMETIC_MANAGER.getRenderModels(event.getEntityPlayer());
        double rotate = this.interpolate(event.getEntityPlayer().prevRotationYawHead, event.getEntityPlayer().rotationYawHead, event.getPartialRenderTick());
        double rotate1 = this.interpolate(event.getEntityPlayer().prevRotationPitch, event.getEntityPlayer().rotationPitch, event.getPartialRenderTick());
        double scale = 1.0;
        GlStateManager.pushMatrix();
        GlStateManager.translate((double) event.getX(), (double) event.getY(), (double) event.getZ());
        GL11.glScaled((double)(-scale), (double)(-scale), (double)scale);
        GL11.glTranslated((double)0.0, (double)(-((double)event.getEntityPlayer().height - (event.getEntityPlayer().isSneaking() ? 0.25 : 0.0) - 0.38) / scale), (double)0.0);
        GL11.glRotated((double)(180.0 + rotate), (double)0.0, (double)1.0, (double)0.0);
        GL11.glRotated((double)rotate1, (double)1.0, (double)0.0, (double)0.0);
        GlStateManager.translate((double)0.0, (double)-0.45, (double)0.0);
        if(model instanceof GlassesModel) {
            GlassesModel gm = new GlassesModel();
            mc.getTextureManager().bindTexture(gm.glassesTexture);
            gm.render(event.getEntity(), 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
            mc.getTextureManager().deleteTexture(gm.glassesTexture);
            GlStateManager.popMatrix();
        }
    }

    public float interpolate(float yaw1, float yaw2, float percent) {
        float rotation = (yaw1 + (yaw2 - yaw1) * percent) % 360.0f;
        if (rotation < 0.0f) {
            rotation += 360.0f;
        }
        return rotation;
    }
}
