package io.shulie.takin.web.biz.pojo.request.pts;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Data
public class PtsDataSourceRequest implements Serializable {

    private List<PtsCsvRequest> csvs = new ArrayList<>();
}
