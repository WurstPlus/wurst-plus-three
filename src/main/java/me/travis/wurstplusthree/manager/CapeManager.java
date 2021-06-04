package me.travis.wurstplusthree.manager;

import me.travis.wurstplusthree.util.Globals;
import me.travis.wurstplusthree.util.elements.Pair;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class CapeManager implements Globals {

    private final List<UUID> ogCapes = new ArrayList<>();
    private final List<Pair<UUID, BufferedImage>> donatorCapes = new ArrayList<>();
    private final List<UUID> poggersCapes = new ArrayList<>();
    private final List<UUID> contributorCapes = new ArrayList<>();

    private final List<ResourceLocation> capeFrames = new ArrayList<>();

    public static int capeFrameCount = 0;

    static class gifCapeCounter extends TimerTask {
        @Override
        public void run() {
            capeFrameCount++;
        }
    }

    public ResourceLocation getGifCape() {
        return capeFrames.get(capeFrameCount % 35);
    }

    public CapeManager() {
        Timer timer = new Timer();
        timer.schedule(new gifCapeCounter(), 0, 41);

        for (int i = 0; i < 35; i++) {
            capeFrames.add(new ResourceLocation("textures/gifcape/cape-" + i + ".png"));
        }

        try { // og
            URL capesList = new URL("https://raw.githubusercontent.com/WurstPlus/capes/main/ogs.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                ogCapes.add(UUID.fromString(inputLine));
            }
        } catch (Exception ignored) {}
        try { // dev
            URL capesList = new URL("https://raw.githubusercontent.com/WurstPlus/capes/main/dev.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                contributorCapes.add(UUID.fromString(inputLine));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try { // cool dudes
            URL capesList = new URL("https://raw.githubusercontent.com/WurstPlus/capes/main/cooldude.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                poggersCapes.add(UUID.fromString(inputLine));
            }
        } catch (Exception ignored) {}
        try { // donator
            File tmp = new File("Wurstplus3"+ File.separator + "capes");
            if (!tmp.exists()) {
                tmp.mkdirs();
            }
            URL capesList = new URL("https://raw.githubusercontent.com/WurstPlus/capes/main/donator.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String colune = inputLine.trim();
                String uuid = colune.split(":")[0];
                String cape = colune.split(":")[1];
                URL capeUrl = new URL("https://raw.githubusercontent.com/WurstPlus/capes/main/capes/" + cape + ".png");
                BufferedImage capeImage = ImageIO.read(capeUrl);
                ImageIO.write(capeImage, "png", new File("Wurstplus3/capes/" + uuid + ".png"));
                donatorCapes.add(new Pair<>(UUID.fromString(uuid), capeImage));
            }
        } catch (Exception ignored) {}
    }

    public void reload(){
        try { // og
            URL capesList = new URL("https://raw.githubusercontent.com/WurstPlus/capes/main/ogs.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                ogCapes.add(UUID.fromString(inputLine));
            }
        } catch (Exception ignored) {}
        try { // dev
            URL capesList = new URL("https://raw.githubusercontent.com/WurstPlus/capes/main/dev.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                contributorCapes.add(UUID.fromString(inputLine));
            }
        } catch (Exception ignored) {}
        try { // cool dudes
            URL capesList = new URL("https://raw.githubusercontent.com/WurstPlus/capes/main/cooldude.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                poggersCapes.add(UUID.fromString(inputLine));
            }
        } catch (Exception ignored) {}
        try { // donator
            File tmp = new File("Wurstplus3"+ File.separator + "capes");
            if (!tmp.exists()) {
                tmp.mkdirs();
            }
            URL capesList = new URL("https://raw.githubusercontent.com/WurstPlus/capes/main/donator.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String colune = inputLine.trim();
                String uuid = colune.split(":")[0];
                String cape = colune.split(":")[1];
                URL capeUrl = new URL("https://raw.githubusercontent.com/WurstPlus/capes/main/capes/" + cape + ".png");
                BufferedImage capeImage = ImageIO.read(capeUrl);
                ImageIO.write(capeImage, "png", new File("Wurstplus3/capes/" + uuid + ".png"));
                donatorCapes.add(new Pair<>(UUID.fromString(uuid), capeImage));
            }
        } catch (Exception ignored) {}
    }

    public boolean isOg(UUID uuid) {
        return this.ogCapes.contains(uuid);
    }

    public boolean isDonator(UUID uuid) {
        for (Pair<UUID, BufferedImage> donator : this.donatorCapes) {
            if (donator.getKey().toString().equalsIgnoreCase(uuid.toString())) {
                return true;
            }
        } return false;
    }

    public BufferedImage getCapeFromDonor(UUID uuid) {
        for (Pair<UUID, BufferedImage> donator : this.donatorCapes) {
            if (donator.getKey().toString().equalsIgnoreCase(uuid.toString())) {
                return donator.getValue();
            }
        } return null;
    }

    public boolean isPoggers(UUID uuid) {
        return this.poggersCapes.contains(uuid);
    }

    public boolean isContributor(UUID uuid) {
        return this.contributorCapes.contains(uuid);
    }

}
