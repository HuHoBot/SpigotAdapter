package cn.huohuas001.huHoBot.Tools;

import java.util.UUID;

/**
 * 包ID管理器
 */
public class PackId {
    //生成一个随机的UUID
    public static String getPackID() {
        UUID guid = UUID.randomUUID();
        return guid.toString().replace("-", "");
    }
}
