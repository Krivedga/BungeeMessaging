package yurelax.dev.bungeemessaging;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import yurelax.dev.bungeemessaging.config.ConfigManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;


public class EventListener implements Listener {


    @EventHandler
    public void pluginMessageEvent(PluginMessageEvent event) {
        if (ConfigManager.debugMode) System.out.println("Message | " + new Date());
        try {

            ByteArrayInputStream stream = new ByteArrayInputStream(event.getData());
            DataInputStream in = new DataInputStream(stream);

            if (!event.getTag().equals(ConfigManager.channel)) {
                return;
            }

            Server server = (Server) event.getSender();

            String targetServerName = in.readUTF();
            if (ConfigManager.debugMode) System.out.println("targetServerName = " + targetServerName);

            ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(targetServerName);
            if (serverInfo == null) {
                if (ConfigManager.debugMode) System.out.println("Server is not found | " + targetServerName);
                return;
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(byteArrayOutputStream);

            out.writeUTF(server.getInfo().getName());
            out.write(event.getData());
            out.close();

            if (ConfigManager.debugMode) System.out.println("Data sent to " + targetServerName);
            serverInfo.sendData(event.getTag(), byteArrayOutputStream.toByteArray());

        } catch (Exception e) {
            if (ConfigManager.debugMode) e.printStackTrace();
        }

    }
}
