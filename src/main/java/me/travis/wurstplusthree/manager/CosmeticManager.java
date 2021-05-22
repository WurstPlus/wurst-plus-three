package me.travis.wurstplusthree.manager;

import me.travis.wurstplusthree.util.Globals;
import me.travis.wurstplusthree.util.elements.cosmetics.GlassesModel;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.player.EntityPlayer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Madmegsox1
 * @since 01/05/2021
 */

public class CosmeticManager implements Globals {
    public Map<String, ModelBase> cosmeticMap = new HashMap<>();

    public static GlassesModel gm = new GlassesModel();

    public CosmeticManager() {
        try {
            URL capesList = new URL("https://raw.githubusercontent.com/WurstPlus/capes/main/cosmetics.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String colune = inputLine.trim();
                String name = colune.split(":")[0];
                //WurstplusThree.LOGGER.info(name);
                String type = colune.split(":")[1];
                if (type.equals("glasses")) {
                    this.cosmeticMap.put(name, gm);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        cosmeticMap.clear();
        try {
            URL capesList = new URL("https://raw.githubusercontent.com/WurstPlus/capes/main/cosmetics.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String colune = inputLine.trim();
                String name = colune.split(":")[0];
                //WurstplusThree.LOGGER.info(name);
                String type = colune.split(":")[1];
                if (type.equals("glasses")) {
                    this.cosmeticMap.put(name, gm);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public ModelBase getRenderModels(EntityPlayer player) {
        return this.cosmeticMap.get(player.getUniqueID().toString());
    }

    public boolean hasCosmetics(EntityPlayer player) {
        return this.cosmeticMap.containsKey(player.getUniqueID().toString());
    }


}
