package io.shulie.takin.cloud.biz.input.scenemanage;

/**
 * @author fanxx
 * @date 2021/4/12 4:48 下午
 */
public class SceneInspectInput {
    /**
     * 循环次数
     */
    private Integer loopsNum;

    /**
     * 定时器周期
     */
    private Long fixedTimer;

    public Integer getLoopsNum() {
        return loopsNum;
    }

    public SceneInspectInput setLoopsNum(Integer loopsNum) {
        this.loopsNum = loopsNum;
        return this;
    }

    public Long getFixedTimer() {
        return fixedTimer;
    }

    public SceneInspectInput setFixedTimer(Long fixedTimer) {
        this.fixedTimer = fixedTimer;
        return this;
    }
}
