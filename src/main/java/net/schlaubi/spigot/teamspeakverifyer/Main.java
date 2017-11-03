package net.schlaubi.spigot.teamspeakverifyer;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import net.milkbowl.vault.permission.Permission;
import net.schlaubi.spigot.teamspeakverifyer.listener.TeamSpeakMessageListener;
import net.schlaubi.spigot.teamspeakverifyer.listener.TeamSpeakJoinListener;
import net.schlaubi.util.MySQL;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Main extends JavaPlugin {
    public static Main instance;
    public static TS3Api api;
    private static Permission perms;
    TS3Query query;


    @Override
    public void onEnable() {
        instance = this;
        loadConfig();
        connectTeamSpeak();
        MySQL.connect();
        MySQL.createDatabase();
        setupPermissiones();
        getCommand("teamspeak").setExecutor(new CommandTeamspeak());
        TeamSpeakMessageListener.users.clear();

    }

    private boolean setupPermissiones() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public static Permission getPermissions(){
        return perms;
    }

    @Override
    public void onDisable() {
        MySQL.disconnect();
        query.exit();
    }

    private void connectTeamSpeak() {
        FileConfiguration cfg = getConfiguration();
            final TS3Config config = new TS3Config();
            config.setHost(cfg.getString("TeamSpeak.ip"));
            config.setQueryPort(Integer.parseInt(cfg.getString("TeamSpeak.port")));
            query = new TS3Query(config);
            query.connect();

            api = query.getApi();
            api.login(cfg.getString("TeamSpeak.user"), cfg.getString("TeamSpeak.password"));
            api.selectVirtualServerById(cfg.getInt("TeamSpeak.virtualserver"));
            api.setNickname(cfg.getString("TeamSpeak.name"));
            api.registerAllEvents();
            api.addTS3Listeners(new TeamSpeakJoinListener());
            api.addTS3Listeners(new TeamSpeakMessageListener());




    }

    private void loadConfig() {
        File f = new File("plugins/TeamspeakVerifyer", "config.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        if(!f.exists())
            saveDefaultConfig();
        try {
            cfg.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FileConfiguration getConfiguration(){
        File f = new File("plugins/TeamspeakVerifyer", "config.yml");
        return YamlConfiguration.loadConfiguration(f);
    }
}
