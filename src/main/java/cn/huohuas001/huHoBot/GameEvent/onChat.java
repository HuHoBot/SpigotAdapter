package cn.huohuas001.huHoBot.GameEvent;

import cn.huohuas001.huHoBot.HuHoBot;
import com.alibaba.fastjson2.JSONObject;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class onChat implements Listener {
    public onChat() {
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        String playerName = event.getPlayer().getName();
        HuHoBot plugin = HuHoBot.getPlugin();
        FileConfiguration config = plugin.getConfig();
        String format = config.getString("chatFormat.from_game");
        String prefix = config.getString("chatFormat.post_prefix");
        boolean isPostChat = config.getBoolean("chatFormat.post_chat");
        String serverId = config.getString("serverId");
        if (message.startsWith(prefix) && isPostChat) {
            JSONObject body = new JSONObject();
            body.put("serverId", serverId);
            String formated = format.replace("{name}", playerName).replace("{msg}", message.substring(prefix.length()));
            body.put("msg", formated);
            HuHoBot.getClientManager().getClient().sendMessage("chat", body);
        }

    }
}
