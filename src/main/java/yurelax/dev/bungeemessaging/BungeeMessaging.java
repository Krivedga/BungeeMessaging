package yurelax.dev.bungeemessaging;

import net.md_5.bungee.api.plugin.Plugin;
import yurelax.dev.bungeemessaging.config.ConfigManager;

public final class BungeeMessaging extends Plugin {
    private static BungeeMessaging instance;
    public static ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager();
        configManager.init();
        configManager.scan();
        if (ConfigManager.debugMode) {
            System.out.println("BungeeMessaging | Debug Mode Enabled");
        }
        getProxy().registerChannel(ConfigManager.channel);
        getProxy().getPluginManager().registerListener(this, new EventListener());

    }

    @Override
    public void onDisable() {
    }

    public static BungeeMessaging getInstance() {
        return instance;
    }
}
