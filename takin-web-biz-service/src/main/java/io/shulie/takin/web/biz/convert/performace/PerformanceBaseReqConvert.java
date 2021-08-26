package io.shulie.takin.web.biz.convert.performace;

import io.shulie.takin.web.biz.pojo.input.PerformanceBaseDataCreateInput;
import io.shulie.takin.web.biz.pojo.perfomanceanaly.PerformanceBaseDataReq;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author qianshui
 * @date 2020/11/10 上午10:57
 */
@Mapper
public interface PerformanceBaseReqConvert {

    PerformanceBaseReqConvert INSTANCE = Mappers.getMapper(PerformanceBaseReqConvert.class);

    PerformanceBaseDataCreateInput reqToInput(PerformanceBaseDataReq source);
}
