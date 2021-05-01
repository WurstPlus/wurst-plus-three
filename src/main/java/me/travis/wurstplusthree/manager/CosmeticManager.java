package me.travis.wurstplusthree.manager;

import me.travis.wurstplusthree.util.elements.cosmetics.GlassesModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.player.EntityPlayer;
import scala.actors.threadpool.Arrays;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Madmegsox1
 * @since 01/05/2021
 */

public class CosmeticManager {
    private final Minecraft mc = Minecraft.getMinecraft();
    public Map<String, List<ModelBase>> cosmeticMap = new HashMap<>();

    public CosmeticManager(){
        this.cosmeticMap.put("da20a139-54f7-4319-9f6a-a76330d658d0", Arrays.asList(new ModelBase[]{new GlassesModel(),}));
    }


    public List<ModelBase> getRenderModels(EntityPlayer player) {
        return this.cosmeticMap.get(player.getUniqueID().toString());
    }

    public boolean hasCosmetics(EntityPlayer player) {
        return this.cosmeticMap.containsKey(player.getUniqueID().toString());
    }

}
