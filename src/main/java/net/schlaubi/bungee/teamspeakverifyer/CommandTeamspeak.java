package net.schlaubi.bungee.teamspeakverifyer;

import com.google.common.collect.ImmutableSet;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.config.Configuration;
import net.schlaubi.util.MySQL;

import java.util.*;


public class CommandTeamspeak extends Command implements TabExecutor{

    public static HashMap<String, String> users = new HashMap<>();
    private Configuration cfg = Main.getConfiguration();

    public CommandTeamspeak(String name) {
        super(name);
    }
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
    public void execute(CommandSender sender, String[] args) {

        if(sender instanceof ProxiedPlayer){
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            if(args.length > 0){
                if(args[0].equalsIgnoreCase("verify")){
                    if(users.containsKey(pp.getName())){
                        pp.sendMessage(cfg.getString("Messages.running").replace("&", "§").replace("%code%", users.get(pp.getName())));
                    } else if (MySQL.userExists(pp)){
                        pp.sendMessage(cfg.getString("Messages.verified").replace("&", "§"));
                    } else {
                        users.put(pp.getName(), generateString());
                        pp.sendMessage(cfg.getString("Messages.verify").replace("&", "§").replace("%code%", users.get(pp.getName())));
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (users.containsKey(pp.getName())) {
                                    users.remove(pp.getName());
                                }
                            }
                        }, 60 * 1000);
                    }
                } else if(args[0].equalsIgnoreCase("unlink")){
                    if (!MySQL.userExists(pp)) {
                        pp.sendMessage(cfg.getString("Messages.notverified").replace("&", "§"));
                    } else {
                        Main.api.removeClientFromServerGroup(cfg.getInt("Roles.defaultrole"), Integer.parseInt(MySQL.getValue(pp, "identity")));
                        cfg.getSection("Roles").getKeys().forEach(i -> {
                            if(pp.hasPermission("group." + i)) {
                                Main.api.removeClientFromServerGroup(cfg.getInt("Roles.group." + i), Integer.parseInt(MySQL.getValue(pp, "identity")));
                            }
                        });
                        pp.sendMessage(cfg.getString("Messages.unlinked").replace("&", "§"));
                    }
                } else if (args[0].equalsIgnoreCase("reload")){
                    if(pp.hasPermission("ts.reload")){
                      Main.LoadConfig();
                      pp.sendMessage("§7[§aTeamSpeak§7]§a Settings reloaded");
                    }
                } else if(args[0].equalsIgnoreCase("update")){
                    if (!MySQL.userExists(pp)) {
                        pp.sendMessage(cfg.getString("Messages.notverified").replace("&", "§"));
                    } else {
                        System.out.println(MySQL.getValue(pp, "identity"));
                        cfg.getSection("Roles.group").getKeys().forEach(i -> {
                            if(pp.hasPermission("group." + i)) {
                                if(pp.hasPermission("group." + i)) {
                                    Main.api.addClientToServerGroup(cfg.getInt("Roles.group." + i), Integer.parseInt(MySQL.getValue(pp, "identity")));
                                }
                            }
                        });
                        pp.sendMessage(cfg.getString("Messages.updated").replace("&", "§"));
                    }
                }
            } else {
                pp.sendMessage(cfg.getString("Messages.help").replace("&", "§").replace("%nl", "/n"));

            }
        } else {
            ProxyServer.getInstance().getConsole().sendMessage("§4§lYou must be a player to run this command");
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        String[] subcommands = {"unlink", "reload", "update", "verify"};
        ProxiedPlayer pp = (ProxiedPlayer) sender;
        if(args.length > 1 || args.length == 0){
            return ImmutableSet.of();
        }
        Set<String> matches = new HashSet<>();
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
