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

    /**
     * 根据key获取  根据租户获取数据
     * @param code
     * @param tenantId
     * @param envCode
     * @return
     */
    List<TDictionaryVo> getDictByCode(String code,Long tenantId,String envCode);
}
