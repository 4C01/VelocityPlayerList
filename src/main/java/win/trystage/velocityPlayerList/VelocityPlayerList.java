package win.trystage.velocityPlayerList;

import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Plugin(id = "velocity-player-list", name = "VelocityPlayerList", version = "1.0-SNAPSHOT", description = "show velocity player list", authors = {"TrystageBedwars", "zyghit"})
public class VelocityPlayerList {
    private static Logger logger;
    private static ProxyServer proxyServer;
    private static MiniMessage miniMessage = MiniMessage.miniMessage();
    private static Path dataDirectory;
    private static Toml config;
    @Inject
    public VelocityPlayerList(Logger logger, @DataDirectory Path dataDirectory, ProxyServer proxyServer, CommandManager commandManager){
        this.logger = logger;
        this.proxyServer = proxyServer;
        this.dataDirectory = dataDirectory;
        commandManager.register(
                commandManager.metaBuilder("onlines").build(), // 命令元数据
                new CommandOnlines()
        );
        commandManager.register(
                commandManager.metaBuilder("vtp").build(), // 命令元数据
                new CommandVTP()
        );
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        loadConfig();
    }
    public static void loadConfig() {
        try {
            File configFile = new File(dataDirectory.toFile(), "config.toml");
            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                Files.copy(VelocityPlayerList.class.getResourceAsStream("/config.toml"), configFile.toPath());
            }
            config = new Toml().read(configFile);


            logger.info("Successfully loaded config file");
        } catch (IOException e) {
            logger.error("Could not load config file ", e);
        }
    }

    @Subscribe
    public void onPlayerJoin(LoginEvent event){
        Player player = event.getPlayer();
        // announce if enabled
        if(config.getBoolean("options.player-join-notify")) {
            for(Player target : proxyServer.getAllPlayers()){
                if (target.hasPermission("vpl.announce")) {
                    target.sendMessage(miniMessage.deserialize(config.getString("message.join").replace("{player}",player.getUsername())));
                }
            }
        }
    }
    @Subscribe
    public void onPlayerLeave(DisconnectEvent event){
        Player player = event.getPlayer();
        // announce if enabled
        if(config.getBoolean("options.player-leave-notify")) {
            for(Player target : proxyServer.getAllPlayers()){
                if (target.hasPermission("vpl.announce")) {
                    target.sendMessage(miniMessage.deserialize(config.getString("message.leave").replace("{player}",player.getUsername())));
                }
            }
        }
    }

    // bunch of gets
    public static Toml getConfig() {
        return config;
    }

    public static MiniMessage getMiniMessage() {
        return miniMessage;
    }

    public static ProxyServer getProxyServer () {
        return proxyServer;
    }
}
