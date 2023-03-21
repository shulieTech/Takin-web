package io.shulie.takin.web.biz.pojo.request.pts;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class PtsCsvRequest implements Serializable {

    private String fileName;

    private Boolean ingoreFirstLine;

    private String uploadStatus;

    private String fileSize;

    private String datalines;

    private List<PtsCsvLineRequest> lineParams = new ArrayList<>();
}
