package io.shulie.takin.web.data.dao.dictionary;

import java.util.List;

import com.pamirs.takin.entity.domain.vo.TDictionaryVo;

/**
 * @author 无涯
 * @date 2021/6/3 8:32 下午
 */
public interface DictionaryDataDAO {
    /**
     * 根据key获取
     * @param code
     * @return
     */
    List<TDictionaryVo> getDictByCode(String code);
}
