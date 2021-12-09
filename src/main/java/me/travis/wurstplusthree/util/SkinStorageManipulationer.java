package me.travis.wurstplusthree.util;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author Madmegsox1
 * @since 28/04/2021
 */

public class SkinStorageManipulationer {
    public static ResourceLocation getTexture() {
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(new File("Wurstplus3/tmp/skin.png"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        DynamicTexture texture = new DynamicTexture(bufferedImage);
        WrappedResource wr = new WrappedResource(FMLClientHandler.instance().getClient().getTextureManager().getDynamicTextureLocation("skin.png", texture));
        return wr.location;
    }

    public static class WrappedResource {
        public final ResourceLocation location;

        public WrappedResource(ResourceLocation location) {
            this.location = location;
        }
    }
}
