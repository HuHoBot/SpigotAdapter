package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.HuHoBot;
import cn.huohuas001.huHoBot.Tools.SetController;
import com.alibaba.fastjson2.JSONObject;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Set;

public class QueryAllowList extends EventRunner {
    @Override
    public boolean run() {
        Set<OfflinePlayer> Or_whitelist = HuHoBot.getPlugin().getServer().getWhitelistedPlayers();
        Set<String> whiteList = SetController.convertToPlayerNames(Or_whitelist);
        StringBuilder whitelistNameString = new StringBuilder();
        JSONObject rBody = new JSONObject();
        if (body.containsKey("key")) {
            String key = body.getString("key");
            if (key.length() < 2) {
                whitelistNameString.append("查询白名单关键词:").append(key).append("结果如下:\n");
                whitelistNameString.append("请使用两个字母及以上的关键词进行查询!");
                rBody.put("list", whitelistNameString);
                sendMessage("queryWl", rBody);
                return true;
            }
            whitelistNameString.append("查询白名单关键词:").append(key).append("结果如下:\n");
            List<String> filterList = SetController.searchInSet(whiteList, key);
            if (filterList.isEmpty()) {
                whitelistNameString.append("无结果\n");
            } else {
                for (String plName : filterList) {
                    whitelistNameString.append(plName).append("\n");
                }
                whitelistNameString.append("共有").append(filterList.size()).append("个结果");
            }
            rBody.put("list", whitelistNameString);
            sendMessage("queryWl", rBody);
        } else if (body.containsKey("page")) {
            int page = body.getInteger("page");
            whitelistNameString.append("服内白名单如下:\n");
            List<List<String>> splitedNameList = SetController.chunkSet(whiteList, 10);
            List<String> currentNameList = splitedNameList.get(page - 1);
            if (page - 1 > splitedNameList.size()) {
                whitelistNameString.append("没有该页码\n");
                whitelistNameString.append("共有").append(splitedNameList.size()).append("页\n请使用/查白名单 {页码}来翻页");
            } else {
                for (String plName : currentNameList) {
                    whitelistNameString.append(plName).append("\n");
                }
                whitelistNameString.append("共有").append(splitedNameList.size()).append("页，当前为第").append(page).append("页\n请使用/查白名单 {页码}来翻页");
            }
            rBody.put("list", whitelistNameString);
            sendMessage("queryWl", rBody);
        } else {
            whitelistNameString.append("服内白名单如下:\n");
            List<List<String>> splitedNameList = SetController.chunkSet(whiteList, 10);
            if (splitedNameList.isEmpty()) {
                whitelistNameString.append("无结果\n");
            } else {
                List<String> currentNameList = splitedNameList.get(0);
                for (String plName : currentNameList) {
                    whitelistNameString.append(plName).append("\n");
                }
            }
            whitelistNameString.append("共有").append(splitedNameList.size()).append("页，当前为第1页\n请使用/查白名单 {页码}来翻页");
            rBody.put("list", whitelistNameString);
            sendMessage("queryWl", rBody);
        }
        return true;
    }
}
