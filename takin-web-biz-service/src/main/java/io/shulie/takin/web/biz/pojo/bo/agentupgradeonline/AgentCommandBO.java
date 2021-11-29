package io.shulie.takin.web.biz.pojo.bo.agentupgradeonline;

import java.util.UUID;

import com.pamirs.takin.common.util.MD5Util;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description agent命令业务数据
 * @Author ocean_wll
 * @Date 2021/11/17 10:29 上午
 */
public class AgentCommandBO {

    /**
     * 任务的uuid
     */
    private String uuid;

    /**
     * 指令id
     */
    private Long id;

    /**
     * 指令对应的参数
     */
    private String extrasString;

    public AgentCommandBO() {
    }

    public AgentCommandBO(Long id, String extras) {
        this.id = id;
        this.extrasString = extras;
    }

    public String getUuid() {
        if (StringUtils.isNotBlank(extrasString)) {
            return MD5Util.getMD5(extrasString);
        }
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExtrasString() {
        return extrasString;
    }

    public void setExtrasString(String extrasString) {
        this.extrasString = extrasString;
    }
}
