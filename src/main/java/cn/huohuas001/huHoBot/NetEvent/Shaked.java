package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.HuHoBot;
import cn.huohuas001.huHoBot.TimeTask.SendHeart;
import net.md_5.bungee.api.ChatColor;

import java.util.logging.Logger;

public class Shaked extends EventRunner {
    private HuHoBot plugin = HuHoBot.getPlugin();
    private Logger logger = plugin.getLogger();

    private void shakedProcess() {
        plugin.getClientManager().setShouldReconnect(true);
        HuHoBot.getClientManager().cancelCurrentTask();
        HuHoBot.getClientManager().setAutoDisConnectTask();
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new SendHeart(), 0, 5 * 20);
    }

    @Override
    public boolean run() {
        int code = body.getInteger("code");
        String msg = body.getString("msg");
        switch (code) {
            case 1:
                logger.info("与服务端握手成功.");
                shakedProcess();
                break;
            case 2:
                logger.info("握手完成!,附加消息:" + msg);
                shakedProcess();
                break;
            case 3:
                logger.severe(ChatColor.DARK_RED + "握手失败，客户端密钥错误.");
                plugin.getClientManager().setShouldReconnect(false);
                break;
            case 6:
                logger.info("与服务端握手成功，服务端等待绑定...");
                shakedProcess();
                break;
            default:
                logger.severe(ChatColor.DARK_RED + "握手失败，原因" + msg);
        }
        return true;
    }
}
