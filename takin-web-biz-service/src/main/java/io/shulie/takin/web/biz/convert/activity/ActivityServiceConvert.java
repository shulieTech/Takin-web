package io.shulie.takin.web.biz.convert.activity;

import io.shulie.takin.web.biz.pojo.response.activity.ActivityListResponse;
import io.shulie.takin.web.data.result.activity.ActivityListResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author zhaoyong
 */
@Mapper
public interface ActivityServiceConvert {
    ActivityServiceConvert INSTANCE = Mappers.getMapper(ActivityServiceConvert.class);


    List<ActivityListResponse> ofActivityList(List<ActivityListResult> activityList);
}
