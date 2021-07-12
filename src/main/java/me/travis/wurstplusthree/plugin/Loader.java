package me.travis.wurstplusthree.plugin;

import me.travis.wurstplusthree.WurstplusThree;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.jar.JarFile;

public class Loader {
    File pluginDir;
    File[] pluginsFiles;
    ArrayList<String> classes;
    ArrayList<URL> urls;
    ArrayList<String> names;
    ArrayList<Plugin> plugins;

    public Loader() {
        WurstplusThree.LOGGER.info("Loading Plugins");
        pluginDir = new File(WurstplusThree.CONFIG_MANAGER.pluginFolder);
        if (!pluginDir.exists()) pluginDir.mkdir();
        pluginsFiles = pluginDir.listFiles((dir, name) -> name.endsWith(".jar"));
        plugins = new ArrayList<>();
        loadClasses();
    }


    public void loadClasses() {
        if (pluginsFiles != null && pluginsFiles.length > 0) {
            classes = new ArrayList<>();
            urls = new ArrayList<>(pluginsFiles.length);
            names = new ArrayList<>();
            for (File file : pluginsFiles) {
                try {
                    JarFile pluginJar = new JarFile(file);
                    pluginJar.stream().forEach(jarEntry -> {
                        if (jarEntry.getName().endsWith(".class")) {
                            classes.add(jarEntry.getName());
                        }
                        if (jarEntry.getName().equals("config.json")) { // loads the config json
                            try {
                                URLClassLoader classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()});
                                InputStream s = classLoader.getResourceAsStream("config.json");
                                InputStreamReader streamReader = new InputStreamReader(s);

                                BufferedReader stream = new BufferedReader(streamReader);
                                String line;
                                while ((line = stream.readLine()) != null) {
                                    String[] split = line.split(":");
                                    if (split[0].equals("name")) {
                                        names.add(split[1]);
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    URL url = file.toURI().toURL();
                    urls.add(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]));
            classes.spliterator().forEachRemaining(name -> {
                try {
                    Class cls = classLoader.loadClass(name.replaceAll("/", ".").replace(".class", ""));
                    Class[] ife = cls.getInterfaces();
                    WurstplusThree.LOGGER.info(ife[0].getName());
                    if (ife[0].equals(Plugin.class)) {
                        WurstplusThree.LOGGER.info("asd");
                        Plugin plugin = (Plugin) cls.newInstance(); // creates plugin instance
                        plugins.add(plugin);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
        }

        if (!plugins.isEmpty()) {
            WurstplusThree.LOGGER.info(plugins.get(0).name());
            plugins.spliterator().forEachRemaining(Plugin::init);
        }
        if(!names.isEmpty()) {
            for (String name : names) {
                WurstplusThree.LOGGER.info("Loaded -> " + name);
            }
        }
        WurstplusThree.LOGGER.info("Loaded Plugins");
    }

}

