package me.travis.wurstplusthree.manager;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.command.Commands;
import me.travis.wurstplusthree.gui.hud.element.HudElement;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.hacks.client.Gui;
import me.travis.wurstplusthree.hack.hacks.client.HudEditor;
import me.travis.wurstplusthree.hack.hacks.combat.Burrow;
import me.travis.wurstplusthree.setting.Setting;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.setting.type.KeySetting;
import me.travis.wurstplusthree.util.ClientMessage;
import me.travis.wurstplusthree.util.Globals;
import me.travis.wurstplusthree.util.WhitelistUtil;
import me.travis.wurstplusthree.util.elements.WurstplusPlayer;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;
import scala.reflect.io.Directory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

public class ConfigManager implements Globals {


    // FOLDERS
    private final String mainFolder = "Wurstplus3/";
    private final String configsFolder = mainFolder + "configs/";
    private String activeConfigFolder = configsFolder + "default/";
    public final String pluginFolder = mainFolder + "plugins/";
    public String configName = "default";

    private final String drawnFile = "drawn.txt";
    private final String enemiesFile = "enemies.json";
    private final String friendsFile = "friends.json";
    private final String fontFile = "font.txt";
    private final String burrowFile = "burrowBlocks.txt";
    private final String IRCtoken = "IRCtoken.dat";
    private final String hudFile = "hud.txt";
    private final String coordsFile = "playersCoords.txt";

    private final String drawnDir = mainFolder + drawnFile;
    private final String fontDir = mainFolder + fontFile;
    private final String burrowDir = mainFolder + burrowFile;
    private final String IRCdir = mainFolder + IRCtoken;
    private final String enemiesDir = mainFolder + enemiesFile;
    private final String friendsDir = mainFolder + friendsFile;
    private final String hudDir = mainFolder + hudFile;
    private final String coordsDir = mainFolder + coordsFile;

    // FOLDER PATHS
    private final Path mainFolderPath = Paths.get(mainFolder);
    private final Path configsFolderPath = Paths.get(configsFolder);
    private Path activeConfigFolderPath = Paths.get(activeConfigFolder);

    private final Path drawnPath = Paths.get(drawnDir);
    private final Path fontPath = Paths.get(fontDir);
    private final Path burrowPath = Paths.get(burrowDir);
    private final Path IRCpath = Paths.get(IRCdir);
    private final Path hudPath = Paths.get(hudDir);
    private final Path coordsPath = Paths.get(coordsDir);

    public void loadConfig() {
        try {
            this.loadEnemies();
            this.loadFriends();
            this.loadSettings();
            this.loadBinds();
            this.loadDrawn();
            this.loadFont();
            this.loadBurrowBlock();
            this.loadHud();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        try {
            this.loadSettings();
            this.loadBinds();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onLogin(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        String serverIP = "@" + mc.getCurrentServerData().serverIP;
        Path path = Paths.get(configsFolder + serverIP + "/");
        if (Files.exists(path)) {
            setActiveConfigFolder(serverIP + "/");
        }
    }

    public void saveConfig() {
        try {
            this.verifyDir(mainFolderPath);
            this.verifyDir(configsFolderPath);
            this.verifyDir(activeConfigFolderPath);

            this.saveHud();
            this.saveEnemies();
            this.saveFriends();
            this.saveSettings();
            this.saveBinds();
            this.saveDrawn();
            this.saveFont();
            this.saveBurrowBlock();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getActiveConfigFolder() {
        return this.activeConfigFolder;
    }
    // CHANGES ACTIVE CONFIG FOLDER

    public boolean setActiveConfigFolder(String folder) {
        if (folder.equals(this.activeConfigFolder)) {
            return false;
        }
        this.saveConfig();
        this.configName = folder.replace("/", "");
        this.activeConfigFolder = configsFolder + folder;
        this.activeConfigFolderPath = Paths.get(activeConfigFolder);
        String currentConfigDir = mainFolder + configsFolder + activeConfigFolder;
        Paths.get(currentConfigDir);
        String bindsFile = "binds.txt";
        String bindsDir = currentConfigDir + bindsFile;
        Paths.get(bindsDir);
        if (!Files.exists(Paths.get(configsFolder + folder))) {
            this.clearSettings();
        }
        this.reloadConfig();
        return true;
    }

    // LOAD & SAVE PALS

    private void saveFriends() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(WurstplusThree.FRIEND_MANAGER.getFriends());
        OutputStreamWriter file;

        file = new OutputStreamWriter(new FileOutputStream(friendsDir), StandardCharsets.UTF_8);
        file.write(json);
        file.close();
    }

    private void loadFriends() throws IOException {
        Gson gson = new Gson();
        Reader reader = Files.newBufferedReader(Paths.get(friendsDir));

        WurstplusThree.FRIEND_MANAGER.setFriends(gson.fromJson(reader, new TypeToken<ArrayList<WurstplusPlayer>>(){}.getType()));

        reader.close();
    }

    // LOAD & SAVE ENEMIES

    private void saveEnemies() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(WurstplusThree.ENEMY_MANAGER.getEnemies());

        OutputStreamWriter file;

        file = new OutputStreamWriter(new FileOutputStream(enemiesDir), StandardCharsets.UTF_8);
        file.write(json);
        file.close();
    }

    private void loadEnemies() throws IOException {
        Gson gson = new Gson();
        Reader reader = Files.newBufferedReader(Paths.get(enemiesDir));

        WurstplusThree.ENEMY_MANAGER.setEnemies(gson.fromJson(reader, new TypeToken<ArrayList<WurstplusPlayer>>(){}.getType()));

        reader.close();
    }

    // LOAD & SAVE SETTINGS

    private void saveSettings() throws IOException {
        for (Hack hack : WurstplusThree.HACKS.getHacks()) {
            String fileName = activeConfigFolder + hack.getName() + ".txt";
            Path filePath = Paths.get(fileName);
            this.deleteFile(fileName);
            verifyFile(filePath);

            File file = new File(fileName);
            BufferedWriter br = new BufferedWriter(new FileWriter(file));

            for (Setting setting : hack.getSettings()) {
                if (setting instanceof ColourSetting) {
                    ColourSetting color = (ColourSetting) setting;
                    br.write(setting.getName() + ":" + color.getValue().getRed() + ":" + color.getValue().getGreen()
                            + ":" + color.getValue().getBlue() + ":" + color.getValue().getAlpha() + ":"
                            + color.getRainbow() + "\r\n");
                }
                else if(setting instanceof KeySetting){
                    KeySetting key = (KeySetting) setting;
                    br.write(setting.getName()+ ":" + key.getKey() + "\r\n");
                }
                else {
                    br.write(setting.getName() + ":" + setting.getValue() + "\r\n");
                }
            }

            br.close();
        }
    }

    private void loadSettings() throws IOException {
        for (Hack hack : WurstplusThree.HACKS.getHacks()) {
            String file_name = activeConfigFolder + hack.getName() + ".txt";
            File file = new File(file_name);
            if (!file.exists()) continue;
            FileInputStream fi_stream = new FileInputStream(file.getAbsolutePath());
            DataInputStream di_stream = new DataInputStream(fi_stream);
            BufferedReader br = new BufferedReader(new InputStreamReader(di_stream));

            String line;
            while ((line = br.readLine()) != null) {

                String colune = line.trim();
                String name = colune.split(":")[0];
                String value = colune.split(":")[1];

                Setting setting = hack.getSettingByName(name);
                if (setting == null) continue;
                switch (setting.getType()) {
                    case "boolean":
                        setting.setValue(Boolean.parseBoolean(value));
                        break;
                    case "colour":
                        try {
                            ColourSetting colourSetting = (ColourSetting) setting;
                            int red = Integer.parseInt(value);
                            int green = Integer.parseInt(colune.split(":")[2]);
                            int blue = Integer.parseInt(colune.split(":")[3]);
                            int alpha = Integer.parseInt(colune.split(":")[4]);
                            boolean rainbow = Boolean.parseBoolean(colune.split(":")[5]);
                            colourSetting.setRainbow(rainbow);
                            colourSetting.setValue(red, green, blue, alpha);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case "double":
                        setting.setValue(Double.parseDouble(value));
                        break;
                    case "enum":
                        setting.setValue(value);
                        break;
                    case "int":
                        setting.setValue(Integer.parseInt(value));
                        break;
                    case "key":
                        KeySetting key = (KeySetting) setting;
                        key.setKey(Integer.parseInt(value));
                        break;
                }
            }

            br.close();
        }
    }

    private void clearSettings() {
        for (Hack hack : WurstplusThree.HACKS.getHacks()) {
            if (hack instanceof Gui || hack instanceof HudEditor) continue;
            hack.setHold(false);
            hack.setEnabled(false);
            hack.setBind(Keyboard.KEY_NONE);
            for (Setting setting : hack.getSettings()) {
                setting.setValue(setting.defaultValue);
            }
        }
    }

    // LOAD & SAVE BINDS

    private void saveBinds() throws IOException {
        final String file_name = activeConfigFolder + "BINDS.txt";
        final Path file_path = Paths.get(file_name);

        this.deleteFile(file_name);
        this.verifyFile(file_path);
        final File file = new File(file_name);
        final BufferedWriter br = new BufferedWriter(new FileWriter(file));
        br.write(Commands.prefix + "\r\n");
        for (Hack module : WurstplusThree.HACKS.getHacks()) {
            br.write(module.getName() + ":" + module.getBind() + ":" + module.isEnabled() + ":" + module.isHold() + "\r\n");
        }
        br.close();
    }

    private void loadBinds() throws IOException {
        final String file_name = activeConfigFolder + "BINDS.txt";
        final File file = new File(file_name);
        final FileInputStream fi_stream = new FileInputStream(file.getAbsolutePath());
        final DataInputStream di_stream = new DataInputStream(fi_stream);
        final BufferedReader br = new BufferedReader(new InputStreamReader(di_stream));
        boolean flag = true;
        String line;
        while ((line = br.readLine()) != null) {
            try {
                if (flag) {
                    Commands.prefix = line;
                    flag = false;
                } else {
                    final String colune = line.trim();
                    final String tag = colune.split(":")[0];
                    final String bind = colune.split(":")[1];
                    final String active = colune.split(":")[2];
                    final String hold = colune.split(":")[3];
                    Hack hack = WurstplusThree.HACKS.getHackByName(tag);
                    hack.setBind(Integer.parseInt(bind));
                    hack.setHold(Boolean.parseBoolean(hold));
                    hack.setEnabled(Boolean.parseBoolean(active));
                }

            } catch (Exception ignored) {}
        }
        br.close();
    }

    private void saveHud() throws IOException {
        FileWriter writer = new FileWriter(hudDir);
        for (HudElement hudElement : WurstplusThree.HUD_MANAGER.getHudElements()) {
            writer.write(hudElement.getName() + ":" + hudElement.getX() + ":" + hudElement.getY() + ":" + hudElement.isEnabled() + System.lineSeparator());
        }
        writer.close();
    }

    private void loadHud() throws IOException {
        for (String hudElement : Files.readAllLines(hudPath).stream().distinct().collect(Collectors.toList())) {
            try {
                String trim = hudElement.trim();
                String name = trim.split(":")[0];
                int x = Integer.parseInt(trim.split(":")[1]);
                int y = Integer.parseInt(trim.split(":")[2]);
                boolean enabled = Boolean.parseBoolean(trim.split(":")[3]);
                HudElement element = WurstplusThree.HUD_MANAGER.getElementByName(name);
                if (element == null) continue;
                element.setX(x);
                element.setY(y);
                element.setEnabled(enabled);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveDrawn() throws IOException {
        FileWriter writer = new FileWriter(drawnDir);
        for (Hack hack : WurstplusThree.HACKS.getDrawnHacks()) {
            writer.write(hack.getName() + System.lineSeparator());
        }
        writer.close();
    }

    private void loadDrawn() throws IOException {
        for (String hackName : Files.readAllLines(drawnPath).stream().distinct().collect(Collectors.toList())) {
            Hack hack = WurstplusThree.HACKS.getHackByName(hackName);
            if (hack == null) continue;
            WurstplusThree.HACKS.addDrawHack(hack);
        }
    }

    private void saveFont() throws IOException {
        FileWriter writer = new FileWriter(fontDir);
        writer.write(WurstplusThree.GUI_FONT_MANAGER.fontName + System.lineSeparator());
        writer.write(WurstplusThree.GUI_FONT_MANAGER.fontSize + System.lineSeparator());
        writer.close();
    }

    private void loadFont() throws IOException {
        boolean flag = true;
        for (String line : Files.readAllLines(fontPath)) {
            if (flag) {
                WurstplusThree.GUI_FONT_MANAGER.setFont(line);
                flag = false;
            } else {
                WurstplusThree.GUI_FONT_MANAGER.setFontSize(Integer.parseInt(line));
                return;
            }
        }
        WurstplusThree.GUI_FONT_MANAGER.setFont();
    }

    public void saveCoords(String coords) throws IOException {
        FileWriter fw = new FileWriter(coordsDir, true);
        BufferedWriter bw = new BufferedWriter(fw);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy -- HH:mm:ss");
        Date date = new Date();
        bw.write("[" + formatter.format(date) + "] " + coords);
        bw.newLine();
        bw.close();
    }

    public void saveBurrowBlock() throws IOException {
        FileWriter writer = new FileWriter(burrowDir);
        //Burrow a = (Burrow) WurstplusThree.HACKS.getHackByName("Burrow");
        String s = WurstplusThree.COMMANDS.getBurrowCommand().getBBlock();
        writer.write(s);
        writer.close();
    }

    private void loadBurrowBlock() throws IOException {
        for (String l : Files.readAllLines(burrowPath)){
            Burrow a = (Burrow) WurstplusThree.HACKS.getHackByName("Burrow");
            new WhitelistUtil();
            a.setBlock(WhitelistUtil.findBlock(l));
            WurstplusThree.COMMANDS.getBurrowCommand().setBBlock(l);
        }
    }

    public void saveIRCtoken(String token) throws IOException {
        FileWriter writer = new FileWriter(IRCdir);
        writer.append( mc.player.getName()+":"+mc.player.getUniqueID()+":"+token);
        writer.close();
    }

    public String loadIRCtoken() throws IOException {
        String line="";
        for (String l : Files.readAllLines(IRCpath)){
            line = l;
            break;
        }
        String[] split = line.split(":");
        return split[2];
    }

    public boolean deleteFile(final String path) throws IOException {
        final File f = new File(path);
        return f.delete();
    }

    public void verifyFile(final Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }

    public void verifyDir(final Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
    }

}
