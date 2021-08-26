package com.pamirs.takin.entity.domain.vo.report;

import java.io.Serializable;

import lombok.Data;

/**
 * @author moriarty
 */
@Data
public class ScenePluginParam implements Serializable {

    private Long pluginId;

    private String pluginVersion;
}
