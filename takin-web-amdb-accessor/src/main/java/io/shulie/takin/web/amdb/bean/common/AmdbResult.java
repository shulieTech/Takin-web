package io.shulie.takin.web.amdb.bean.common;

import java.io.Serializable;

import lombok.Data;

@Data
public class AmdbResult<T> implements Serializable {
    private static final long serialVersionUID = 45387487319877474L;
    private ErrorInfo error;
    private T data;
    private Long total;
    private Boolean success;
    private Boolean notSuccess;
}
