package me.travis.wurstplusthree.hack.player;

import com.google.gson.Gson;
import me.travis.wurstplusthree.command.commands.PlayerSpooferCommand;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.util.ClientMessage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Madmegsox1
 * @since 28/04/2021
 */

public class PlayerSpoofer extends Hack {
    public PlayerSpoofer() {
        super("PlayerSpoofer", "spoofs you name and skin", Category.PLAYER, false);
    }

    public File tmp;
    public static PlayerSpoofer INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        BufferedImage image = null;
        try {
            this.tmp = new File("Wurstplus3"+ File.separator + "tmp");
            if (!this.tmp.exists()) {
                this.tmp.mkdirs();
            }
            Gson gson = new Gson();
            if (PlayerSpooferCommand.name == null) {
                ClientMessage.sendErrorMessage("Please set the player name!");
                this.disable();
            }
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + PlayerSpooferCommand.name);
            Reader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            Map<?, ?> map = (Map<?, ?>) gson.fromJson(reader, Map.class);
            ConcurrentHashMap<String, String> valsMap = new ConcurrentHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key = (String) entry.getKey();
                String val = (String) entry.getValue();
                valsMap.put(key, val);
            }
            reader.close();
            String uuid = valsMap.get("id");
            URL url2 = new URL("https://mc-heads.net/skin/" + uuid);
            image = ImageIO.read(url2);
            ImageIO.write(image, "png", new File("Wurstplus3/tmp/skin.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        deleteSkinChangerFiles();
    }
    public void deleteSkinChangerFiles() {
        for (File file : mc.gameDir.listFiles()) {
            if (!file.isDirectory() && file.getName().contains("-skinchanger")) file.delete();
        }
    }
    public String getOldName(){
        return mc.getSession().getUsername();
    }
}
