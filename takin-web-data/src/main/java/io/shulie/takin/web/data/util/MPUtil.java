package io.shulie.takin.web.data.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.shulie.takin.web.ext.util.WebPluginUtils;

/**
 * mybatis-plus 辅助工具类
 *
 * @param <T> 相对应实体类
 * @author liuchuan
 * @date 2019/6/19
 */
public interface MPUtil<T> {

    /**
     * 一个
     */
    String LIMIT_ONE = "limit 1";

    /* ---------------- 默认方法, 实现后直接调用 -------------- */

    /**
     * 设置分页参数
     *
     * @param pageBaseDTO 分页参数类
     * @return mybatis plus page 类
     */
    default Page<T> setPage(PageBaseDTO pageBaseDTO) {
        return new Page<>(pageBaseDTO.getRealCurrent(), pageBaseDTO.getPageSize());
    }


    /**
     * 设置分页参数
     *
     * @param page 当前页数
     * @param size 每页大小
     * @return mybatis plus page 类
     */
    default Page<T> setPage(Integer page, Integer size) {
        return new Page<>(page, size);
    }

    /**
     * 设置分页参数, 无总数的返回
     *
     * @param pageBaseDTO 分页参数类
     * @return mybatis plus page 类
     */
    default Page<T> setPageNoCount(PageBaseDTO pageBaseDTO) {
        return setPage(pageBaseDTO, false);
    }

    /**
     * 设置分页参数, 根据入参, 返回是否需要总数的返回
     *
     * @param pageBaseDTO 分页参数类
     * @param isSearchCount 是否需要总数
     * @return mybatis plus page 类
     */
    default Page<T> setPage(PageBaseDTO pageBaseDTO, boolean isSearchCount) {
        return new Page<>(pageBaseDTO.getRealCurrent(), pageBaseDTO.getPageSize(), isSearchCount);
    }

    /**
     * 获得 query 包装类
     *
     * @return query 普通包装类
     */
    default QueryWrapper<T> getQueryWrapper() {
        return new QueryWrapper<>();
    }

    /**
     * 获得 query 包装类, 带有租户id
     *
     * @return query 普通包装类, 带有租户id
     */
    default QueryWrapper<T> getCustomerQueryWrapper() {
        return this.getQueryWrapper().eq("customer_id", WebPluginUtils.getCustomerId());
    }

    /**
     * 获得 query lambda 包装类
     *
     * @return query lambda 包装类
     */
    default LambdaQueryWrapper<T> getLambdaQueryWrapper() {
        return new LambdaQueryWrapper<>();
    }

    /**
     * 获得 query lambda 包装类, 查详情使用, limit 1
     *
     * @return query lambda 包装类
     */
    default LambdaQueryWrapper<T> getLimitOneLambdaQueryWrapper() {
        return this.getLambdaQueryWrapper().last(LIMIT_ONE);
    }

    /**
     * 获得 query lambda 包装类, 带有租户id
     *
     * @return query lambda 包装类, 带有租户id
     */
    default LambdaQueryWrapper<T> getCustomerLambdaQueryWrapper() {
        return this.getCustomerQueryWrapper().lambda();
    }

    /**
     * 获得 query lambda 包装类, 带有租户id, 查详情使用, limit 1
     *
     * @return query lambda 包装类, 带有租户id
     */
    default LambdaQueryWrapper<T> getCustomerLimitOneLambdaQueryWrapper() {
        return this.getCustomerQueryWrapper().lambda().last(LIMIT_ONE);
    }

    /**
     * 获得 update 包装类
     *
     * @return update 普通包装类
     */
    default UpdateWrapper<T> getUpdateWrapper() {
        return new UpdateWrapper<>();
    }

    /**
     * 获得 update lambda 包装类
     *
     * @return update lambda 包装类
     */
    default LambdaUpdateWrapper<T> getLambdaUpdateWrapper() {
        return new LambdaUpdateWrapper<>();
    }

    /* ---------------- 静态方法, 类名.xx() 调用 -------------- */

    /**
     * 静态方法获得 query 包装类
     *
     * @param <T> 实体类
     * @return query 包装类
     */
    static <T> QueryWrapper<T> getQueryWrapperStatic() {
        return new QueryWrapper<>();
    }

    /**
     * 静态方法获得 query lambda 包装类
     *
     * @param <T> 实体类
     * @return query lambda 包装类
     */
    static <T> LambdaQueryWrapper<T> getLambdaQueryWrapperStatic() {
        return new LambdaQueryWrapper<>();
    }

    /**
     * 静态方法 获得 update 包装类
     *
     * @param <T> 实体类
     * @return update 包装类
     */
    static <T> UpdateWrapper<T> getUpdateWrapperStatic() {
        return new UpdateWrapper<>();
    }


    /**
     * 静态方法 获得 update lambda 包装类
     *
     * @param <T> 实体类
     * @return update lambda 包装类
     */
    static <T> LambdaUpdateWrapper<T> getLambdaUpdateWrapperStatic() {
        return new LambdaUpdateWrapper<>();
    }


}
