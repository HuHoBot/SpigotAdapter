package cn.huohuas001.huHoBot.Tools;

import cn.huohuas001.huHoBot.HuHoBot;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义命令
 */
public class CustomCommand {
    private final HuHoBot plugin;
    private Map<String, CommandObject> commandMap;

    public CustomCommand(HuHoBot plugin) {
        this.plugin = plugin;
    }

    public void loadCommandsFromConfig() {
        // 从配置文件中读取 commands 列表
        FileConfiguration config = plugin.getConfig();
        List<Map<?, ?>> commands = config.getMapList("customCommand");

        // 初始化 commandMap
        commandMap = new HashMap<>();
        for (Map<?, ?> commandData : commands) {
            String key = (String) commandData.get("key");
            String command = (String) commandData.get("command");
            int permission = ((Integer) commandData.get("permission"));

            // 创建 CommandObject 并放入 HashMap
            if (key != null) {
                commandMap.put(key, new CommandObject(key, command, permission));
            }
        }
    }

    public Map<String, CommandObject> getCommandMap() {
        return commandMap;
    }
}
