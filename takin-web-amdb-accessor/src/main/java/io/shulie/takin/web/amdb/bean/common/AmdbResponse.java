package io.shulie.takin.web.amdb.bean.common;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class AmdbResponse<T> implements Serializable {
    private static final long serialVersionUID = 45387487319877474L;
    private ErrorInfo error;
    private List<T> data;
    private Long total;
    private Boolean success;
    private Boolean notSuccess;
}
