package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.HuHoBot;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class SendConfig extends EventRunner {
    private HuHoBot plugin = HuHoBot.getPlugin();

    @Override
    public boolean run() {
        FileConfiguration config = this.getConfig();
        config.set("serverId", body.getString("serverId"));
        config.set("hashKey", body.getString("hashKey"));
        plugin.saveConfig();
        plugin.getLogger().info(ChatColor.GOLD + "配置文件已接受.");
        plugin.reloadBotConfig();
        plugin.getLogger().info(ChatColor.GOLD + "自动断开连接以刷新配置文件...");
        HuHoBot.getClientManager().shutdownClient();
        return true;
    }
}
