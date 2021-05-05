package me.travis.wurstplusthree.manager;

import me.travis.wurstplusthree.util.elements.cosmetics.GlassesModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.player.EntityPlayer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
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
    public Map<String, ArrayList<ModelBase>> cosmeticMap = new HashMap<>();
    public static GlassesModel gm = new GlassesModel();

    public CosmeticManager(){
        try {
            URL capesList = new URL("https://raw.githubusercontent.com/TrvsF/capes/main/cosmetics.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String colune = inputLine.trim();
                String name = colune.split(":")[0];
                //WurstplusThree.LOGGER.info(name);
                String type = colune.split(":")[1];
                String[] cosmetics = type.split(",");
                ArrayList<ModelBase> cList = new ArrayList<>();
                for(String c : cosmetics){
                    if(c.equals("glasses")){
                        cList.add(gm);
                    }
                }
                this.cosmeticMap.put(name, cList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reload(){
        cosmeticMap.clear();
        try {
            URL capesList = new URL("https://raw.githubusercontent.com/TrvsF/capes/main/cosmetics.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String colune = inputLine.trim();
                String name = colune.split(":")[0];
                String type = colune.split(":")[1];
                String[] cosmetics = type.split(",");
                ArrayList<ModelBase> cList = new ArrayList<>();
                for(String c : cosmetics){
                    if(c.equals("glasses")){
                        cList.add(gm);
                    }
                }
                this.cosmeticMap.put(name, cList);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public ArrayList<ModelBase> getRenderModels(EntityPlayer player) {
        return this.cosmeticMap.get(player.getUniqueID().toString());
    }

    public boolean hasCosmetics(EntityPlayer player) {
        return this.cosmeticMap.containsKey(player.getUniqueID().toString());
    }


}
