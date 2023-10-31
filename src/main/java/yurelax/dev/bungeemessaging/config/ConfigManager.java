package yurelax.dev.bungeemessaging.config;


import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import yurelax.dev.bungeemessaging.BungeeMessaging;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ConfigManager {

    @ConfigAnnotation(path = "utils.debugMode")
    public static boolean debugMode = false;
    @ConfigAnnotation(path = "channel")
    public static String channel = "servermessaging:main";
    private Configuration config;
    private File yurBungeeCordFile;

//    public static FileConfiguration config;

    public void init() {
        loadFile();

    }



    private List<Object> scanned = new ArrayList<>();

    private void loadFile() {
        File dataFolder = BungeeMessaging.getInstance().getDataFolder();
        if (!dataFolder.exists()) {
            try {
                dataFolder.mkdir();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        yurBungeeCordFile = new File(BungeeMessaging.getInstance().getDataFolder(), "config.yml");
        try {
            if (!yurBungeeCordFile.exists()) {
                boolean newFile = yurBungeeCordFile.createNewFile();
            }
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(yurBungeeCordFile);

        } catch (IOException e) {
            throw new RuntimeException("Config is not loaded " + e);
        }
        saveConfig();
    }

    public void scan() {
        for (Object o : scanned) {
            scanClass(o);
        }
        scanClass(this);
        saveConfig();
    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(BungeeMessaging.getInstance().getDataFolder(), "config.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public void reloadConfig() {
        loadFile();
        scan();
    }

    public void scanClass(Object object) {
        if (!scanned.contains(object)) {
            scanned.add(object);
        }
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigAnnotation.class)) {
                ConfigAnnotation annotation = field.getAnnotation(ConfigAnnotation.class);
                String path = annotation.path();
                try {
                    Class<?> fieldType = field.getType();
                    Object value = config.get(path);
                    Object curValue = field.get(fieldType);
                    if (value == null) {
                        if (curValue != null) {
                            config.set(path, curValue);
                        }
                    } else {
                        field.set(this, value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        saveConfig();
    }


}
