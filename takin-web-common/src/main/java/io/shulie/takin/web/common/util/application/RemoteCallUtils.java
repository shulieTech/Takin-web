package io.shulie.takin.web.common.util.application;

import io.shulie.amdb.common.enums.RpcType;
import io.shulie.takin.web.common.enums.application.AppRemoteCallConfigEnum;
import io.shulie.takin.web.common.enums.application.AppRemoteCallTypeEnum;

/**
 * @author 无涯
 * @date 2021/6/2 3:25 下午
 */
public class RemoteCallUtils {


    /**
     * 去重
     * @param appName
     * @param type
     * @param interfaceName
     * @return
     */
    public static String buildRemoteCallName(String appName,String interfaceName,Object type) {
        return  appName + "@@"+  interfaceName + "@@" + (type == null ? "" :type);
    }

    /**
     * 导入导出用
     * @param interfaceName
     * @param type
     * @return
     */
    public static String buildImportRemoteCallName(String interfaceName,Object type) {
        return  interfaceName + "@@" + (type == null ? "" :type);
    }

    /**
     * 获取interfaceName
     * @param rpcType
     * @param serviceName
     * @param methodName
     * @return
     */
    public static String getInterfaceName(String rpcType,String serviceName,String methodName) {
        String interfaceName = "";
        switch (Integer.parseInt(rpcType)) {
            case RpcType.TYPE_WEB_SERVER:
                interfaceName = serviceName;
                break;
            case RpcType.TYPE_RPC:
                // 去掉参数列表  setUser(com.example.clientdemo.userModel)   setUser~(com.example.clientdemo
                // .userModel)
                interfaceName = serviceName.split(":")[0] + "#" + methodName.split("~")[0].split("\\(")[0];
                break;
            default:
                interfaceName = serviceName + "#" + methodName;
        }
        return interfaceName;
    }

    /**
     * 获取interfaceName
     * @param rpcName
     * @param serviceName
     * @param methodName
     * @return
     */
    public static String getInterfaceNameByRpcName(String rpcName,String serviceName,String methodName) {
        String interfaceName = "";
        AppRemoteCallTypeEnum typeEnum = AppRemoteCallTypeEnum.getEnumByDesc(rpcName.toUpperCase());
        if(typeEnum == null) {
            return serviceName;
        }
        switch (typeEnum) {
            case HTTP:
                interfaceName = serviceName;
                break;
            case FEIGN:
                interfaceName = serviceName.split(":")[0] + "#" + methodName.split("~")[0].split("\\(")[0];
                break;
            case DUBBO:
                // 去掉参数列表  setUser(com.example.clientdemo.userModel)   setUser~(com.example.clientdemo
                // .userModel)
                interfaceName = serviceName.split(":")[0] + "#" + methodName.split("~")[0].split("\\(")[0];
                break;
            default:
                interfaceName = serviceName + "#" + methodName;
        }
        return interfaceName;
    }

    /**
     * 是否校验白名单异常
     * @param interfaceType
     * @param type
     * @return
     */
    public static boolean checkWhite(Integer interfaceType, Integer type) {
        if(type == null) {
            return !interfaceType.equals(AppRemoteCallTypeEnum.FEIGN.getType());
        }else {
            return !interfaceType.equals(AppRemoteCallTypeEnum.FEIGN.getType()) && type != null
                && type.equals(AppRemoteCallConfigEnum.OPEN_WHITELIST.getType());
        }

    }
}
