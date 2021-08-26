package io.shulie.takin.web.entrypoint.controller.confcenter;

import com.pamirs.takin.common.ResponseError;
import com.pamirs.takin.common.ResponseOk;
import com.pamirs.takin.common.constant.TakinErrorEnum;
import com.pamirs.takin.common.exception.TakinModuleException;
import com.pamirs.takin.entity.domain.entity.TPressureTimeRecord;
import io.shulie.takin.web.biz.service.PressureTimeRecordService;
import io.shulie.takin.web.common.constant.APIUrls;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shulie
 * @description
 * @create 2019-04-12 09:10:28
 */
@Api(tags = "PressureTimeRecord")
@RestController
@RequestMapping(APIUrls.TAKIN_API_URL)
public class PressureTimeRecordController {

    Logger logger = LoggerFactory.getLogger(PressureTimeRecordController.class);

    @Autowired
    private PressureTimeRecordService pressureTimeRecordService;

    /**
     * API.01.11.001
     * 说明: 保存开始压测时间
     *
     * @return org.springframework.http.ResponseEntity<java.lang.Object>
     * @author shulie
     * @create 2019/4/12 9:22
     */
    @RequestMapping(value = APIUrls.TAKIN_CONFCENTER_ADD_PRESSURETIME, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> savePressureTimeRecord(@RequestBody TPressureTimeRecord pressureTimeRecord) {
        try {
            pressureTimeRecordService.savePressureTimeRecord(pressureTimeRecord);
            return ResponseOk.create("保存成功");
        } catch (TakinModuleException e) {
            logger.error("PressureTimeRecordController.savePressureTimeRecord 保存压测开始时间异常{}", e);
            return ResponseError.create(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            logger.error("PressureTimeRecordController.savePressureTimeRecord 保存压测开始时间异常{}", e);
            return ResponseError.create(TakinErrorEnum.PRESSURE_TIME_RECORD_SAVE_EXCEPTION.getErrorCode(),
                TakinErrorEnum.PRESSURE_TIME_RECORD_SAVE_EXCEPTION.getErrorMessage());
        }
    }

    /**
     * API.01.11.002
     * 说明: 更新结束压测时间
     *
     * @return org.springframework.http.ResponseEntity<java.lang.Object>
     * @author shulie
     * @create 2019/4/12 9:22
     */
    @RequestMapping(value = APIUrls.TAKIN_CONFCENTER_UPDATE_PRESSURETIME,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> updatePressureTimeRecord(@RequestBody TPressureTimeRecord pressureTimeRecord) {
        try {
            pressureTimeRecordService.updatePressureTimeRecord(pressureTimeRecord);
            return ResponseOk.create("更新成功");
        } catch (TakinModuleException e) {
            logger.error("PressureTimeRecordController.updatePressureTimeRecord 更新压测结束时间异常{}", e);
            return ResponseError.create(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            logger.error("PressureTimeRecordController.updatePressureTimeRecord 更新压测结束时间异常{}", e);
            return ResponseError.create(TakinErrorEnum.PRESSURE_TIME_RECORD_UPDATE_EXCEPTION.getErrorCode(),
                TakinErrorEnum.PRESSURE_TIME_RECORD_UPDATE_EXCEPTION.getErrorMessage());
        }
    }

    /**
     * API.01.11.003
     * 说明: 查询最新的压测时间记录
     *
     * @return org.springframework.http.ResponseEntity<java.lang.Object>
     * @author shulie
     * @create 2019/4/12 9:22
     */
    @RequestMapping(value = APIUrls.TAKIN_CONFCENTER_QUERY_LATEST_PRESSURETIME,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> queryLatestPressureTime() {
        try {
            TPressureTimeRecord pressureTimeRecord = pressureTimeRecordService.queryLatestPressureTime();
            return ResponseOk.create(pressureTimeRecord);
        } catch (TakinModuleException e) {
            logger.error("PressureTimeRecordController.queryLatestPressureTime 查询最新的压测时间记录异常{}", e);
            return ResponseError.create(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            logger.error("PressureTimeRecordController.queryLatestPressureTime 查询最新的压测时间记录异常{}", e);
            return ResponseError.create(TakinErrorEnum.PRESSURE_TIME_RECORD_QUERY_LATEST_EXCEPTION.getErrorCode(),
                TakinErrorEnum.PRESSURE_TIME_RECORD_QUERY_LATEST_EXCEPTION.getErrorMessage());
        }
    }

}
