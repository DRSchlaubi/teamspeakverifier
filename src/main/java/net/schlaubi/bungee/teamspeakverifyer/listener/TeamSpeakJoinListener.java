package net.schlaubi.bungee.teamspeakverifyer.listener;

import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import net.md_5.bungee.config.Configuration;

public class TeamSpeakJoinListener extends TS3EventAdapter{

    @Override
    public void onClientJoin(ClientJoinEvent e) {
        Configuration cfg = net.schlaubi.bungee.teamspeakverifyer.Main.getConfiguration();
        net.schlaubi.bungee.teamspeakverifyer.Main.api.sendPrivateMessage(e.getClientId(), cfg.getString("Messages.ts3join").replace("%user%", e.getClientNickname()));

    }


}
