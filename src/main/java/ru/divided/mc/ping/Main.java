package ru.divided.mc.ping;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.GameRule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class Main extends JavaPlugin {

    String supportedVersion = "1.16 - 1.19";
    String pluginVersion = "1.0";

    int serverVersion;

    @Override
    public void onEnable() {
        System.out.println("pingPlugin for version " + supportedVersion + " enabled!");

        String serverVersionStr   = getServer().getBukkitVersion();
        String serverVersionMajor = serverVersionStr.split("\\.")[0] + serverVersionStr.split("\\.")[1].split("-")[0];

        serverVersion = Integer.parseInt(serverVersionMajor.replaceAll("\\.", ""));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        switch (cmd.getName()) {
            case "pinginfo":
                pinginfoCommand(sender, args);
                break;
            case "ping":
                pingCommand(sender, args);
                break;
            default:
                break;
        }

        return false;
    }

    public void pinginfoCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) { //check if sender is a player
            Player p = (Player) sender; //convert to player
            if (!p.getWorld().getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK))
                return; //check gamerule and stop if send command feedback is false
        }
        sender.sendMessage("pingPlugin by divided__ v" + pluginVersion + " \n----\nType /ping to see your ping in ms to the server.\n----\nhttps://github.com/TheDivied/rPing");
    }

    public void pingCommand(CommandSender sender, String[] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (!p.getWorld().getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)) return; //check gamerule and stop if send command feedback is false

        }


        Player targetPlayer = getTargetPlayer(sender, args);

        if (targetPlayer == null) return;


        int ping = getPing(targetPlayer);

        if (ping < 0) return;


        if (targetPlayer.getName().equals(sender.getName())) {
            sender.sendMessage("§aYour ping: §e" + ping + "ms");
        } else {
            sender.sendMessage("§a" + targetPlayer.getName() + "'s ping: §e" + ping + "ms");
        }

    }

    private Player getTargetPlayer(CommandSender sender, String[] args) {
        if (args.length == 1) {

            if (Bukkit.getPlayer(args[0]) != null) {
                return Bukkit.getPlayer(args[0]);
            } else {
                sender.sendMessage("That player doesn't seem to be online!");
                return null;
            }

        } else if (sender instanceof Player) {

            return (Player) sender;

        } else {

            sender.sendMessage("Only Players can check their own ping!");
            return null;
        }
    }

    private int getPing(Player targetPlayer) {
        try {

            if (serverVersion >= 117) {

                return targetPlayer.getPing();

            } else {

                Class<?> CraftPlayerClass = Class.forName(getServer().getClass().getPackage().getName() + ".entity.CraftPlayer");
                Object CraftPlayer        = CraftPlayerClass.cast(targetPlayer);

                Method getHandle = CraftPlayer.getClass().getMethod("getHandle");
                Object EntityPlayer = getHandle.invoke(CraftPlayer);
                Field  ping         = EntityPlayer.getClass().getDeclaredField("ping");
                return ping.getInt(EntityPlayer);

            }

        } catch (Exception err) {
            targetPlayer.sendMessage("Error: " + err.getMessage());
            return -1;
        }
    }
}