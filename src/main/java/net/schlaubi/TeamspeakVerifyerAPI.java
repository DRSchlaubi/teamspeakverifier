package net.schlaubi;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.schlaubi.util.MySQL;
import org.bukkit.entity.Player;

public class TeamspeakVerifyerAPI {


    public static boolean isVerified(Player player){
        return MySQL.userExists(player);
    }
    public static boolean isVerified(String identity){
        return MySQL.userExists(identity);
    }

    public static String getUserName(String id){
        return MySQL.getValue(id, "uuid");
    }

    public static String getDatabaseId(Player player){
        return MySQL.getValue(player, "identity");
    }

    public static String getDatabaseId(ProxiedPlayer player){
        return MySQL.getValue(player, "identity");
    }

    public static boolean isVerified(ProxiedPlayer player){
        return MySQL.userExists(player);
    }
}
