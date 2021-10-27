package io.shulie.takin.web.biz.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.pagehelper.PageHelper;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.pamirs.takin.common.constant.TakinConstantEnum;
import com.pamirs.takin.common.exception.TakinModuleException;
import com.pamirs.takin.common.util.PageInfo;
import com.pamirs.takin.entity.dao.dict.TDictionaryDataMapper;
import com.pamirs.takin.entity.dao.dict.TDictionaryTypeMapper;
import com.pamirs.takin.entity.domain.entity.TDictionaryData;
import com.pamirs.takin.entity.domain.entity.TDictionaryType;
import com.pamirs.takin.entity.domain.vo.TDictionaryVo;
import io.shulie.takin.web.biz.common.CommonService;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.dictionary.DictionaryDataDAO;
import io.shulie.takin.web.data.param.dictionary.DictionaryParam;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.shulie.takin.web.ext.util.WebPluginUtils.EnvCodeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 说明：数据字典服务
 *
 * @author shulie
 * @version 1.0
 * @create 2018/10/31 0031 16:46
 */
@Service
public class DictionaryMntService extends CommonService {

    @Autowired
    private TDictionaryDataMapper tDictionaryDataMapper;

    /**
     * 说明: 保存数据字典
     *
     * @param tDictionaryVo
     * @return void
     * @author shulie
     * @date 2018/10/31 0031 17:06
     */
    @Transactional(value = "takinTransactionManager", rollbackFor = Exception.class)
    public void saveDictionary(TDictionaryVo tDictionaryVo) throws TakinModuleException {
        String userName = null;

        //判断类型名称是否已经存在
        TDictionaryType tDictionaryType = tDictionaryTypeMapper.selectDictionaryByTypeAlias(
            tDictionaryVo.getTypeAlias());

        if (tDictionaryType == null) {
            //1，保存到t_dictionary_type表中
            tDictionaryType = new TDictionaryType();
            tDictionaryType.setCreateUserCode(userName);
            tDictionaryType.setModifyUserCode(userName);
            tDictionaryType.setTypeAlias(tDictionaryVo.getTypeAlias());
            tDictionaryType.setTypeName(tDictionaryVo.getTypeName());
            tDictionaryType.setActive(tDictionaryVo.getTypeActive());
            tDictionaryTypeMapper.insert(tDictionaryType);
        }
        // 2, 保存到t_dictionary_data表中
        TDictionaryData tDictionaryData = new TDictionaryData();
        tDictionaryData.setDictType(tDictionaryType.getId());
        tDictionaryData.setValueCode(tDictionaryVo.getValueCode());
        tDictionaryData.setValueName(tDictionaryVo.getValueName());
        tDictionaryData.setValueOrder(Integer.valueOf(tDictionaryVo.getValueOrder()));
        tDictionaryData.setLanguage(tDictionaryVo.getLanguage());
        tDictionaryData.setActive(tDictionaryVo.getValueActive());
        tDictionaryData.setCreateUserCode(userName);
        tDictionaryData.setModifyUserCode(userName);
        tDictionaryDataMapper.insert(tDictionaryData);

    }

    /**
     * 1，更新数据字典类型中的类型名称、类型别名、是否可维护
     * 2，更新数据字典值中的值顺序、值名称、值代码、是否激活
     *
     * @param tDictionaryVo
     */
    public void updateDictionary(TDictionaryVo tDictionaryVo) {
        tDictionaryDataMapper.updateDictionary(tDictionaryVo);
    }

    /**
     * 查询数据字典列表
     *
     * @param paramMap
     * @return
     */
    public PageInfo<TDictionaryVo> queryDictionaryList(Map<String, Object> paramMap) {
        paramMap.put("tenant_id", WebPluginUtils.traceTenantId());
        paramMap.put("env_code", WebPluginUtils.traceEnvCode());
        PageHelper.startPage(PageInfo.getPageNum(paramMap), PageInfo.getPageSize(paramMap));
        List<TDictionaryVo> dictionaryVoList = tDictionaryDataMapper.queryDictionaryList(paramMap);

        return new PageInfo<>(dictionaryVoList);
    }

    /**
     * 查询数据字典详情
     *
     * @param tDictionaryId
     * @return
     */
    public TDictionaryVo queryDictionaryDetail(String tDictionaryId) {
        DictionaryParam param = new DictionaryParam();
        param.setTDictionaryId(tDictionaryId);
        return tDictionaryDataMapper.queryDictionaryDetail(param);
    }

    /**
     * 删除数据字典值
     * 1, 删除后，如果数据字典类型中还有值
     * 2，删除后，如果数据字典类型中没有值了，也需要把数据字典类型删除
     *
     * @param tDictionaryIdList
     */
    public void deleteDictionary(String tDictionaryIdList) {

        tDictionaryDataMapper.deleteDictionary(Splitter.on(",").trimResults().omitEmptyStrings()
            .splitToList(tDictionaryIdList));
        //判断字典类型有无对应的字典值，没有则删除。
        //        DELETE t FROM t_dictionary_type t LEFT JOIN t_dictionary_data d ON t.ID = d.DICT_TYPE WHERE d.ID IS
        //        NULL
        tDictionaryDataMapper.deleteEmptyDictType();
    }

    /**
     * 查询数据字典KEY_VALUE值
     *
     * @param dictCode
     * @return
     */
    public Map<String, Object> queryDictionaryKeyValue(String dictCode) {
        List<Map<String, Object>> dictMaps = tDicDao.queryDicList(StringUtils.upperCase(dictCode));
        if (dictMaps.isEmpty()) {
            return Maps.newHashMap();
        }
        Map<String, Object> resultMap = Maps.newTreeMap(Comparator.comparingInt(Integer::parseInt));
        dictMaps.forEach(map -> resultMap.put(MapUtils.getString(map, TakinConstantEnum.VALUE_ORDER.toString()),
            MapUtils.getString(map, TakinConstantEnum.VALUE_NAME.toString())));
        return resultMap;
    }

    @Transactional(value = "takinTransactionManager", rollbackFor = Exception.class)
    public void init(Long tenantId) {
        DictionaryParam param = new DictionaryParam();
        param.setTenantId(WebPluginUtils.DEFAULT_TENANT_ID);
        List<TDictionaryData> dataList = tDictionaryDataMapper.queryList(param);
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        //测试环境-默认租户下的 字典内容
        List<TDictionaryData> testDataList = dataList.stream().filter(
            t -> EnvCodeEnum.TEST.getEnvCode().equals(t.getEnvCode())).collect(Collectors.toList());
        //生产环境-默认租户下的 字典内容
        List<TDictionaryData> prodDataList = dataList.stream().filter(
            t -> EnvCodeEnum.PROD.getEnvCode().equals(t.getEnvCode())).collect(Collectors.toList());
        try {
            if (CollectionUtils.isNotEmpty(testDataList)) {
                testDataList.forEach(t -> t.setTenantId(tenantId));
                tDictionaryDataMapper.batchInsert(testDataList);
            }
            if (CollectionUtils.isNotEmpty(prodDataList)) {
                prodDataList.forEach(t -> t.setTenantId(tenantId));
                tDictionaryDataMapper.batchInsert(prodDataList);
            }
        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "数据字典初始化错误", e);
        }
    }
}
