package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.HuHoBot;
import org.bukkit.configuration.file.FileConfiguration;

public class Chat extends EventRunner {
    @Override
    public boolean run() {
        String nick = body.getString("nick");
        String msg = body.getString("msg");
        FileConfiguration config = this.getConfig();
        String message = config.getString("chatFormatGroup").replace("{nick}", nick).replace("{msg}", msg);
        HuHoBot.getPlugin().getServer().broadcastMessage(message);
        return true;
    }
}
