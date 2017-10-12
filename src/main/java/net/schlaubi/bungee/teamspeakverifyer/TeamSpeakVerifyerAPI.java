package net.schlaubi.bungee.teamspeakverifyer;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.schlaubi.util.MySQL;

public class TeamSpeakVerifyerAPI {

    public static boolean isVerified(ProxiedPlayer player){
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

    public static String getDatabaseId(ProxiedPlayer player){
        return MySQL.getValue(player, "uuid");
    }
}
