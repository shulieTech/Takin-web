package io.shulie.takin.web.biz.pojo.response.user;

import com.opencsv.bean.CsvBindByPosition;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/3/9 11:55 上午
 */
@Data
public class UserCsvBean {

    @ApiModelProperty("")
    @CsvBindByPosition(position = 0)
    private String deptNames;

    @CsvBindByPosition(position = 1)
    private String username;

    @CsvBindByPosition(position = 2)
    private String password;

    @CsvBindByPosition(position = 3)
    private String errorMsg;
}
