package me.travis.wurstplusthree.manager;

import me.travis.wurstplusthree.util.elements.cosmetics.GlassesModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Madmegsox1
 * @since 01/05/2021
 */

public class CosmeticManager {
    private final Minecraft mc = Minecraft.getMinecraft();
    public Map<String, ModelBase> cosmeticMap = new HashMap<>();
    public static GlassesModel gm = new GlassesModel();

    public CosmeticManager(){
        this.cosmeticMap.put("da20a139-54f7-4319-9f6a-a76330d658d0", gm);
    }


    public ModelBase getRenderModels(EntityPlayer player) {
        return this.cosmeticMap.get(player.getUniqueID().toString());
    }

    public boolean hasCosmetics(EntityPlayer player) {
        return this.cosmeticMap.containsKey(player.getUniqueID().toString());
    }

}
