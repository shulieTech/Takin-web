package io.shulie.takin.cloud.biz.cloudserver;

import java.util.List;

import cn.hutool.core.date.DateUtil;
import com.pamirs.takin.cloud.entity.domain.bo.scenemanage.WarnBO;
import com.pamirs.takin.cloud.entity.domain.dto.report.BusinessActivityDTO;
import com.pamirs.takin.cloud.entity.domain.dto.report.CloudReportDTO;
import com.pamirs.takin.cloud.entity.domain.entity.report.Report;
import com.pamirs.takin.cloud.entity.domain.entity.report.ReportBusinessActivityDetail;
import com.pamirs.takin.cloud.entity.domain.entity.scene.manage.WarnDetail;
import io.shulie.takin.cloud.biz.output.report.ReportDetailOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.WarnDetailOutput;
import io.shulie.takin.cloud.common.bean.scenemanage.WarnBean;
import io.shulie.takin.cloud.common.utils.TestTimeUtil;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.data.result.scenemanage.WarnDetailResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author 莫问
 * @date 2020-04-17
 */

@Mapper(imports = {TestTimeUtil.class, DateUtil.class})
public interface ReportConverter {

    ReportConverter INSTANCE = Mappers.getMapper(ReportConverter.class);

    /**
     * Report Converter ReportDetail
     *
     * @param report -
     * @return -
     */
    @Mapping(target = "startTime", expression = "java(DateUtil.formatDateTime(report.getStartTime()))")
    ReportDetailOutput ofReportDetail(ReportResult report);

    /**
     * Report Converter ReportDTO
     *
     * @param report -
     * @return -
     */
    List<CloudReportDTO> ofReport(List<Report> report);

    /**
     * 转换 {@link com.pamirs.takin.cloud.entity.domain.entity.report.Report } -> {@link com.pamirs.takin.cloud.entity.domain.dto.report.CloudReportDTO}
     *
     * @param report {@link com.pamirs.takin.cloud.entity.domain.entity.report.Report }
     * @return {@link com.pamirs.takin.cloud.entity.domain.dto.report.CloudReportDTO}
     */
    @Mappings({
        @Mapping(target = "startTime",
            expression = "java(DateUtil.formatDateTime(report.getStartTime()))"),
        @Mapping(target = "totalTime",
            expression = "java(TestTimeUtil.format(report.getStartTime(), report.getEndTime()))")
    })
    CloudReportDTO ofReport(Report report);

    /**
     * WarnBO Converter WarnBean
     *
     * @param warn -
     * @return -
     */
    List<WarnBean> ofWarn(List<WarnBO> warn);

    /**
     * 转换 {@link WarnDetail} -> {@link WarnDetailOutput}
     *
     * @param warnDetail {@link WarnDetail}
     * @return {@link WarnDetailOutput}
     */
    @Mappings(
        value = {
            @Mapping(target = "reportId", source = "ptId"),
            @Mapping(target = "content", source = "warnContent"),
            @Mapping(target = "warnTime",
                expression = "java(DateUtil.formatDateTime(warnDetail.getWarnTime()))")
        }
    )
    WarnDetailOutput ofWarn(WarnDetail warnDetail);

    /**
     * WarnDetail Converter WarnDetailResult
     *
     * @param warnDetail -
     * @return -
     */
    List<WarnDetailOutput> ofWarnDetail(List<WarnDetail> warnDetail);

    /**
     * 转换{@link WarnBO} -> {@link WarnBean}
     *
     * @param warn {@link WarnBO}
     * @return {@link WarnBean}
     */
    @Mapping(target = "lastWarnTime",
        expression = "java(DateUtil.formatDateTime(warn.getLastWarnTime()))")
    WarnBean ofWarn(WarnBO warn);

    /**
     * 转换 {@link com.pamirs.takin.cloud.entity.domain.entity.scene.manage.WarnDetail} -> {@link WarnDetailResult}
     *
     * @param warnDetail {@link com.pamirs.takin.cloud.entity.domain.entity.scene.manage.WarnDetail}
     * @return {@link WarnDetailResult}
     */
    @Mappings(
        value = {
            @Mapping(target = "reportId", source = "ptId"),
            @Mapping(target = "content", source = "warnContent"),
            @Mapping(target = "warnTime",
                expression = "java(DateUtil.formatDateTime(warnDetail.getWarnTime()))")
        }
    )
    WarnDetailResult ofWarnDetail(WarnDetail warnDetail);

    /**
     * ReportBusinessActivityDetail Converter BusinessActivityDTO
     *
     * @param data -
     * @return -
     */
    List<BusinessActivityDTO> ofBusinessActivity(List<ReportBusinessActivityDetail> data);
}
