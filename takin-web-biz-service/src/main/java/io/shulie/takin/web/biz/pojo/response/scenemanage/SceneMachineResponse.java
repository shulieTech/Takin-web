package io.shulie.takin.web.biz.pojo.response.scenemanage;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SceneMachineResponse implements Serializable {

    private String lastStartMachineId;
    private Integer lastStartMachineType;
    private List<SceneMachineCluster> list;

    @Data
    @Builder
    public static class SceneMachineCluster {
        private String id;
        private String name;
        private String type;
        private Long cpu;
        private Long memory;
        private boolean disabled;
    }
}
