package io.shulie.takin.web.amdb.bean.result.application;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/10/19 10:17 下午
 */
@Data
public class ApplicationErrorDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private List<String> agentIds;
    private String description;
    private String time;

}
