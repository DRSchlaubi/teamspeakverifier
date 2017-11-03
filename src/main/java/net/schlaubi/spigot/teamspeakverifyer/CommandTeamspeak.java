package net.schlaubi.spigot.teamspeakverifyer;


import net.milkbowl.vault.permission.Permission;
import net.schlaubi.spigot.teamspeakverifyer.listener.TeamSpeakMessageListener;
import net.schlaubi.util.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CommandTeamspeak implements CommandExecutor, TabExecutor {

    private static HashMap<String, String> users = TeamSpeakMessageListener.users;

    private String generateString(){
        String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567980";
        StringBuilder random = new StringBuilder();
        Random rnd = new Random();
        while(random.length() < 5){
            int index = (int) (rnd.nextFloat() * CHARS.length());
            random.append(CHARS.charAt(index));
        }
        return random.toString();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command name, String lable, String[] args) {
        FileConfiguration cfg = Main.getConfiguration();
        if(sender instanceof Player){
            Player player = (Player) sender;

            String playerinfo = player.getName();
            if(args.length > 0){
                if(args[0].equalsIgnoreCase("reload")){
                    if(player.hasPermission("ts.reload")){
                        player.sendMessage("§7[§aTeamSpeak§7]§a Settings reloaded");
                        try {
                            cfg.save(new File("plugins/TeamspeakVerifyer", "config.yml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (args[0].equalsIgnoreCase("verify")){
                    if(users.containsKey(playerinfo)){
                        player.sendMessage(cfg.getString("Messages.running").replace("&", "§").replace("%code%", users.get(playerinfo)));
                    } else if(MySQL.userExists(player)) {
                        player.sendMessage(cfg.getString("Messages.verified").replace("&", "§"));
                    } else {
                        users.put(playerinfo, generateString());
                        player.sendMessage(cfg.getString("Messages.verify").replace("&", "§").replace("%code%", users.get(playerinfo)));
                        Bukkit.getScheduler().runTaskLater(Main.instance, () -> {
                            if(users.containsKey(player.getName())){
                                users.remove(player.getName());
                            }
                        }, 60*1000);
                    }
                } else if (args[0].equalsIgnoreCase("unlink")) {
                    if (!MySQL.userExists(player)) {
                        player.sendMessage(cfg.getString("Messages.notverified").replace("&", "§"));
                    } else {
                        Main.api.removeClientFromServerGroup(cfg.getInt("Roles.defaultrole"), Integer.parseInt(MySQL.getValue(player, "identity")));
                        Permission perms = Main.getPermissions();
                        String group = perms.getPrimaryGroup(player);
                        Main.api.removeClientFromServerGroup(cfg.getInt("Roles.group." + group), Integer.parseInt(MySQL.getValue(player, "identity")));
                        MySQL.deleteUser(player);
                        player.sendMessage(cfg.getString("Messages.unlinked").replace("&", "§"));

                    }
                } else if (args[0].equalsIgnoreCase("update")) {
                    if (!MySQL.userExists(player)) {
                        player.sendMessage(cfg.getString("Messages.notverified").replace("&", "§"));
                    } else {
                        Permission perms = Main.getPermissions();
                        String group = perms.getPrimaryGroup(player);
                        Main.api.addClientToServerGroup(cfg.getInt("Roles.group." + group), Integer.parseInt(MySQL.getValue(player, "identity")));
                        player.sendMessage(cfg.getString("Messages.updated").replace("&", "§"));
                    }
                }
            } else {
                player.sendMessage(cfg.getString("Messages.help").replace("&", "§").replace("%nl%", "/n"));
            }
        } else {
            Bukkit.getConsoleSender().sendMessage("§4§lYou must be a player to user this Command");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command name, String lable, String[] args) {
        String[] subcommands = {"reload", "verify", "unlink", "update"};
        if(args.length > 1 || args.length == 0){
            return Arrays.asList(subcommands);
        }
        List<String> matches = new ArrayList<>();
        if(args.length > 0){
            for(String subcommand : subcommands){
                if(subcommand.startsWith(args[0])){
                    matches.add(subcommand);
                }
            }
            return matches;
        }
        return null;
    }
}
