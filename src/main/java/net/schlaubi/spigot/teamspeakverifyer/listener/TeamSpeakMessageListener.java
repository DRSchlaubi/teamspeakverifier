package net.schlaubi.spigot.teamspeakverifyer.listener;

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import net.milkbowl.vault.permission.Permission;
import net.schlaubi.spigot.teamspeakverifyer.Main;
import net.schlaubi.util.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class TeamSpeakMessageListener extends TS3EventAdapter{

    public static HashMap<String, String> users = new HashMap<>();

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
        ClientInfo info = Main.api.getClientInfo(e.getInvokerId());
        Permission perms = Main.getPermissions();
        FileConfiguration cfg = Main.getConfiguration();
        if (e.getTargetMode().equals(TextMessageTargetMode.CLIENT)){
            String message = e.getMessage();
            String[] args = message.split(" ");
            if(message.startsWith("!verify")){
                if(users.containsValue(args[1])){
                    Player player = Bukkit.getPlayer(getUser(args[1]));
                    String group = perms.getPrimaryGroup(player);
                    Main.api.addClientToServerGroup(cfg.getInt("Roles.defaultrole"), info.getDatabaseId());
                    Main.api.addClientToServerGroup(cfg.getInt("Roles.group." + group), info.getDatabaseId());
                    Main.api.pokeClient(e.getInvokerId(), cfg.getString("Messages.success").replace("%user%", e.getInvokerName()).replace("%minecraft%", player.getName()));
                    MySQL.createUser(player, String.valueOf(info.getDatabaseId()));
                    users.remove(player.getName());
                } else {
                    Main.api.sendPrivateMessage(e.getInvokerId(), cfg.getString("Messages.invalidcode"));
                }
            } else if (message.contains("!info")){
                Main.api.sendPrivateMessage(e.getInvokerId(), "TeamspeakVerifyer made by Schlaubi");
            }
        }
    }
}
