package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.Api.BotQueryWhiteList;
import cn.huohuas001.huHoBot.HuHoBot;
import cn.huohuas001.huHoBot.Tools.SetController;
import com.alibaba.fastjson2.JSONObject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Set;

public class QueryAllowList extends EventRunner {
    /*@Override
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
    }*/

    public boolean CallEvent() {
        Set<OfflinePlayer> orWhitelist = HuHoBot.getPlugin().getServer().getWhitelistedPlayers();
        Set<String> whiteList = SetController.convertToPlayerNames(orWhitelist);

        // 根据参数类型创建事件
        BotQueryWhiteList event = createEvent();
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false; // 事件被取消时中断处理
        }

        // 保留原有处理流程
        JSONObject rBody = new JSONObject();
        StringBuilder content = new StringBuilder();

        if (body.containsKey("key")) {
            handleKeyword(event, whiteList, content);
        } else if (body.containsKey("page")) {
            handlePage(event, whiteList, content);
        } else {
            handleDefault(event, whiteList, content);
        }

        rBody.put("list", content.toString());
        sendMessage("queryWl", rBody);
        return true;
    }

    private BotQueryWhiteList createEvent() {
        if (body.containsKey("key")) {
            String key = body.getString("key");
            return BotQueryWhiteList.createKeywordEvent(key, packId);
        } else if (body.containsKey("page")) {
            int page = body.getInteger("page");
            return BotQueryWhiteList.createPageEvent(page, packId);
        }
        return BotQueryWhiteList.createPageEvent(1, packId); // 默认第一页
    }

    private void handleKeyword(BotQueryWhiteList event, Set<String> whitelist, StringBuilder sb) {
        String key = event.getKeyWord();
        if (key.length() < 2) {
            sb.append("请使用两个字母及以上的关键词进行查询!");
            return;
        }

        List<String> results = SetController.searchInSet(whitelist, key);
        event.responseList(results, 0); // 通过事件发送响应
    }

    private void handlePage(BotQueryWhiteList event, Set<String> whitelist, StringBuilder sb) {
        int page = event.getPages();
        List<List<String>> pages = SetController.chunkSet(whitelist, 10);

        if (page - 1 >= pages.size()) {
            sb.append("没有该页码\n");
        }

        List<String> currentPage = pages.get(page - 1);
        event.responseList(currentPage, pages.size()); // 通过事件发送响应
    }

    private void handleDefault(BotQueryWhiteList event, Set<String> whitelist, StringBuilder sb) {
        List<List<String>> pages = SetController.chunkSet(whitelist, 10);
        event.responseList(pages.get(0), pages.size()); // 默认发送第一页
    }

    @Override
    public boolean run() {
        HuHoBot.getScheduler().runTask(this::CallEvent);

        return true;
    }
}
