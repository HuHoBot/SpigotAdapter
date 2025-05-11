package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.HuHoBot;
import org.bukkit.configuration.file.FileConfiguration;

public class Chat extends EventRunner {
    @Override
    public boolean run() {
        String nick = body.getString("nick");
        String msg = body.getString("msg");
        FileConfiguration config = this.getConfig();
        boolean isPostChat = config.getBoolean("chatFormat.post_chat");
        String message = config.getString("chatFormat.from_group").replace("{nick}", nick).replace("{msg}", msg);
        if (isPostChat) {
            HuHoBot.getPlugin().getServer().broadcastMessage(message);
        }
        return true;
    }
}
