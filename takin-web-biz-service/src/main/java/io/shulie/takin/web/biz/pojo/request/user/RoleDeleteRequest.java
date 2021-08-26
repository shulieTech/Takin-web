package io.shulie.takin.web.biz.pojo.request.user;

import java.util.List;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/2 4:15 下午
 */
@Data
public class RoleDeleteRequest {

    private List<Long> ids;

}
