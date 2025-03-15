package cn.huohuas001.huHoBot.TimeTask;

import cn.huohuas001.huHoBot.HuHoBot;

/**
 * 发送心跳包计划事件
 */
public class SendHeart implements Runnable {
    @Override
    public void run() {
        HuHoBot plugin = HuHoBot.getPlugin();
        plugin.getClientManager().sendHeart();
    }
}
