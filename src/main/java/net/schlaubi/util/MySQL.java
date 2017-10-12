package net.schlaubi.util;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.schlaubi.spigot.teamspeakverifyer.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.*;

public class MySQL {


    private static Connection connection;

    public static void connect()
    {
        FileConfiguration cfg = Main.getConfiguration();
        String host = cfg.getString("MySQL.host");
        Integer port = cfg.getInt("MySQL.port");
        String user = cfg.getString("MySQL.user");
        String database = cfg.getString("MySQL.database");
        String password = cfg.getString("MySQL.password");
        try
        {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&autoReconnectForPools=true&interactiveClient=true&characterEncoding=UTF-8", user, password);
            Bukkit.getConsoleSender().sendMessage("§a§l[TeamSpeakVerifyer]MySQL connection success");
        }
        catch (SQLException e)
        {
            Bukkit.getConsoleSender().sendMessage("§4§l[TeamSpeakVerifyer]MySQL connection failed");
            e.printStackTrace();
        }
    }

    public static void bungeconnect(){
        Configuration cfg = net.schlaubi.bungee.teamspeakverifyer.Main.getConfiguration();
        String host = cfg.getString("MySQL.host");
        Integer port = cfg.getInt("MySQL.port");
        String user = cfg.getString("MySQL.user");
        String database = cfg.getString("MySQL.database");
        String password = cfg.getString("MySQL.password");

        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&autoReconnectForPools=true&interactiveClient=true&characterEncoding=UTF-8", user, password);
            ProxyServer.getInstance().getConsole().sendMessage("§a§l[TeamSpeakVerifyer] MySQL connection success");
        } catch (SQLException e){
            ProxyServer.getInstance().getConsole().sendMessage("§4§l[TeamSpeakVerifyer] MySQL connection failed");
            e.printStackTrace();
        }
    }

    private static boolean isConnected()
    {
        return connection != null;
    }

    public static void disconnect()
    {
        try
        {
            if (!isConnected()) {
                connection.close();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static boolean userExists(Player player)
    {
        try
        {
            if (connection.isClosed()) {
                connect();
            }
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM teamspeakverifyer WHERE uuid =?");
            ps.setString(1, player.getName());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean userExists(ProxiedPlayer player)
    {
        try
        {
            if (connection.isClosed()) {
                connect();
            }
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM teamspeakverifyer WHERE uuid =?");
            ps.setString(1, player.getName());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean userExists(String id)
    {
        try
        {
            if (connection.isClosed()) {
                connect();
            }
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM teamspeakverifyer WHERE identity =?");
            if (!Bukkit.getServer().getOnlineMode()) {
                ps.setString(1, id);
            }
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }


    public static void createDatabase()
    {
        try
        {
            if (connection.isClosed()) {
                connect();
            }
            PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS teamspeakverifyer( `id` INT NOT NULL AUTO_INCREMENT , `uuid` TEXT NOT NULL , `identity` TEXT NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;");
            ps.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static void createUser(Player player, String identity)
    {
        try
        {
            if (connection.isClosed()) {
                connect();
            }
            PreparedStatement ps = connection.prepareStatement("INSERT INTO teamspeakverifyer(`uuid`,`identity`) VALUES (?, ?)");
            ps.setString(1, player.getName());
            ps.setString(2, identity);
            ps.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    public static void createUser(ProxiedPlayer player, String identity)
    {
        try
        {
            if (connection.isClosed()) {
                connect();
            }
            PreparedStatement ps = connection.prepareStatement("INSERT INTO teamspeakverifyer(`uuid`,`identity`) VALUES (?, ?)");
            ps.setString(1, player.getName());
            ps.setString(2, identity);
            ps.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static String getValue(Player player, String type)
    {
        try
        {
            if (connection.isClosed()) {
                connect();
            }
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM teamspeakverifyer WHERE uuid = ?");
            ps.setString(1, player.getName());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(type);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public static String getValue(ProxiedPlayer player, String type)
    {
        try
        {
            if (connection.isClosed()) {
                connect();
            }
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM teamspeakverifyer WHERE uuid = ?");
            ps.setString(1, player.getName());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(type);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public static String getValue(String identity, String type)
    {
        try
        {
            if (connection.isClosed()) {
                connect();
            }
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM teamspeakverifyer WHERE identity = ?");
            ps.setString(1, identity);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(type);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteUser(Player player)
    {
        try
        {
            if (connection.isClosed()) {
                connect();
            }
            PreparedStatement ps = connection.prepareStatement("DELETE FROM teamspeakverifyer WHERE uuid=?");
            ps.setString(1, player.getName());
            ps.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static void deleteUser(ProxiedPlayer player)
    {
        try
        {
            if (connection.isClosed()) {
                connect();
            }
            PreparedStatement ps = connection.prepareStatement("DELETE FROM teamspeakverifyer WHERE uuid=?");
            ps.setString(1, player.getName());
            ps.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
