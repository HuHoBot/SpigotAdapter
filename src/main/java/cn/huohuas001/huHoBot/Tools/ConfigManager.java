package cn.huohuas001.huHoBot.Tools;

import cn.huohuas001.huHoBot.HuHoBot;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ConfigManager {
    private final HuHoBot plugin;
    private final File configFile;
    private final File oldConfigFile;
    private final int version;

    public ConfigManager(HuHoBot plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        this.oldConfigFile = new File(plugin.getDataFolder(), "config_old.yml");
        this.version = 2;
    }

    //检测是否该修改配置文件
    public boolean checkConfig() {
        if (configFile.exists()) {
            int version = plugin.getConfig().contains("version") ? plugin.getConfig().getInt("version") : -1;
            plugin.getLogger().info(String.valueOf(version));
            if (this.version > version) {
                return false;
            }
        }
        return true;
    }

    private void migrateServerUrl(FileConfiguration oldConfig, FileConfiguration newConfig) {
        if (oldConfig.contains("motd.server_url") || oldConfig.contains("motdUrl")) {
            String oldUrl = oldConfig.getString("motd.server_url") != null ? oldConfig.getString("motd.server_url") : oldConfig.getString("motdUrl");

            // 拆分地址和端口
            String[] parts = oldUrl.split(":");
            String ip = parts.length > 0 ? parts[0] : "localhost";
            int port = 25565; // 默认端口

            try {
                if (parts.length > 1) {
                    port = Integer.parseInt(parts[1]);
                }
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("无效的端口号: " + parts[1] + " 已使用默认 25565");
            }

            // 只有当新配置不存在时才设置
            newConfig.set("motd.server_ip", ip);
            newConfig.set("motd.server_port", port);

            // 记录迁移日志
            plugin.getLogger().info("已迁移 MOTD 地址: " + oldUrl + " → " + ip + ":" + port);
        }
    }

    public void migrateConfig() {
        try {
            // 1. 备份旧配置
            if (configFile.exists()) {
                Files.move(configFile.toPath(), oldConfigFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                plugin.getLogger().info("已备份旧配置文件至 config_old.yml");
            }

            // 2. 生成新配置文件
            plugin.saveDefaultConfig();
            plugin.reloadConfig();
            FileConfiguration newConfig = plugin.getConfig();

            // 3. 迁移旧配置数据
            if (oldConfigFile.exists()) {
                FileConfiguration oldConfig = YamlConfiguration.loadConfiguration(oldConfigFile);

                // 使用类型安全的迁移方法
                migrateValue(oldConfig, newConfig, "serverId");
                migrateValue(oldConfig, newConfig, "hashKey");

                // 迁移聊天格式
                migrateNested(oldConfig, newConfig, "chatFormatGame", "chatFormat.from_game");
                migrateNested(oldConfig, newConfig, "chatFormatGroup", "chatFormat.from_group");

                // 迁移MOTD设置
                migrateServerUrl(oldConfig, newConfig);

                // 迁移白名单命令
                migrateNested(oldConfig, newConfig, "addWhiteListCmd", "whiteList.add");
                migrateNested(oldConfig, newConfig, "delWhiteListCmd", "whiteList.del");

                // 迁移自定义命令（保持结构不变）
                if (oldConfig.isConfigurationSection("customCommand")) {
                    newConfig.set("customCommand", oldConfig.get("customCommand"));
                }

                // 设置新版本号
                newConfig.set("version", this.version);

                // 保存更新后的配置
                plugin.saveConfig();
                plugin.getLogger().info("配置文件迁移完成");
            }
        } catch (IOException e) {
            plugin.getLogger().severe("配置文件迁移失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void migrateValue(FileConfiguration oldConfig, FileConfiguration newConfig, String path) {
        if (oldConfig.contains(path)) {
            Object value = oldConfig.get(path);
            // 保留新配置的注释，只有当新配置不存在该路径时才覆盖
            if (!newConfig.contains(path)) {
                newConfig.set(path, value);
            }
        }
    }

    private void migrateNested(FileConfiguration oldConfig, FileConfiguration newConfig,
                               String oldPath, String newPath) {
        if (oldConfig.contains(oldPath)) {
            Object value = oldConfig.get(oldPath);
            // 只有当新配置不存在目标路径时才迁移
            if (!newConfig.contains(newPath)) {
                newConfig.set(newPath, value);
            }
        }
    }
}

