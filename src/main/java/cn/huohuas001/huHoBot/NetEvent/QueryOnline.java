package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.HuHoBot;
import com.alibaba.fastjson2.JSONObject;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.Collection;

public class QueryOnline extends EventRunner {
    public String setPlaceholder(String oriText) {
        String text = PlaceholderAPI.setPlaceholders(null, oriText);
        return text;
    }
    @Override
    public boolean run() {
        //获取motd Config
        String server_ip = getConfig().getString("motd.server_ip");
        int server_port = getConfig().getInt("motd.server_port");
        String api = getConfig().getString("motd.api");
        String text = getConfig().getString("motd.text");
        boolean output_online_list = getConfig().getBoolean("motd.output_online_list");

        StringBuilder onlineNameString = new StringBuilder();
        int onlineSize = -1;
        if (output_online_list) {
            onlineNameString.append("\n在线玩家列表：\n");
            Collection<? extends Player> onlineList = HuHoBot.getPlugin().getServer().getOnlinePlayers();
            for (Player pl : onlineList) {
                String playerName = pl.getName();
                onlineNameString.append(playerName).append("\n");
            }
            onlineSize = onlineList.size();
        }

        onlineNameString.append(setPlaceholder(text.replace("{online}", String.valueOf(onlineSize))));


        // 构造JSON对象
        JSONObject list = new JSONObject();
        list.put("msg", onlineNameString);
        list.put("url", server_ip + ":" + String.valueOf(server_port));
        list.put("imgUrl", api.replace("{server_ip}", server_ip).replace("{server_port}", String.valueOf(server_port)));
        list.put("post_img", getConfig().getBoolean("motd.post_img"));
        list.put("serverType", "java");
        JSONObject rBody = new JSONObject();
        rBody.put("list", list);

        //返回消息
        sendMessage("queryOnline", rBody);
        return true;
    }
}
