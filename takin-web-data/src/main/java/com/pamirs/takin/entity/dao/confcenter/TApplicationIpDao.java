package com.pamirs.takin.entity.dao.confcenter;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.TApplicationIp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 说明: application id relationship dao
 *
 * @author shulie
 * @version v1.0
 * @date 2018年4月26日
 */
@Mapper
public interface TApplicationIpDao {

    /**
     * 说明: query application - ip by application name and type
     *
     * @param applicationName 应用名称
     * @return 应用列表
     * @author shulie
     */
    public List<TApplicationIp> queryApplicationIpByNameTypeList(@Param("applicationName") String applicationName,
        @Param("type") String type);

    /**
     * 说明: query application - ip by application name and type
     *
     * @param applicationName 应用名称
     * @return 应用列表
     * @author shulie
     */
    public List<TApplicationIp> queryApplicationIpByNameList(@Param("applicationName") String applicationName);

    /**
     * 说明: query application - ip by application ip
     *
     * @param applicationIp ip地址
     * @return 应用列表
     * @author shulie
     */
    public List<TApplicationIp> queryApplicationIpByIpList(@Param("applicationIp") String applicationIp);
}
