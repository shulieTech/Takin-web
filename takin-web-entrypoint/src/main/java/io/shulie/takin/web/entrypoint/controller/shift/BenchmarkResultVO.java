package io.shulie.takin.web.entrypoint.controller.shift;

import lombok.Data;

import java.util.List;

@Data
    public class BenchmarkResultVO {
        private String title;
        private String lastModified;
        private String description;
        private List systems;
        private List results;
    }