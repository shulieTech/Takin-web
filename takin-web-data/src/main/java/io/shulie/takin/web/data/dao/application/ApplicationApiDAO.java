package io.shulie.takin.web.data.dao.application;

import java.util.List;

import com.pamirs.takin.entity.domain.query.ApplicationApiParam;
import io.shulie.takin.web.data.param.application.ApplicationApiCreateParam;
import io.shulie.takin.web.data.param.application.ApplicationApiQueryParam;
import io.shulie.takin.web.data.param.application.ApplicationApiUpdateUserParam;
import io.shulie.takin.web.data.result.application.ApplicationApiManageResult;
import org.apache.ibatis.annotations.Param;

/**
 * @author fanxx
 * @date 2020/11/4 5:53 下午
 */
public interface ApplicationApiDAO {

    /**
     * 新增
     *
     * @param param 数据项
     * @return 影响行数
     */
    int insert(ApplicationApiCreateParam param);

    /**
     * 分配
     *
     * @param param 数据项
     * @return 影响行数
     */
    int allocationUser(ApplicationApiUpdateUserParam param);

    /**
     * 根据id删除
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入 - 选择性
     *
     * @param record 数据项
     * @return 插入行数
     */
    int insertSelective(ApplicationApiCreateParam record);

    /**
     * 根据主键获取
     *
     * @param id 主键
     * @return 数据项
     */
    ApplicationApiManageResult selectByPrimaryKey(Long id);

    /**
     * 通过主键更新-选择性
     *
     * @param record 更新内容
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(ApplicationApiCreateParam record);

    /**
     * 通过主键更新-全量
     *
     * @param record 更新内容
     * @return 影响行数
     */
    int updateByPrimaryKey(ApplicationApiCreateParam record);

    /**
     * 查询
     *
     * @return 数据项
     */
    List<ApplicationApiManageResult> query();

    /**
     * 检查查询
     *
     * @param apiParam 入参
     * @return 数据项
     */
    List<ApplicationApiManageResult> querySimple(ApplicationApiParam apiParam);

    /**
     * 根据租户查询
     *
     * @param apiParam 入参
     * @return 数据项
     */
    List<ApplicationApiManageResult> querySimpleWithTenant(ApplicationApiParam apiParam);

    /**
     * 查询
     *
     * @param record  入参
     * @param userIds 用户主键
     * @return 数据项
     */
    List<ApplicationApiManageResult> selectBySelective(@Param("record") ApplicationApiQueryParam record,
                                                       @Param("userIds") List<Long> userIds, @Param("deptIds") List<Long> deptIds);

    /**
     * 批量新增
     *
     * @param list 数据项
     * @return 影响行数
     */
    int insertBatch(List<ApplicationApiCreateParam> list);

    /**
     * 根据应用删除 只删除上报的
     *
     * @param appName 应用名称
     */
    void deleteByAppName(String appName);

    boolean check(String applicationName, String method, String api);
}
