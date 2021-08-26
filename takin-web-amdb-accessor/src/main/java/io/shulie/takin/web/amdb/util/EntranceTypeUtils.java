/*
 * Copyright 2021 Shulie Technology, Co.Ltd
 * Email: shulie@shulie.io
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.shulie.takin.web.amdb.util;

import io.shulie.amdb.common.enums.RpcType;
import io.shulie.takin.web.amdb.bean.common.EntranceTypeEnum;
import io.shulie.takin.web.amdb.bean.common.EntranceTypeInfo;

/**
 * @author 无涯
 * @date 2021/5/12 4:12 下午
 */
public class EntranceTypeUtils {

    /**
     * 获取rpcType HTTP -> 0
     * @param type
     * @return
     */
    public static EntranceTypeInfo getRpcType(String type) {
        EntranceTypeEnum entranceTypeEnum = EntranceTypeEnum.getEnumByName(type);
        EntranceTypeInfo info = new EntranceTypeInfo();
        switch (entranceTypeEnum) {
            case HTTP:
                info.setRpcType(RpcType.TYPE_WEB_SERVER + "");
                return info;
            case DUBBO:
                info.setRpcType(RpcType.TYPE_RPC + "");
                return info;
            case KAFKA:
            case RABBITMQ:
            case ROCKETMQ:
                info.setRpcType(RpcType.TYPE_MQ + "");
                info.setMiddlewareName(entranceTypeEnum.getType());
                return info;
            case ELASTICJOB:
                info.setRpcType(RpcType.TYPE_JOB + "");
                info.setMiddlewareName(entranceTypeEnum.getType());
                return info;
            case GRPC:
                info.setRpcType(RpcType.TYPE_RPC + "");
                info.setMiddlewareName(entranceTypeEnum.getType());
                return info;
            default:
                return info;
        }
    }

    /**
     * type 0 -> HTTP
     * @param rpcType
     * @return
     */
    public static String getType(String rpcType) {
        switch (rpcType) {
            case RpcType.TYPE_WEB_SERVER + "":
                return EntranceTypeEnum.HTTP.getType();
            case RpcType.TYPE_RPC + "":
                return EntranceTypeEnum.DUBBO.getType();
            case RpcType.TYPE_MQ + "":
                return EntranceTypeEnum.RABBITMQ.getType();
            case RpcType.TYPE_JOB + "":
                return EntranceTypeEnum.ELASTICJOB.getType();
            default:
                return "未知";
        }
    }



}
