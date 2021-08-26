package io.shulie.takin.web.common.vo;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import io.shulie.takin.web.common.domain.WebRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author qianshui
 * @date 2020/5/11 下午9:11
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileWrapperVO extends WebRequest implements Serializable {

    private static final long serialVersionUID = 8484664446939598273L;

    private List<File> file;
}
