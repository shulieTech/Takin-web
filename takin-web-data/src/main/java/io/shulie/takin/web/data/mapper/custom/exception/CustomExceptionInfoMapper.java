package io.shulie.takin.web.data.mapper.custom.exception;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.data.mapper.mysql.ExceptionInfoMapper;
import io.shulie.takin.web.data.model.mysql.ExceptionInfoEntity;
import org.springframework.stereotype.Service;

/**
 * @author 无涯
 * @date 2021/1/14 10:13 上午
 */
@Service
public class CustomExceptionInfoMapper extends ServiceImpl<ExceptionInfoMapper, ExceptionInfoEntity> {
}
