package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.HuHoBot;
import com.alibaba.fastjson2.JSONObject;
import org.bukkit.entity.Player;

import java.util.Collection;

public class QueryOnline extends EventRunner {
    @Override
    public boolean run() {
        Collection<? extends Player> onlineList = HuHoBot.getPlugin().getServer().getOnlinePlayers();
        StringBuilder onlineNameString = new StringBuilder(); // 使用StringBuilder来累积玩家名称
        for (Player pl : onlineList) {
            String playerName = pl.getName();
            onlineNameString.append(playerName).append("\n");
        }
        onlineNameString.append("共").append(onlineList.size()).append("人在线");
        JSONObject list = new JSONObject();
        list.put("msg", onlineNameString);
        list.put("url", getConfig().getString("motdUrl"));
        list.put("serverType", "java");
        JSONObject rBody = new JSONObject();
        rBody.put("list", list);
        sendMessage("queryOnline", rBody);
        return true;
    }
}
