package net.schlaubi.bungee.teamspeakverifyer;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.schlaubi.bungee.teamspeakverifyer.listener.TeamSpeakJoinListener;
import net.schlaubi.bungee.teamspeakverifyer.listener.TeamSpeakMessageListener;
import net.schlaubi.util.MySQL;

import java.io.*;

public class Main extends Plugin {
    public static Main instance;
    public static Configuration configuration;
    public static TS3Api api;
    TS3Query query;
    @Override
    public void onEnable() {
        instance = this;
        LoadConfig();
        MySQL.bungeconnect();
        MySQL.createDatabase();
        connectTeamspeak();
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandTeamspeak("teamspeak"));
    }

    public static void LoadConfig() {
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(loadResource(Main.instance, "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void connectTeamspeak() {
        Configuration cfg = getConfiguration();
        final TS3Config config = new TS3Config();
        config.setHost(cfg.getString("TeamSpeak.ip"));
        config.setQueryPort(cfg.getInt("TeamSpeak.port"));
        query = new TS3Query();
        query.connect();
        api = query.getApi();
        api.login(cfg.getString("TeamSpeak.user"), cfg.getString("TeamSpeak.password"));
        api.selectVirtualServerById(cfg.getInt("TeamSpeak.virtualserver"));
        api.setNickname(cfg.getString("TeamSpeak.name"));
        api.registerAllEvents();
        api.addTS3Listeners(new TeamSpeakJoinListener());
        api.addTS3Listeners(new TeamSpeakMessageListener());

    }

    @Override
    public void onDisable() {
        MySQL.disconnect();
        query.exit();
    }

    private static File loadResource(Plugin plugin, String resource) {
        File folder = plugin.getDataFolder();
        if (!folder.exists())
            folder.mkdir();
        File resourceFile = new File(folder, resource);
        try {
            if (!resourceFile.exists()) {
                resourceFile.createNewFile();
                try (InputStream in = plugin.getResourceAsStream(resource);
                     OutputStream out = new FileOutputStream(resourceFile)) {
                     ByteStreams.copy(in, out);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resourceFile;
    }

    public static Configuration getConfiguration(){
        return configuration;
    }
}
