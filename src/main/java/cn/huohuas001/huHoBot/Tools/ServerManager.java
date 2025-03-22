package cn.huohuas001.huHoBot.Tools;

import cn.huohuas001.huHoBot.HuHoBot;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 服务器管理
 */
public class ServerManager {

    public static List<String> msgList = new LinkedList<>();

    public static String getMessageLogStripColor(String msgLog) {
        return ChatColor.stripColor(msgLog.toString());
    }

    public static String sendCmd(String cmd, boolean disPlay, boolean clearColor) {
        AtomicReference<String> returnStr = new AtomicReference<>("无返回值");

        CommandSender commandSender = new ConsoleSender();

        HuHoBot.getScheduler().runTask(() -> {
            msgList.clear();
            HuHoBot.getPlugin().getServer().dispatchCommand(commandSender, cmd);
        });

        HuHoBot.getScheduler().runTaskLaterAsynchronously(() -> {
            synchronized (returnStr) {
                returnStr.notify();
                StringBuilder stringBuilder = new StringBuilder();
                if (msgList.size() == 0) {
                    msgList.add("无返回值");
                }
                for (String msg : msgList) {
                    if (msgList.get(msgList.size() - 1).equalsIgnoreCase(msg)) {
                        stringBuilder.append(msg);
                    } else {
                        stringBuilder.append(msg).append("\n");
                    }
                }
                if (!disPlay) {
                    msgList.clear();
                    returnStr.set("无返回值");
                }
                if (stringBuilder.toString().length() <= 5000) {
                    String ret = stringBuilder.toString();
                    if (clearColor) {
                        ret = getMessageLogStripColor(ret);
                    }
                    returnStr.set(ret);
                } else {
                    returnStr.set("返回值过长");
                }
                msgList.clear();
            }
        }, 20L);

        synchronized (returnStr) {
            try {
                returnStr.wait(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return returnStr.get();
        }
    }
}

