package io.shulie.takin.web.data.dao.pradar;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.shulie.takin.web.data.param.pradarconfig.PagePradarZkConfigParam;
import io.shulie.takin.web.data.param.pradarconfig.PradarConfigCreateParam;
import io.shulie.takin.web.data.result.pradarzkconfig.PradarZkConfigResult;

/**
 * @author junshao
 * @date 2021/07/08 2:35 下午
 */
public interface PradarZkConfigDAO {

    /**
     * 创建
     *
     * @param createParam 创建所需参数
     * @return 是否成功
     */
    boolean insert(PradarConfigCreateParam createParam);

    /**
     * 只更新系统的配置
     *
     * @param updateParam 更新所需数据
     * @return 是否更新成功
     */
    boolean update(PradarConfigCreateParam updateParam);

    int delete(PradarConfigCreateParam deleteParam);

    /**
     * 系统配置的列表
     *
     * @return pradarConfig 系统配置的列表
     */
    List<PradarZkConfigResult> list();

    /**
     * 通过id获得详情
     *
     * @param id 主键id
     * @return 配置详情
     */
    PradarZkConfigResult getById(Long id);

    /**
     * 通过 zk path 获得配置
     * 如果自己的存在， 就使用自己的， 不存在， 就使用
     *
     * @param zkPath zk路径
     * @return 配置
     */
    PradarZkConfigResult getByZkPath(String zkPath);

    /**
     * pradarZkConfig 分页列表
     * 先找租户自己的，没有就找系统的
     *
     * @param pageBaseDTO 分页参数
     * @param param 筛选条件
     * @return 分页列表
     */
    IPage<PradarZkConfigResult> page(PagePradarZkConfigParam param, PageBaseDTO pageBaseDTO);

    /**
     * pradarZkConfig 分页列表
     *
     * @param sysDefaultTenantId 系统租户id
     * @param sysDefaultEnvCode  系统环境
     * @param pageBaseDTO        分页参数
     * @return pradarZkConfig 分页列表
     */
    PagingList<PradarZkConfigResult> page(Long sysDefaultTenantId, String sysDefaultEnvCode, PageBaseDTO pageBaseDTO);

    /**
     * 根据id删除
     *
     * @param id        主键id
     * @return 是否成功
     */
    boolean deleteById(Long id);

}
