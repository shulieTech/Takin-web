package io.shulie.takin.web.biz.convert.performace;

import java.util.List;

import com.pamirs.takin.common.util.http.DateUtil;
import io.shulie.takin.web.biz.pojo.input.PerformanceBaseDataCreateInput;
import io.shulie.takin.web.common.vo.perfomanceanaly.PerformanceThreadDataVO;
import io.shulie.takin.web.data.param.perfomanceanaly.PerformanceBaseDataParam;
import io.shulie.takin.web.data.param.perfomanceanaly.PerformanceThreadDataParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author qianshui
 * @date 2020/11/10 上午10:57
 */
@Mapper(imports = DateUtil.class)
public interface PerformanceBaseInputConvert {

    PerformanceBaseInputConvert INSTANCE = Mappers.getMapper(PerformanceBaseInputConvert.class);

    PerformanceThreadDataParam inputToParam(PerformanceThreadDataVO source);

    List<PerformanceThreadDataParam> inputToParamList(List<PerformanceThreadDataVO> sources);

    PerformanceBaseDataParam inputToParam(PerformanceBaseDataCreateInput source);
}
