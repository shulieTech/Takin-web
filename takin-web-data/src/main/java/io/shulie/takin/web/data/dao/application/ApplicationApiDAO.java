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
     * @param param
     * @return
     */
    int insert(ApplicationApiCreateParam param);

    /**
     * 分配
     * @param param
     * @return
     */
    int allocationUser(ApplicationApiUpdateUserParam param);

    /**
     * 根据id删除
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入
     * @param record
     * @return
     */
    int insertSelective(ApplicationApiCreateParam record);

    /**
     * 获取
     * @param id
     * @return
     */
    ApplicationApiManageResult selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ApplicationApiCreateParam record);

    int updateByPrimaryKey(ApplicationApiCreateParam record);

    List<ApplicationApiManageResult> query();

    List<ApplicationApiManageResult> querySimple(ApplicationApiParam apiParam);

    /**
     * 根据租户查询
     * @param apiParam
     * @return
     */
    List<ApplicationApiManageResult> querySimpleWithTenant(ApplicationApiParam apiParam);

    List<ApplicationApiManageResult> selectBySelective(@Param("record") ApplicationApiQueryParam record,@Param("userIds") List<Long> userIds);

    /**
     * 批量新增
     * @param list
     * @return
     */
    int insertBatch(List<ApplicationApiCreateParam> list);

    /**
     * 根据应用删除 只删除上报的
     * @param appName
     */
    void deleteByAppName(String appName);


}
