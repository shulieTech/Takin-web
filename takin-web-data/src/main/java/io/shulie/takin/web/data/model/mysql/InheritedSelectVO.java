package io.shulie.takin.web.data.model.mysql;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.shulie.takin.common.beans.component.SelectVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InheritedSelectVO extends SelectVO {

    @JsonIgnore
    private Integer order;

    private List<SelectVO> children;

    public InheritedSelectVO(String label, String value) {
        super(label, value);
    }

    public InheritedSelectVO(String label, String value, List<SelectVO> children) {
        this(label, value);
        this.children = children;
    }

    public InheritedSelectVO(String label, String value, List<SelectVO> children, Integer order) {
        this(label, value, children);
        this.order = order;
    }

    public Integer getOrder() {
        if (order == null) {
            return Integer.MAX_VALUE;
        }
        return order;
    }
}
