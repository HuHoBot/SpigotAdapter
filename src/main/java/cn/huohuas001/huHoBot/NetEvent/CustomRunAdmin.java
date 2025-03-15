package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.Api.BotCustomCommand;
import cn.huohuas001.huHoBot.HuHoBot;
import cn.huohuas001.huHoBot.Tools.CommandObject;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Map;

public class CustomRunAdmin extends EventRunner {
    private final HuHoBot plugin = HuHoBot.getPlugin();

    private void CallEvent() {
        String keyWord = body.getString("key");
        List<String> param = body.getList("runParams", String.class);

        Map<String, CommandObject> commandMap = HuHoBot.getPlugin().getCommandMap();
        // 测试查找功能
        CommandObject result = commandMap.get(keyWord);
        BotCustomCommand event = new BotCustomCommand(keyWord, body, packId, true);
        if (result == null) {
            Bukkit.getPluginManager().callEvent(event);
            return;
        } else {
            String command = result.getCommand();
            for (int i = 0; i < param.size(); i++) {
                int replaceNum = i + 1;
                command = command.replace("&" + String.valueOf(replaceNum), param.get(i));
            }
            runCommand(command);
        }

        //执行后判定是否有命令接收
        if (!event.isCancelled()) {
            respone("未找到关键词" + keyWord + "对应的自定义事件", "error");
        }

    }

    @Override
    public boolean run() {
        Bukkit.getScheduler().runTask(plugin, this::CallEvent);

        return false;
    }
}
