package me.travis.wurstplusthree.plugin;

import me.travis.wurstplusthree.WurstplusThree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.jar.JarFile;
import java.lang.reflect.Method;

public class Loader {
    File pluginDir;
    File[] pluginsFiles;
    ArrayList<String> classes;
    ArrayList<URL> urls;
    ArrayList<String> names;

    public Loader() {
        pluginDir = new File(WurstplusThree.CONFIG_MANAGER.pluginFolder);
        if (!pluginDir.exists()) pluginDir.mkdir();
        pluginsFiles = pluginDir.listFiles((dir, name) -> name.endsWith(".jar"));
        loadClasses();
    }


    public void loadClasses() {
        if (pluginsFiles != null && pluginsFiles.length > 0) {
            classes = new ArrayList<>();
            urls = new ArrayList<>(pluginsFiles.length);
            names = new ArrayList<>();
            for(File file : pluginsFiles){
                try {
                    JarFile pluginJar = new JarFile(file);
                    pluginJar.stream().forEach(jarEntry -> {
                        if (jarEntry.getName().endsWith(".class")) {
                            classes.add(jarEntry.getName());
                        }
                        if(jarEntry.getName().endsWith(".wplugin")){
                            WurstplusThree.LOGGER.info("file exists");
                            try { //TODO fix this
                                Scanner scanner = new Scanner(file);
                                while (scanner.hasNext()){
                                    WurstplusThree.LOGGER.info(scanner.nextLine());
                                    String line = scanner.nextLine();
                                    String[] split = line.split(":");
                                    if(split[0].equals("name")){
                                        names.add(split[1]);
                                    }
                                }
                            } catch (FileNotFoundException e) {
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
                    if (cls.getName().equals("org.wurst.plugin.Main")) {
                        for (Method method : cls.getMethods()) {
                            if(method.getName().equals("init")) {
                                method.setAccessible(true);
                                Arrays.stream(method.getParameterTypes()).forEach(c -> {
                                    WurstplusThree.LOGGER.info(c.getName());
                                });
                                method.invoke(cls);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            for(String name : names){
                WurstplusThree.LOGGER.info("Loaded -> " + name);
            }
        }
    }
}
