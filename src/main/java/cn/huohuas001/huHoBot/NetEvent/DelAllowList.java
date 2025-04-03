package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.HuHoBot;
import org.bukkit.configuration.file.FileConfiguration;

public class DelAllowList extends EventRunner {
    @Override
    public boolean run() {
        String XboxId = body.getString("xboxid");
        FileConfiguration config = this.getConfig();
        String command = config.getString("whiteList.del").replace("{name}", XboxId);
        runCommand(command);
        String name = HuHoBot.getPlugin().getServerName();
        respone(name + "已接受删除名为" + XboxId + "的白名单请求", "success");
        return true;
    }
}
