package com.pamirs.takin.entity.dao.dict;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface TDictDao {

    /**
     * 说明: 查询白名单字典列表或者查询链路等级列表
     *
     * @return 白名单字典列表或者链路等级列表
     * @author shulie
     */
    List<Map<String, Object>> queryDicList(@Param("TYPE_ALIAS") String TYPE_ALIAS,@Param("tenantId")Long tenandId,@Param("envCode")String envCode);
}
