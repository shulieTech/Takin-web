package io.shulie.takin.web.ext.entity;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author by: hezhongqi
 * @date 2021/8/2 21:07
 */
@Data
public class UserExt {
    private Long id;

    private String name;

    private String nick;

    private String key;

    private String salt;

    private String password;

    private Integer status;

    /**
     * 用户类型 0:系统管理员 1:其他
     */
    private Integer userType;

    private Integer model;

    /**
     * 角色 0:管理员，1:体验用户 2:正式用户（作废）
     */
    private Integer role;

    private Integer isDelete;

    private Date gmtCreate;

    private Date gmtUpdate;

    private String version;

    @JsonProperty("xToken")
    private String xToken;

    /**
     * 用户部门信息
     */
    private List<DeptQueryExt> deptList;

    private List<String> permissionUrl;

    private Map<String, Boolean> permissionMenu;

    private Map<String, Boolean> permissionAction;

    private Map<String, List<Integer>> permissionData;

    /**
     * 账号体系下：每个公司有一个管理账号，我们称为租户管理员账号，或者主账号
     * 其他账号，我们称为子账号
     * 子账号和主账号，根据管理员对应的key，将数据进行隔离
     */
    private Long customerId;
    private String customerKey;
}
