package io.shulie.takin.web.common.util;

import java.util.UUID;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import io.shulie.takin.cloud.ext.content.enums.RpcTypeEnum;
import io.shulie.takin.web.common.enums.activity.BusinessTypeEnum;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author shiyajian
 * create: 2021-01-12
 */
public class ActivityUtil {

    /**
     * 判断是否是正常业务活动
     *
     * @param type 业务活动类型
     * @return 是否是正常业务活动
     */
    public static boolean isNormalBusiness(Integer type) {
        return BusinessTypeEnum.NORMAL_BUSINESS.getType().equals(type);
    }

    /**
     * linkId
     *
     * @param serviceName 服务名称
     * @param methodName  方法名称
     * @param appName     应用名称
     * @param rpcType     rpcType
     * @param extend      扩展字段
     * @return linkId 链路id
     */
    public static String createLinkId(String serviceName, String methodName, String appName, String rpcType, String extend) {
        StringBuffer tags = new StringBuffer();
        tags.append(objectToString(serviceName, ""))
                .append("|")
                .append(objectToString(methodName, ""))
                .append("|")
                .append(objectToString(appName, ""))
                .append("|")
                .append(objectToString(rpcType, ""))
                .append("|")
                .append("");
        // 保持跟amdb生成的md5一致
        return MD5Tool.md5(tags.toString());
    }

    private static String objectToString(Object value, String defaultStr) {
        if (value == null || "null".equalsIgnoreCase(value.toString())) {
            return "";
        }
        return ObjectUtils.toString(value);
    }

    public static String buildEntrance(String methodName, String serviceName, String rpcType) {
        if (StrUtil.isBlank(methodName)) {
            methodName = "METHOD_NAME";
        }
        /*
        if (RpcTypeEnum.MQ.getValue().equals(rpcType)) {
            return StringUtils.join(Lists.newArrayList(serviceName, rpcType), "|");
        }
        */
        return StringUtils.join(Lists.newArrayList(methodName, serviceName, rpcType), "|");
    }

    /**
     * 获取s
     *
     * @param virtualEntrance
     * @param rpcType
     * @return
     */
    public static String buildVirtualEntrance(String methodName, String virtualEntrance, String rpcType) {
        if (StringUtils.isNotBlank(methodName)) {
            return StringUtils.join(Lists.newArrayList(methodName, virtualEntrance, rpcType), "|");
        }
        if (StringUtils.isNotBlank(rpcType)) {
            return StringUtils.join(Lists.newArrayList(virtualEntrance, rpcType), "|");
        } else {
            return virtualEntrance;
        }

    }

    public static String serviceNameLabel(String serviceName, String methodName) {
        return serviceName + "#" + methodName;
    }

    /**
     * 1、应用名称
     * 2、methodName
     * 3、serviceName
     * 4、rpcType
     */
    public static EntranceJoinEntity covertEntrance(String dbEntrance) {
        String[] split = StringUtils.split(dbEntrance, "\\|");
        if (split.length == 2) {
            EntranceJoinEntity entranceJoinEntity = new EntranceJoinEntity();
            entranceJoinEntity.setServiceName(split[0]);
            entranceJoinEntity.setRpcType(split[1]);
            return entranceJoinEntity;
        }
        if (split.length != 3) {
            return new EntranceJoinEntity();
        }
        EntranceJoinEntity entranceJoinEntity = new EntranceJoinEntity();
        entranceJoinEntity.setMethodName(split[0]);
        entranceJoinEntity.setServiceName(split[1]);
        entranceJoinEntity.setRpcType(split[2]);
        return entranceJoinEntity;
    }

    public static String toEntrance(EntranceJoinEntity entranceJoinEntity) {
        return StringUtils.join(
                Lists.newArrayList(
                        entranceJoinEntity.getMethodName(),
                        entranceJoinEntity.getServiceName(),
                        entranceJoinEntity.getRpcType()
                ), "|"
        );
    }

    /**
     * 1、虚拟入口
     */
    public static EntranceJoinEntity covertVirtualEntrance(String dbEntrance) {
        String[] split = StringUtils.split(dbEntrance, "\\|");
        EntranceJoinEntity entranceJoinEntity = new EntranceJoinEntity();
        if (split.length == 1) {
            entranceJoinEntity.setVirtualEntrance(dbEntrance);
        } else if (split.length == 2) {
            // 服务入口
            entranceJoinEntity.setVirtualEntrance(split[0]);
            entranceJoinEntity.setRpcType(split[1]);
        } else if (split.length == 3) {
            // 服务入口
            entranceJoinEntity.setMethodName(split[0]);
            entranceJoinEntity.setVirtualEntrance(split[1]);
            entranceJoinEntity.setServiceName(split[1]);
            entranceJoinEntity.setRpcType(split[2]);
        } else {
            entranceJoinEntity.setVirtualEntrance(dbEntrance);
            entranceJoinEntity.setRpcType("-1");
        }
        return entranceJoinEntity;
    }

    /**
     * 虚拟业务活动转换
     *
     * @param dbEntrance 入口
     * @return 业务活动入口转换对象
     */
    public static EntranceJoinEntity covertVirtualEntranceV2(String dbEntrance) {
        String[] split = StringUtils.split(dbEntrance, "\\|");
        EntranceJoinEntity entranceJoinEntity = new EntranceJoinEntity();
        entranceJoinEntity.setServiceName(split[0]);
        entranceJoinEntity.setRpcType(split[1]);
        return entranceJoinEntity;
    }

    /**
     * 根据入口, 业务活动类型, 获得业务活动入口转换对象
     *
     * @param entrance 入口
     * @param type     业务活动类型, 1 虚拟业务活动, 0 正常业务活动
     * @return 业务活动入口转换对象
     */
    public static EntranceJoinEntity getEntranceJoinEntityByEntranceAndType(String entrance, Integer type) {
        return ActivityUtil.isNormalBusiness(type) ? ActivityUtil.covertEntrance(entrance)
                : ActivityUtil.covertVirtualEntranceV2(entrance);
    }

    public static EntranceJoinEntity covertEntranceV2(String dbEntrance) {
        String[] split = StringUtils.split(dbEntrance, "\\|");
        EntranceJoinEntity entranceJoinEntity = new EntranceJoinEntity();
        if(split.length == 3) {
            entranceJoinEntity.setMethodName(split[0]);
            entranceJoinEntity.setServiceName(split[1]);
            entranceJoinEntity.setRpcType(split[2]);
        } else if(split.length == 2) {
            entranceJoinEntity.setServiceName(split[0]);
            entranceJoinEntity.setRpcType(split[1]);
        } else if(split.length == 1) {
            entranceJoinEntity.setServiceName(split[0]);
        }
        return entranceJoinEntity;
    }

    @Data
    public static class EntranceJoinEntity {

        private String applicationName;

        private String methodName;

        private String serviceName;

        private String rpcType;

        private String virtualEntrance;

    }
}
