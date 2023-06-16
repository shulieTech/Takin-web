package com.pamirs.takin.entity.domain.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PressureTestTimeDTO implements Serializable {

    public Date startTime;

    public Date endTime;

}
