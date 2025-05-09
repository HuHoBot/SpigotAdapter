package cn.huohuas001.huHoBot;

import cn.huohuas001.huHoBot.GameEvent.onChat;
import cn.huohuas001.huHoBot.NetEvent.*;
import cn.huohuas001.huHoBot.Tools.*;
import com.alibaba.fastjson2.JSONObject;
import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class HuHoBot extends JavaPlugin {
    //private static final org.slf4j.Logger log = LoggerFactory.getLogger(HuHoBot.class); //Logger
    private static final String pluginName = "HuHoBot"; //插件名
    private static HuHoBot plugin; //插件对象
    private static WebsocketClientManager clientManager; //Websocket客户端
    private static TaskScheduler scheduler; //计划对象
    //NetEvent对象
    public bindRequest bindRequestObj;
    private final Map<String, EventRunner> eventList = new HashMap<>(); //事件列表
    private Logger logger; //Logger
    private CustomCommand customCommand; //自定义命令对象
    public static ConfigManager configManager;

    /**
     * 获取插件
     *
     * @return 插件对象
     */
    public static HuHoBot getPlugin() {
        return plugin;
    }


    /**
     * 获取计划对象
     *
     * @return 计划对象
     */
    public static TaskScheduler getScheduler() {
        return scheduler;
    }

    /**
     * 获取插件名
     *
     * @return 插件名
     */
    public static String getPluginName() {
        return pluginName;
    }

    public static WebsocketClientManager getClientManager() {
        return clientManager;
    }

    @Override
    public void onEnable() {
        //初始化变量
        plugin = this;
        logger = getLogger();
        scheduler = UniversalScheduler.getScheduler(this);

        configManager = new ConfigManager(this);

        if (configManager.checkConfig()) {
            configManager.migrateConfig();
        }


        //检测是否为null
        FileConfiguration config = getConfig();
        if (config.get("serverId") == null) {
            String uuidString = PackId.getPackID();
            config.set("serverId", uuidString);
            saveConfig();
        }

        //优化插件配置
        if (config.get("customCommand") == null) {
            config.set("customCommand", Collections.emptyList());
            saveConfig();
        }

        //初始化命令
        this.getCommand("huhobot").setExecutor(new CommandManager());
        this.customCommand = new CustomCommand(this);
        customCommand.loadCommandsFromConfig();

        getServer().getPluginManager().registerEvents(new onChat(), this);

        //初始化事件
        totalRegEvent();

        //连接
        clientManager = new WebsocketClientManager();
        clientManager.connectServer();

        int pluginId = 25692;
        Metrics metrics = new Metrics(this, pluginId);

        // Optional: Add custom charts
        metrics.addCustomChart(new Metrics.SimplePie("chart_id", () -> "Use HuHoBot"));

        logger.info("HuHoBot Loaded. By HuoHuas001");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

    }

    /**
     * 重连HuHoBot服务器
     *
     * @return 是否连接成功
     */
    public boolean reconnect() {
        if (clientManager.isOpen()) {
            return false;
        }
        clientManager.connectServer();
        return true;
    }

    /**
     * 断连HuHoBot服务器
     *
     * @return 是否断连成功
     */
    public boolean disConnectServer() {
        clientManager.setShouldReconnect(false);
        return clientManager.shutdownClient();
    }

    /**
     * 重载插件配置文件
     *
     * @return 是否重载成功
     */
    public boolean reloadBotConfig() {
        reloadConfig();
        customCommand.loadCommandsFromConfig();
        return true;
    }

    /**
     * 返回是否已经被绑定成功
     *
     * @return 是否绑定成功
     */
    public boolean isBind() {
        FileConfiguration config = getConfig();
        String hashKey = config.getString("hashKey");
        return hashKey != null && !hashKey.isEmpty();
    }

    /**
     * 在控制台输出绑定ID
     */
    public void sendBindMessage() {
        if (!configManager.isHashKeyValue()) {
            String serverId = getConfig().getString("serverId");
            String message = "服务器尚未在机器人进行绑定，请在群内输入\"/绑定 " + serverId + "\"";
            logger.warning(message);
        }

    }

    /**
     * 获取服务器名
     *
     * @return 服务器名
     */
    public String getServerName() {
        return getServer().getName();
    }

    /**
     * 运行命令
     *
     * @param command 命令
     */
    public void runCommand(String command) {
        String newPackId = PackId.getPackID();
        runCommand(command, newPackId);
    }

    /**
     * 运行命令
     *
     * @param command 命令
     * @param packId  消息包ID
     */
    public void runCommand(String command, String packId) {
        HuHoBot.getScheduler().runTaskAsynchronously(() -> {
            String sendCmdMsg = ServerManager.sendCmd(command, true, true);
            clientManager.getClient().respone("已执行.\n" + sendCmdMsg, "success", packId);
        });
    }

    /**
     * 注册Websocket事件
     *
     * @param eventName 事件名称
     * @param event     事件对象
     */
    private void registerEvent(String eventName, EventRunner event) {
        eventList.put(eventName, event);
    }


    /**
     * 统一事件注册
     */
    private void totalRegEvent() {
        registerEvent("sendConfig", new SendConfig());
        registerEvent("shaked", new Shaked());
        registerEvent("chat", new Chat());
        registerEvent("add", new AddAllowList());
        registerEvent("delete", new DelAllowList());
        registerEvent("cmd", new RunCommand());
        registerEvent("queryList", new QueryAllowList());
        registerEvent("queryOnline", new QueryOnline());
        registerEvent("shutdown", new ShutDown());
        registerEvent("run", new CustomRun());
        registerEvent("runAdmin", new CustomRunAdmin());
        registerEvent("heart", new Heart());
        bindRequestObj = new bindRequest();
        registerEvent("bindRequest", bindRequestObj);
    }

    /**
     * 当收到Websocket消息时的回调
     *
     * @param data 回调数据
     */
    public void onWsMsg(JSONObject data) {
        JSONObject header = data.getJSONObject("header");
        JSONObject body = data.getJSONObject("body");

        String type = header.getString("type");
        String packId = header.getString("id");

        EventRunner event = eventList.get(type);
        if (event != null) {
            event.EventCall(packId, body);
        } else {
            logger.severe(ChatColor.DARK_RED + "在处理消息是遇到错误: 未知的消息类型");
            logger.severe(ChatColor.DARK_RED + "此错误具有不可容错性!请检查插件是否为最新!");
            logger.info(ChatColor.AQUA + "正在断开连接...");
            clientManager.shutdownClient();
        }

    }


    /**
     * 获取自定义命令对象
     *
     * @return CommandMap
     */
    public Map<String, CommandObject> getCommandMap() {
        return customCommand.getCommandMap();
    }
}
