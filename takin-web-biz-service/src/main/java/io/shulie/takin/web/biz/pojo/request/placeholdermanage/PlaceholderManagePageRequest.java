package io.shulie.takin.web.biz.pojo.request.placeholdermanage;


import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class PlaceholderManagePageRequest extends PagingDevice {

    private String key;


    private String value;


    private String remark;
}
