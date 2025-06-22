package cn.huohuas001.huHoBot;

import cn.huohuas001.config.ServerConfig;
import com.alibaba.fastjson2.JSONObject;
import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
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

    public boolean connectServer() {
        logger.info("正在连接服务端...");
        try {
            URI uri = new URI(websocketUrl);
            if (client == null || !client.isOpen()) {
                // Cloudflare需要设置特殊头部
                Map<String, String> headers = new HashMap<>();
                headers.put("Host", "agent-remote.txssb.cn");
                headers.put("User-Agent", "HuHoBot/1.0");

                // 创建带SSL上下文的客户端
                SSLContext sslContext = createCloudflareSSLContext();
                client = new WsClient(uri, this, headers, sslContext);

                setShouldReconnect(true);
                client.connect();
            }
            return true;
        } catch (Exception e) {
            logger.severe("连接HuHoBot失败: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private SSLContext createCloudflareSSLContext() throws Exception {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new TrustManager[]{
                new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        }, new java.security.SecureRandom());

        // 设置协议版本
        context.getDefaultSSLParameters().setEndpointIdentificationAlgorithm("");
        context.getDefaultSSLParameters().setProtocols(new String[]{"TLSv1.2", "TLSv1.3"});
        return context;
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
