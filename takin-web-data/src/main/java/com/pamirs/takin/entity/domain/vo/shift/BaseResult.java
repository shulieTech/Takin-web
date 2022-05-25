package com.pamirs.takin.entity.domain.vo.shift;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BaseResult<T> {
    private boolean result = true;
    private int code = 0;
    private String message = "OK";
    private T data;

    public void fail(String message) {
        this.setResult(false);
        this.setCode(500);
        this.setMessage(message);
    }
}
