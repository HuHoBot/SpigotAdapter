package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.HuHoBot;
import net.md_5.bungee.api.ChatColor;

public class SendConfig extends EventRunner {
    private HuHoBot plugin = HuHoBot.getPlugin();

    @Override
    public boolean run() {
        String hashKey = body.getString("hashKey");
        getConfigManager().setHashKey(hashKey);
        plugin.getLogger().info(ChatColor.GOLD + "配置文件已接受.");
        plugin.getLogger().info(ChatColor.GOLD + "自动断开连接以刷新配置文件...");
        HuHoBot.getClientManager().shutdownClient();
        return true;
    }
}
