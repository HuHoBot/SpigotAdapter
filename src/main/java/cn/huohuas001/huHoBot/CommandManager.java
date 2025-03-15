package cn.huohuas001.huHoBot;

import cn.huohuas001.huHoBot.NetEvent.bindRequest;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandManager implements CommandExecutor {
    private final HuHoBot plugin = HuHoBot.getPlugin();

    private void onReload(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.isOp()) {
                sender.sendMessage(ChatColor.DARK_RED + "你没有足够的权限.");
                return;
            }
        }

        if (HuHoBot.getPlugin().reloadBotConfig()) {
            sender.sendMessage(ChatColor.AQUA + "重载机器人配置文件成功.");
        }
    }

    private void onReconnect(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.isOp()) {
                sender.sendMessage(ChatColor.DARK_RED + "你没有足够的权限.");
                return;
            }
        }

        if (HuHoBot.getPlugin().reconnect()) {
            sender.sendMessage(ChatColor.GOLD + "重连机器人成功.");
        } else {
            sender.sendMessage(ChatColor.DARK_RED + "重连机器人失败：已在连接状态.");
        }

    }

    private void onDisconnect(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.isOp()) {
                sender.sendMessage(ChatColor.DARK_RED + "你没有足够的权限.");
                return;
            }
        }

        if (HuHoBot.getPlugin().disConnectServer()) {
            sender.sendMessage(ChatColor.GOLD + "已断开机器人连接.");
        }
    }

    private void onBind(CommandSender sender, String[] args) {
        bindRequest obj = plugin.bindRequestObj;
        if (obj.confirmBind(args[1])) {
            sender.sendMessage(ChatColor.GOLD + "已向服务器发送确认绑定请求，请等待服务端下发配置文件.");
        } else {
            sender.sendMessage(ChatColor.DARK_RED + "绑定码错误，请重新输入.");
        }
    }

    private void onHelp(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.AQUA + "HuHoBot 操作相关命令");
        sender.sendMessage(ChatColor.GOLD + ">" + ChatColor.DARK_GRAY + "/huhobot reload - 重载配置文件");
        sender.sendMessage(ChatColor.GOLD + ">" + ChatColor.DARK_GRAY + "/huhobot reconnect - 重新连接服务器");
        sender.sendMessage(ChatColor.GOLD + ">" + ChatColor.DARK_GRAY + "/huhobot disconnect - 断开服务器连接");
        sender.sendMessage(ChatColor.GOLD + ">" + ChatColor.DARK_GRAY + "/huhobot bind <bindCode:string> - 确认绑定");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String keyWord = "";
        if (args.length > 0) {
            keyWord = args[0];
        }

        switch (keyWord) {
            case "reload":
                onReload(sender, args);
                break;
            case "reconnect":
                onReconnect(sender, args);
                break;
            case "disconnect":
                onDisconnect(sender, args);
                break;
            case "bind":
                onBind(sender, args);
                break;
            case "help":
                onHelp(sender, args);
                break;
            default:
                sender.sendMessage(ChatColor.DARK_RED + "使用/huhobot help来获取更多详情");
        }
        return true;
    }
}
