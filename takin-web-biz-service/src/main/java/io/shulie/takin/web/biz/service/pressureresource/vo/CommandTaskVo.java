package io.shulie.takin.web.biz.service.pressureresource.vo;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/10/18 5:44 PM
 */
public class CommandTaskVo {
    /**
     * 模块
     */
    private String module;

    /**
     * resourceId
     */
    private Long resourceId;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public String toString() {
        return "CommandTaskVo{" +
                "module='" + module + '\'' +
                ", resourceId=" + resourceId +
                '}';
    }
}
