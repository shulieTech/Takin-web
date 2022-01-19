package com.pamirs.takin.entity.domain.vo.dsmanage;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

import java.util.Map;
import java.util.List;

/**
 * 数据库配置
 *
 * @author fanxx
 * @date 2020/3/13 上午11:12
 */
@XmlRootElement
@Data
public class Configurations {

    /**
     * 数据调停者
     */
    private DatasourceMediator datasourceMediator;

    /**
     * 数据源
     */
    private List<DataSource> dataSources;

}
