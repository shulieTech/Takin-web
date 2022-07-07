package io.shulie.takin.web.biz.pojo.request.placeholdermanage;


import lombok.Data;

@Data
public class PlaceholderManageRequest {

    private Long Id;


    private String key;


    private String value;


    private String remark;
}
