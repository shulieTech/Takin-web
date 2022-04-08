package io.shulie.takin.web.data.dao.application;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import io.shulie.takin.web.data.model.mysql.InheritedSelectVO;
import io.shulie.takin.web.data.model.mysql.MiddlewareTypeEntity;

public interface MiddlewareTypeDAO extends IService<MiddlewareTypeEntity> {

    List<InheritedSelectVO> selectList();
}
