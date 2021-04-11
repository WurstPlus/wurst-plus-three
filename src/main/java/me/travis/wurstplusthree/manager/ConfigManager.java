package me.travis.wurstplusthree.manager;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.util.Globals;
import me.travis.wurstplusthree.util.elements.WurstplusPlayer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ConfigManager implements Globals {

    // FOLDERS
    private final String MAIN_FOLDER = "Wurstplus3/";
    private final String CONFIGS_FOLDER = MAIN_FOLDER + "configs/";
    private String ACTIVE_CONFIG_FOLDER = CONFIGS_FOLDER + "default/";

    // STATIC FILES
    private final String CLIENT_FILE = "client.json";
    private final String CONFIG_FILE = "config.txt";
    private final String DRAWN_FILE = "drawn.txt";
    private final String EZ_FILE = "ez.txt";
    private final String ENEMIES_FILE = "enemies.json";
    private final String FRIENDS_FILE = "friends.json";
    private final String HUD_FILE = "hud.json";
    private final String BINDS_FILE = "binds.txt";

    // DIRS
    private final String CLIENT_DIR = MAIN_FOLDER + CLIENT_FILE;
    private final String CONFIG_DIR = MAIN_FOLDER + CONFIG_FILE;
    private final String DRAWN_DIR = MAIN_FOLDER + DRAWN_FILE;
    private final String EZ_DIR = MAIN_FOLDER + EZ_FILE;
    private final String ENEMIES_DIR = MAIN_FOLDER + ENEMIES_FILE;
    private final String FRIENDS_DIR = MAIN_FOLDER + FRIENDS_FILE;
    private final String HUD_DIR = MAIN_FOLDER + HUD_FILE;

    private String CURRENT_CONFIG_DIR = MAIN_FOLDER + CONFIGS_FOLDER + ACTIVE_CONFIG_FOLDER;
    private String BINDS_DIR = CURRENT_CONFIG_DIR + BINDS_FILE;

    // FOLDER PATHS
    private final Path MAIN_FOLDER_PATH = Paths.get(MAIN_FOLDER);
    private final Path CONFIGS_FOLDER_PATH = Paths.get(CONFIGS_FOLDER);
    private Path ACTIVE_CONFIG_FOLDER_PATH = Paths.get(ACTIVE_CONFIG_FOLDER);

    // FILE PATHS
    private final Path CLIENT_PATH = Paths.get(CLIENT_DIR);
    private final Path CONFIG_PATH = Paths.get(CONFIG_DIR);
    private final Path DRAWN_PATH = Paths.get(DRAWN_DIR);
    private final Path EZ_PATH = Paths.get(EZ_DIR);
    private final Path ENEMIES_PATH = Paths.get(ENEMIES_DIR);
    private final Path FRIENDS_PATH = Paths.get(FRIENDS_DIR);
    private final Path HUD_PATH = Paths.get(HUD_DIR);

    private Path BINDS_PATH = Paths.get(BINDS_DIR);
    private Path CURRENT_CONFIG_PATH = Paths.get(CURRENT_CONFIG_DIR);

    public void loadConfig() {
        try {
            this.loadEnemies();
            this.loadFriends();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            this.verifyDir(MAIN_FOLDER_PATH);
            this.verifyDir(CONFIGS_FOLDER_PATH);
            this.verifyDir(ACTIVE_CONFIG_FOLDER_PATH);
            this.saveEnemies();
            this.saveFriends();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean setActiveConfigFolder(String folder) {
        if (folder.equals(this.ACTIVE_CONFIG_FOLDER)) {
            return false;
        }

        this.ACTIVE_CONFIG_FOLDER = CONFIGS_FOLDER + folder;
        this.ACTIVE_CONFIG_FOLDER_PATH = Paths.get(ACTIVE_CONFIG_FOLDER);

        this.CURRENT_CONFIG_DIR = MAIN_FOLDER + CONFIGS_FOLDER + ACTIVE_CONFIG_FOLDER;
        this.CURRENT_CONFIG_PATH = Paths.get(CURRENT_CONFIG_DIR);

        this.BINDS_DIR = CURRENT_CONFIG_DIR + BINDS_FILE;
        this.BINDS_PATH = Paths.get(BINDS_DIR);

        // load_settings();
        return true;
    }

    // LOAD & SAVE PALS

    private void saveFriends() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(WurstplusThree.FRIEND_MANAGER.getFriends());
        OutputStreamWriter file;

        file = new OutputStreamWriter(new FileOutputStream(FRIENDS_DIR), StandardCharsets.UTF_8);
        file.write(json);
        file.close();
    }

    private void loadFriends() throws IOException {
        Gson gson = new Gson();
        Reader reader = Files.newBufferedReader(Paths.get(FRIENDS_DIR));

        WurstplusThree.FRIEND_MANAGER.setFriends(gson.fromJson(reader, new TypeToken<ArrayList<WurstplusPlayer>>(){}.getType()));

        reader.close();
    }

    // LOAD & SAVE ENEMIES

    private void saveEnemies() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(WurstplusThree.ENEMY_MANAGER.getEnemies());

        OutputStreamWriter file;

        file = new OutputStreamWriter(new FileOutputStream(ENEMIES_DIR), StandardCharsets.UTF_8);
        file.write(json);
        file.close();
    }

    private void loadEnemies() throws IOException {
        Gson gson = new Gson();
        Reader reader = Files.newBufferedReader(Paths.get(ENEMIES_DIR));

        WurstplusThree.ENEMY_MANAGER.setEnemies(gson.fromJson(reader, new TypeToken<ArrayList<WurstplusPlayer>>(){}.getType()));

        reader.close();
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
