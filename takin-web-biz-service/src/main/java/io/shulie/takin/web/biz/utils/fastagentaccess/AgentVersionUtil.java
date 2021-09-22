package io.shulie.takin.web.biz.utils.fastagentaccess;

/**
 * @Description agent版本处理工具类
 * @Author ocean_wll
 * @Date 2021/8/16 6:35 下午
 */
public class AgentVersionUtil {

    /**
     * 将4位的版本号转成int
     * ps:定义每个版本数字不超过3位
     *
     * @param version x.x.x.x
     * @return int值
     */
    public static Long string2Int(String version) {
        String[] arr = version.split("\\.");
        if (arr.length < 1) {
            return -1L;
        }

        long rs = 0;
        for (int i = 0; i < arr.length; i++) {
            int e = Integer.parseInt(arr[i]) << (10 * (arr.length - i - 1));
            rs += e;
        }
        return rs;
    }
}
