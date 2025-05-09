package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.Api.BotCustomCommand;
import cn.huohuas001.huHoBot.HuHoBot;
import cn.huohuas001.huHoBot.Tools.CommandObject;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Map;

public class CustomRun extends EventRunner {
    private final HuHoBot plugin = HuHoBot.getPlugin();

    private void CallEvent() {
        String keyWord = body.getString("key");
        List<String> param = body.getList("runParams", String.class);

        Map<String, CommandObject> commandMap = HuHoBot.getPlugin().getCommandMap();
        // 测试查找功能
        CommandObject result = commandMap.get(keyWord);
        BotCustomCommand event = new BotCustomCommand(keyWord, body, packId, false);
        if (result == null) {
            Bukkit.getPluginManager().callEvent(event);
            return;
        } else {
            String command = result.getCommand();
            for (int i = 0; i < param.size(); i++) {
                int replaceNum = i + 1;
                command = command.replace("&" + replaceNum, param.get(i));
            }
            if (result.getPermission() > 0) {
                respone("权限不足，若您是管理员，请使用/管理员执行", "error");
                return;
            }
            runCommand(command);
        }

        //执行后判定是否有命令接收
        if (!event.isCancelled() && !keyWord.startsWith("#")) {
            respone("未找到关键词" + keyWord + "对应的自定义事件", "error");
        }
    }

    @Override
    public boolean run() {
        HuHoBot.getScheduler().runTask(this::CallEvent);

        return true;
    }
}
