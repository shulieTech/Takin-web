package io.shulie.takin.web.data.param.scriptmanage;

import java.util.List;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class ScriptManageDeployPageQueryParam extends PagingDevice {

    private static final long serialVersionUID = 1345020969991489239L;
    /**
     * 脚本名称
     */
    private String name;

    /**
     * 关联类型(业务活动)
     */
    private String refType;

    /**
     * 关联值(活动id)
     */
    private String refValue;

    /**
     * 应用实例状态 0代表新建，1代表调试通过；2.历史版本
     */
    private List<Integer> statusList;

    /**
     * 脚本发布实例id列表
     */
    private List<Long> scriptDeployIds;


    private List<Long> scriptIds;

    /**
     * 过滤数据权限
     */
    private List<Long> userIdList;

    /**
     * 脚本类型
     */
    private Integer scriptType;

}
