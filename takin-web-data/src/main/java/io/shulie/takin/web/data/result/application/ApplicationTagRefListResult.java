package io.shulie.takin.web.data.result.application;

import lombok.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.web.data.model.mysql.ApplicationTagRefEntity;

/**
 * 应用标签表(ApplicationTagRef)列表出参类
 *
 * @author ocean_wll
 * @date 2021-11-09 20:48:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ApplicationTagRefListResult extends ApplicationTagRefEntity {

}
