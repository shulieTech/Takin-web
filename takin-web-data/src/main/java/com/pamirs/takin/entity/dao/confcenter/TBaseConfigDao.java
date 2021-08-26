package com.pamirs.takin.entity.dao.confcenter;

import com.pamirs.takin.entity.domain.entity.TBaseConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * takin基础配置
 *
 * @author 298403
 * @date 2019-03-28
 */

@Mapper
public interface TBaseConfigDao {
    /**
     * 通过配置编码 主键删除
     *
     * @param configCode
     * @return
     */
    int deleteByPrimaryKey(String configCode);

    /**
     * 插入一条数据
     *
     * @param record
     * @return
     */
    int insert(TBaseConfig record);

    /**
     * 不为null的值 选择性插入
     *
     * @param record
     * @return
     */
    int insertSelective(TBaseConfig record);

    /**
     * 通过主键 配置编码 搜索
     *
     * @param configCode
     * @return
     */
    TBaseConfig selectByPrimaryKey(String configCode);

    /**
     * 根据主键 配置编码 选择性字段更新
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(TBaseConfig record);

    /**
     * 根据主键 配置编码 全部更新
     *
     * @param record
     * @return
     */
    int updateByPrimaryKey(TBaseConfig record);
}
