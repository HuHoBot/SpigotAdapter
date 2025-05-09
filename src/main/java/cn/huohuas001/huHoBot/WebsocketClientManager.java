package cn.huohuas001.huHoBot;

import cn.huohuas001.config.ServerConfig;
import com.alibaba.fastjson2.JSONObject;
import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;
import net.md_5.bungee.api.ChatColor;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class WebsocketClientManager {
    private static WsClient client; //Websocket客户端
    private static String websocketUrl = ServerConfig.WS_SERVER_URL;
    private final long RECONNECT_DELAY = 5; // 重连延迟时间，单位为秒
    private final int MAX_RECONNECT_ATTEMPTS = 5; // 最大重连尝试次数
    private final HuHoBot plugin;
    private final Logger logger;
    private int ReconnectAttempts = 0;
    private boolean shouldReconnect = true; // 控制是否重连的变量
    private MyScheduledTask currentTask;
    private MyScheduledTask autoDisConnectTask;

    public WebsocketClientManager() {
        plugin = HuHoBot.getPlugin();
        logger = plugin.getLogger();
    }

    /**
     * 设置是否应该重连
     *
     * @param shouldReconnect 是否应该重连
     */
    public void setShouldReconnect(boolean shouldReconnect) {
        this.shouldReconnect = shouldReconnect;
    }

    /**
     * 客户端自动重连循环
     */
    private void autoReconnect() {
        synchronized (this) {
            ReconnectAttempts++;
            if (ReconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
                logger.warning(" 重连尝试已达到最大次数，将不再尝试重新连接。");
                cancelCurrentTask();
                return;
            }
            if (!shouldReconnect) {
                cancelCurrentTask();
                return;
            }
            logger.info(" 正在尝试重新连接,这是第(" + ReconnectAttempts + "/" + MAX_RECONNECT_ATTEMPTS + ")次连接");
            this.connectServer();
        }
    }

    public void cancelCurrentTask() {
        if (currentTask != null) {
            currentTask.cancel();
            currentTask = null;
            ReconnectAttempts = 0;
        }
    }

    public WsClient getClient() {
        return client;
    }

    public boolean shutdownClient() {
        if (client != null && client.isOpen()) {
            client.close(1000);
            return true;
        }
        return false;
    }

    public void autoDisConnectClient() {
        logger.info("连接超时，已自动重连");
        shutdownClient();
    }

    public void setAutoDisConnectTask() {
        if (autoDisConnectTask == null) {
            autoDisConnectTask = HuHoBot.getScheduler().runTaskLater(this::autoDisConnectClient, 6 * 60 * 60 * 20L);
        } else {
            autoDisConnectTask.cancel();
            autoDisConnectTask = null;
            setAutoDisConnectTask();
        }
    }

    /**
     * 连接HuHoBot服务器
     */
    public boolean connectServer() {
        logger.info(" 正在连接服务端...");
        try {
            URI uri = new URI(websocketUrl);
            if (client == null || !client.isOpen()) {
                client = new WsClient(uri, this);
                setShouldReconnect(true); // 设置是否重连
                client.connect();
            }
            return true;
        } catch (URISyntaxException e) {
            logger.severe(ChatColor.DARK_RED + e.getStackTrace().toString());
        }
        return false;
    }

    public boolean isOpen() {
        return client.isOpen();
    }

    public void sendHeart() {
        client.sendMessage("heart", new JSONObject());
    }

    public void clientReconnect() {
        if (shouldReconnect && currentTask == null) {
            currentTask = HuHoBot.getScheduler().runTaskTimer(this::autoReconnect, 0, this.RECONNECT_DELAY * 20L);
        }
    }
}
