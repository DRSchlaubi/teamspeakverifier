package net.schlaubi.bungee.teamspeakverifyer.listener;


import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.schlaubi.bungee.teamspeakverifyer.CommandTeamspeak;
import net.schlaubi.bungee.teamspeakverifyer.Main;
import net.schlaubi.util.MySQL;

import java.util.HashMap;

public class TeamSpeakMessageListener extends TS3EventAdapter {

    private static HashMap<String, String> users = CommandTeamspeak.users;
    private static String getUser(String code) {
        for(String key : users.keySet()) {
            String value = users.get(key);
            if(value.equalsIgnoreCase(code)) {
                return key;
            }
        }
        return null;
    }

    @Override
    public void onTextMessage(TextMessageEvent e) {
        Configuration cfg = Main.getConfiguration();
        ClientInfo info = Main.api.getClientInfo(e.getInvokerId());
        String[] args = e.getMessage().split(" ");
        if(e.getTargetMode().equals(TextMessageTargetMode.CLIENT)){
            if(e.getMessage().startsWith("!verify")) {
                if(users.containsValue(args[1])) {
                    ProxiedPlayer pp = ProxyServer.getInstance().getPlayer(getUser(args[1]));
                    Main.api.addClientToServerGroup(cfg.getInt("Roles.defaultrole"), info.getDatabaseId());
                    cfg.getStringList("BCRoles").forEach(i -> {
                        if (pp.hasPermission("group." + i)) {
                            Main.api.addClientToServerGroup(cfg.getInt("Roles.group." + i), info.getDatabaseId());
                        }
                    });
                    Main.api.pokeClient(e.getInvokerId(), cfg.getString("Messages.success").replace("%user%", e.getInvokerName()).replace("%minecraft%", pp.getName()));
                    MySQL.createUser(pp, String.valueOf(info.getDatabaseId()));
                    users.remove(pp.getName());
                } else {
                    Main.api.sendPrivateMessage(e.getInvokerId(), cfg.getString("Messages.invalidcode"));
                }

            } else {
                Main.api.sendPrivateMessage(e.getInvokerId(), "TeamspeakVerifyer made by Schlaubi");
            }
        }
    }
}
