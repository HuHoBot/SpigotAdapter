package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.HuHoBot;
import net.md_5.bungee.api.ChatColor;

import java.util.logging.Logger;

public class ShutDown extends EventRunner {
    private HuHoBot plugin = HuHoBot.getPlugin();
    private Logger logger = plugin.getLogger();

    @Override
    public boolean run() {
        logger.severe(ChatColor.DARK_RED + "服务端命令断开连接 原因:" + body.getString("msg"));
        logger.severe(ChatColor.DARK_RED + "此错误具有不可容错性!请检查插件配置文件!");
        logger.warning(ChatColor.GOLD + "正在断开连接...");
        HuHoBot.getClientManager().setShouldReconnect(false);
        HuHoBot.getClientManager().shutdownClient();
        return true;
    }
}