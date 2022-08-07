package io.shulie.takin.web.biz.engine.extractor;

import lombok.Data;

@Data
public class ExtractorContext {

    private Long id;    // 操作数据Id
    private Integer type;  // 操作数据类型
    private ExtractorType extractorType;

    public ExtractorContext(Long id, Integer type) {
        this.id = id;
        this.type = type;
    }

    public ExtractorType getExtractorType() {
        return ExtractorType.of(type);
    }
}
