package io.shulie.takin.web.data.mapper.mysql;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author qianshui
 * @date 2020/11/4 下午3:31
 */
public interface MyBatisPlusMapper<T> extends BaseMapper<T> {

    int insertBatchSomeColumn(List<T> entityList);

}
