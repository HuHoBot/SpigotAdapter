## 📚 使用教程：HuHoMonitor 自定义命令系统

### 1. 准备工作

#### 1.1 确保 HuHoBot 已安装

在使用 `HuHoMonitor` 前，请确保你的服务器已正确安装并配置了 `HuHoBot` 插件。

#### 1.2 获取最新版 HuHoBot JAR 文件

从 GitHub Releases 页面下载最新版本的 `HuHoBot-Spigot.jar` 文件：

- 访问 [HuHoBot Releases](https://github.com/HuHoBot/SpigotAdapter/releases)
- 下载最新版本的 `HuHoBot-Spigot.jar`

### 2. 配置项目依赖

#### 2.1 添加 HuHoBot 作为编译时依赖

在你的 `build.gradle` 文件中添加以下内容，将本地 HuHoBot JAR 文件作为编译时依赖：

```gradle 
dependencies { compileOnly files("libs/HuHoBot-x.x.x-Spigot.jar") }
```

### 3. 开发自定义命令监听器

#### 3.1 创建监听器类

创建一个新的 Java 类 `HuHoMonitor.java`，继承 `JavaPlugin` 并实现 `Listener` 接口：

```java 
package com.yourpackage;

import cn.huohuas001.huHoBot.Api.BotCustomCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class HuHoMonitor extends JavaPlugin implements Listener {
    private Logger logger;

    @Override
    public void onEnable() {
        this.logger = getLogger();

        // 检查 HuHoBot 是否已安装
        Plugin huhoBot = getServer().getPluginManager().getPlugin("HuHoBot");
        if (huhoBot == null) {
            this.getLogger().severe("HuHoBot is not installed. Disabling...");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            Class.forName("cn.huohuas001.huHoBot.Api.BotCustomCommand");
        } catch (ClassNotFoundException e) {
            logger.severe("无法加载 BotCustomCommand 类：" + e.getMessage());
        }

        // 注册事件监听器
        this.getServer().getPluginManager().registerEvents(this, huhoBot);
    }

    @EventHandler
    public void onCommandSend(BotCustomCommand event) {
        JSONObject data = event.getData();

        // 获取关键词和参数
        String keyWord = data.getString("key");
        List<String> paramsList = data.getJSONArray("runParams").toJavaList(String.class);

        // 获取用户信息
        JSONObject author = data.getJSONObject("author");
        String qlogoUrl = author.getString("qlogoUrl");
        String bindNick = author.getString("bindNick");
        String openId = author.getString("openId");

        // 获取群组信息
        JSONObject group = data.getJSONObject("group");
        String groupOpenId = group.getString("openId");

        // 执行自定义逻辑...
        if (keyWord.equals("关键字")) {
            event.setCancelled(true);

            // 构建自定义响应
            JSONObject responseJson = new JSONObject();
            responseJson.put("text", "这是返回的文本消息");
            responseJson.put("imgUrl", "https://example.com/image.jpg");

            // 返回自定义响应
            event.response(responseJson, "custom");
        }
    }
}
```

### 4. 编译与部署

#### 4.1 编译插件

运行 Gradle 构建任务以编译你的插件：

```bash 
./gradlew shadowJar
```

#### 4.2 部署插件

将生成的 JAR 文件（位于 `build/libs` 目录）复制到 Minecraft 服务器的 `plugins` 目录，并重启服务器。

### 5. 测试自定义命令

#### 5.1 发送测试命令

在群内发送消息 `@HuHo_Bot /执行 关键词` 查看是否正确触发监听器。

### 6. 注意事项

- 确保 `HuHoBot` 和 `插件` 的版本兼容性。
- 如果遇到类找不到错误，请检查 HuHoBot JAR 文件路径和版本号是否正确。
- 根据实际需求扩展 `onCommandSend` 方法中的逻辑，实现更多自定义功能。

---

通过以上步骤，你就可以成功地开发并部署一个基于 `HuHoBot` 的自定义命令监听器插件。如果有任何问题或建议，请随时提交 Issue 或
PR！

### 附录：事件数据结构说明

在 `BotCustomCommand` 事件中，`event.data` 包含以下 JSON 数据结构：

```json 
{
  "key": "关键字",
  "runParams": [
    "参数1",
    "参数2"
  ],
  "author": {
    "qlogoUrl": "用户头像URL",
    "bindNick": "绑定昵称",
    "openId": "用户OpenID"
  },
  "group": {
    "openId": "群组OpenID"
  }
}
```

- **key**: 触发命令的关键词。
- **runParams**: 命令执行时传递的参数列表。
- **author**: 发送命令的用户信息。
    - **qlogoUrl**: 用户头像 URL。
    - **bindNick**: 用户绑定的昵称。
    - **openId**: 用户的 OpenID。
- **group**: 群组信息。
    - **openId**: 群组的 OpenID。

### 附录：返回自定义响应

如果需要返回复杂的 JSON 结构，可以使用 `JSONObject` 形式的 `response` 方法：

```java 
event.response(responseJson, "custom");
```

responseJson示例：

```json
{
  "text": "这是返回的文本消息",
  //可留空
  "imgUrl": "https://example.com/image.jpg"
  //可留空
}
```

希望这份文档能帮助你更好地理解和使用 `HuHoMonitor` 自定义命令系统！

