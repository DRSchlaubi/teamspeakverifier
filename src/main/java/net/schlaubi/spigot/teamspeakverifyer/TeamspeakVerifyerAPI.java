package net.schlaubi.spigot.teamspeakverifyer;

import net.schlaubi.util.MySQL;
import org.bukkit.entity.Player;

public class TeamspeakVerifyerAPI {


    public static boolean isVerified(Player player){
        if(MySQL.userExists(player)){
            return true;
        }
        return false;
    }
    public static boolean isVerified(String identity){
        if(MySQL.userExists(identity)){
            return true;
        }
        return false;
    }

    public static String getUserName(String id){
        return MySQL.getValue(id, "uuid");
    }

    public static String getDatabaseId(Player player){
        return MySQL.getValue(player, "uuid");
    }
}
