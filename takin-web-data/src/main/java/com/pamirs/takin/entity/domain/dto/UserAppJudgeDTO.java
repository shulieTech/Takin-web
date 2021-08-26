package com.pamirs.takin.entity.domain.dto;

import java.util.List;

import com.pamirs.takin.entity.domain.vo.AppStatusVO;
import lombok.Data;

/**
 * @author mubai<chengjiacai @ shulie.io>
 * @date 2020-04-07 21:17
 */

@Data
public class UserAppJudgeDTO {
    private Boolean success;

    private String info;

    private List<AppStatusVO> list;

    public UserAppJudgeDTO(Boolean success, String info, List<AppStatusVO> list) {
        this.success = success;
        this.info = info;
        this.list = list;
    }

    public UserAppJudgeDTO() {
    }
}
