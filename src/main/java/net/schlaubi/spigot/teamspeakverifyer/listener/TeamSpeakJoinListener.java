package net.schlaubi.spigot.teamspeakverifyer.listener;

import com.github.theholywaffle.teamspeak3.api.event.*;
import net.schlaubi.spigot.teamspeakverifyer.Main;
import net.schlaubi.util.MySQL;
import org.bukkit.configuration.file.FileConfiguration;


public class TeamSpeakJoinListener extends TS3EventAdapter{


    @Override
    public void onClientJoin(ClientJoinEvent e) {
        FileConfiguration cfg = Main.getConfiguration();
        if(!MySQL.userExists(String.valueOf(e.getUniqueClientIdentifier()))) {
            Main.api.sendPrivateMessage(e.getClientId(), cfg.getString("Messages.ts3join").replace("%user%", e.getClientNickname()));
        }
    }
}
