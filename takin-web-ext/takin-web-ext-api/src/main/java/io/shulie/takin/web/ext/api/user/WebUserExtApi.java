package io.shulie.takin.web.ext.api.user;

import java.util.List;
import java.util.Map;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import io.shulie.takin.plugin.framework.core.extension.ExtensionPoint;
import io.shulie.takin.web.ext.entity.AuthQueryParamCommonExt;
import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import io.shulie.takin.web.ext.entity.UserCommonExt;
import io.shulie.takin.web.ext.entity.UserExt;

/**
 * @author hezhongqi
 * @date 2021/7/29 20:30
 */
public interface WebUserExtApi extends ExtensionPoint {
    /**
     * 填充用户数据 简单补充用户数据
     * 用于：
     * 1.mybatis 拦截补充用户数据，插入 + 更新
     * 2.也可用于补充逻辑上的用户数据
     * @param userCommonExt
     */
    void fillUserData(UserCommonExt userCommonExt);

    /**
     * 填充查询参数
     * 用于：query
     * 1.补充查询条件中用户数据
     *
     * @param queryParamCommonExt
     */
    void fillQueryParam(AuthQueryParamCommonExt queryParamCommonExt);

    /**
     * 填充返回参数
     * 用于：查询结果补充用户数据
     * 1.补充查询结果中用户数据
     * 2.补充查询结果中按钮权限的数据
     *
     * @param queryResponseCommonExt
     */
    void fillQueryResponse(AuthQueryResponseCommonExt queryResponseCommonExt);

    /**
     * 根据用户ids 获取应用
     * 用于：查询用户数据
     * todo :或许想办法如何废弃
     *
     * @param userIds
     * @return
     */
    Map<Long, UserExt> getUserMapByIds(List<Long> userIds);

    /**
     * 查询所有用户
     * 用于：初始化白名单文件
     * todo 后续想办法如何废弃
     *
     * @return
     */
    List<UserExt> selectAllUser();

    /**
     * 根据登录账号的租户查询
     * todo 后续想办法如何废弃
     *
     * @return
     */
    UserExt queryUserByKey();

    /**
     * 获取登录的租户id
     *
     * @return
     */
    Long getCustomerId();

    /**
     * 获取登录租户的key
     *
     * @return
     */
    String getTenantUserKey();

    /**
     * 获取License
     * todo 后续想办法废除
     *
     * @return
     */
    String getLicenseValue();

    /**
     * 获取登录用户
     *
     * @return
     */
    UserExt getUser();

    /**
     * 根据userid 查询 key信息
     *
     * @return
     */
    String getUserAppKey(Long userId);
    /**
     * cloud 模块 用户 补充，用于调用cloud接口
     *
     * @param cloudUserExt
     */
    void fillCloudUserData(CloudUserCommonRequestExt cloudUserExt);


}
