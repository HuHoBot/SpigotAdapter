package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.HuHoBot;
import org.bukkit.configuration.file.FileConfiguration;

public class AddAllowList extends EventRunner {
    @Override
    public boolean run() {
        String XboxId = body.getString("xboxid");
        FileConfiguration config = this.getConfig();
        String command = config.getString("whiteList.add").replace("{name}", XboxId);
        runCommand(command);
        String name = HuHoBot.getPlugin().getServerName();
        respone(name + "已接受添加名为" + XboxId + "的白名单请求", "success");
        return true;
    }
}
