package io.shulie.takin.web.data.param.baseserver;

import java.util.List;

import lombok.Data;

/**
 * @author mubai
 * @date 2020-10-26 17:10
 */

@Data
public class ProcessBaseRiskParam {

   private List<String> appNames;

   private long startTime ;

   private long endTime;

   private Long reportId ;
}
