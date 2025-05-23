package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.HuHoBot;
import cn.huohuas001.huHoBot.Tools.ConfigManager;
import cn.huohuas001.huHoBot.WsClient;
import com.alibaba.fastjson2.JSONObject;
import org.bukkit.configuration.file.FileConfiguration;

public class EventRunner {
    String packId;
    JSONObject body;

    void respone(String msg, String type) {
        WsClient client = HuHoBot.getPlugin().getClientManager().getClient();
        client.respone(msg, type, packId);
    }

    void sendMessage(String type, JSONObject body) {
        WsClient client = HuHoBot.getPlugin().getClientManager().getClient();
        client.sendMessage(type, body, packId);
    }

    FileConfiguration getConfig() {
        return HuHoBot.getPlugin().getConfig();
    }

    ConfigManager getConfigManager() {
        return HuHoBot.configManager;
    }

    void runCommand(String command) {
        HuHoBot.getPlugin().runCommand(command, packId);
    }

    /*void runCommandAsync(String command) {
        HuHoBot.getScheduler().runTask(() -> {
            HuHoBot.getScheduler().runTaskLaterAsynchronously(() -> {
                String sendCqMsg = ServerManager.sendCmd(command, true, true);
                respone("已执行.\n" + sendCqMsg, "success");
            }, 20L);
        });
    }*/

    public boolean EventCall(String packId, JSONObject body) {
        this.packId = packId;
        this.body = body;
        return run();
    }

    boolean run() {
        return true;
    }

    ;
}
