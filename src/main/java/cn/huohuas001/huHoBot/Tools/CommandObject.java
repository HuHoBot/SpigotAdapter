package cn.huohuas001.huHoBot.Tools;

/**
 * 自定义命令结构体
 */
public class CommandObject {
    private final String key;
    private final String command;
    private final int permission;

    public CommandObject(String key, String command, int permission) {
        this.key = key;
        this.command = command;
        this.permission = permission;
    }

    public String getKey() {
        return key;
    }

    public String getCommand() {
        return command;
    }

    public int getPermission() {
        return permission;
    }

    @Override
    public String toString() {
        return "CommandObject{" +
                "key='" + key + '\'' +
                ", command='" + command + '\'' +
                ", permission=" + permission +
                '}';
    }
}
