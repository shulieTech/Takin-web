package io.shulie.takin.web.data.param.dictionary;

import io.shulie.takin.web.data.common.BaseTenantBean;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author caijianying
 */
@Getter
@Setter
@NoArgsConstructor
public class DictionaryParam extends BaseTenantBean {
    /**
     * 字典别名
     */
    private String typeAlias;

    /**
     * 字典值ID
     */
    private String tDictionaryId;

}
