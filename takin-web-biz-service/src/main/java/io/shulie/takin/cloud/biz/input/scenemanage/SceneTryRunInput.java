package io.shulie.takin.cloud.biz.input.scenemanage;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author -
 */
@Data
@NoArgsConstructor
public class SceneTryRunInput {
    /**
     * 循环次数
     */
    private Integer loopsNum;

    /**
     * 并发数
     */
    private Integer concurrencyNum;

    public SceneTryRunInput(Integer loopsNum, Integer concurrencyNum) {
        this.loopsNum = loopsNum;
        this.concurrencyNum = concurrencyNum;
    }

    public Integer getLoopsNum() {
        return loopsNum;
    }

    public SceneTryRunInput setLoopsNum(Integer loopsNum) {
        this.loopsNum = loopsNum;
        return this;
    }

    public Integer getConcurrencyNum() {
        return concurrencyNum;
    }

    public void setConcurrencyNum(Integer concurrencyNum) {
        this.concurrencyNum = concurrencyNum;
    }
}
