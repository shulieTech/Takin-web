package io.shulie.takin.adapter.api.model.response.watchman;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WatchmanNode {

    private Long cpu;
    private Long memory;

    private String type;
    private String name;
    private String nfsServer;
    private String nfsDir;
    private Long nfsUsableSpace;
    private Long nfsTotalSpace;

    public WatchmanNode(String name) {
        this.name = name;
    }
}
