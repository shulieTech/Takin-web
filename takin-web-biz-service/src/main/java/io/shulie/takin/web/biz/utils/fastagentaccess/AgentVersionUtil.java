package io.shulie.takin.web.biz.utils.fastagentaccess;

/**
 * agent版本处理工具类
 *
 * @author ocean_wll
 * @date 2021/8/16 6:35 下午
 */
public class AgentVersionUtil {

    /**
     * 将4位的版本号转成int
     * ps:定义每个版本数字不超过3位
     *
     * @param version x.x.x.x
     * @return int值
     */
    public static Long string2Long(String version) {
        String[] arr = version.split("\\.");
        if (arr.length < 1) {
            return -1L;
        }

        long rs = 0;
        for (int i = 0; i < arr.length; i++) {
            long e = Long.parseLong(arr[i]) << (10 * (arr.length - i - 1));
            rs += e;
        }
        return rs;
    }

    /**
     * 比较版本大小
     * <p>
     * 说明：支n位基础版本号+1位子版本号
     * 示例：1.0.2>1.0.1 , 1.0.1.1>1.0.1
     *
     * @param version1 版本1
     * @param version2 版本2
     * @return 0:相同 1:version1大于version2 -1:version1小于version2
     */
    public static int compareVersion(String version1, String version2, boolean reverse) {
        if (version1.equals(version2)) {
            return 0; //版本相同
        }
        String[] v1Array = version1.split("\\.");
        String[] v2Array = version2.split("\\.");
        int v1Len = v1Array.length;
        int v2Len = v2Array.length;
        int baseLen = 0; //基础版本号位数（取长度小的）
        if (v1Len > v2Len) {
            baseLen = v2Len;
        } else {
            baseLen = v1Len;
        }
        for (int i = 0; i < baseLen; i++) {
            if (v1Array[i].equals(v2Array[i])) {
                continue;
            } else {
                return Integer.parseInt(v1Array[i]) > Integer.parseInt(v2Array[i]) && reverse ? 1 : -1;
            }
        }
        //基础版本相同，再比较子版本号
        if (v1Len != v2Len) {
            return v1Len > v2Len && reverse ? 1 : -1;
        } else {
            //基础版本相同，无子版本号
            return 0;
        }
    }

    public static void main(String[] args) {
        System.out.println(compareVersion("1.2","1.1",true));
    }
}
