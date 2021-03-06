package io.shulie.takin.web.biz.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.collect.Lists;
import com.pamirs.takin.common.constant.Constants;
import com.pamirs.takin.common.constant.TakinErrorEnum;
import com.pamirs.takin.common.exception.TakinModuleException;
import com.pamirs.takin.common.util.DateUtils;
import com.pamirs.takin.entity.domain.entity.LinkBottleneck;
import com.pamirs.takin.entity.domain.entity.TLinkTopologyInfo;
import com.pamirs.takin.entity.domain.entity.TWList;
import com.pamirs.takin.entity.domain.vo.TLinkTopologyInfoVo;
import com.pamirs.takin.entity.domain.vo.TLinkTopologyShowVo;
import com.pamirs.takin.entity.domain.vo.TopologyLink;
import com.pamirs.takin.entity.domain.vo.TopologyNode;
import com.pamirs.takin.entity.domain.vo.bottleneck.AsyncVo;
import com.pamirs.takin.entity.domain.vo.bottleneck.BottleCountVo;
import com.pamirs.takin.entity.domain.vo.bottleneck.BottleLevelCountVo;
import com.pamirs.takin.entity.domain.vo.bottleneck.BottleNeckDetailVo;
import com.pamirs.takin.entity.domain.vo.bottleneck.BottleNeckVo;
import com.pamirs.takin.entity.domain.vo.bottleneck.RtVo;
import com.pamirs.takin.entity.domain.vo.bottleneck.StabilityVo;
import io.shulie.takin.web.biz.common.CommonService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * ??????????????? ??????
 *
 * @author 298403
 */
@Service
public class LinkTopologyInfoService extends CommonService {

    private static final Logger logger = LoggerFactory.getLogger(LinkTopologyInfoService.class);

    /**
     * ???????????????HTTP???MQ???JOB???DUBBO???4?????????
     * ???????????????1.?????????????????????   2.????????????  3.TPS/RT?????????  4.RT????????????
     * HTTP??????????????? 1???3???4
     * MQ???JOB??????????????? 1???2???3
     * DUBBO??????????????? 1???3
     */
    private static final String[] BOTTLE_TYPE_HTTP = {"1", "3", "4"};
    private static final String[] BOTTLE_TYPE_MQ = {"1", "2", "3"};
    private static final String[] BOTTLE_TYPE_JOB = {"1", "2", "3"};
    private static final String[] BOTTLE_TYPE_DEFAULT = {"1", "3"};

    /**
     * ????????????
     */
    private static final int PRE_FIVE_MINUTE = -5;
    private static final int PRE_TWENTY_FOUR_HOUR = -24;

    public static void checkFile(MultipartFile file) throws TakinModuleException {
        //????????????????????????
        if (null == file) {
            logger.warn("??????????????????");
            throw new TakinModuleException(TakinErrorEnum.TRANSPARENTFLOW_LINKTOPOLOGY_EXCEL_IS_EMTPT_EXCEPTION);
        }
        //???????????????
        String fileName = file.getOriginalFilename();
        //?????????????????????excel??????
        if (!fileName.endsWith("xls") && !fileName.endsWith("xlsx")) {
            logger.error(fileName + "??????excel??????");
            throw new TakinModuleException(TakinErrorEnum.TRANSPARENTFLOW_LINKTOPOLOGY_EXCEL_SUFFIX_ERROR_EXCEPTION);
        }
    }

    public static Workbook getWorkBook(MultipartFile file) {
        //???????????????
        String fileName = file.getOriginalFilename();
        //??????Workbook??????????????????????????????excel
        Workbook workbook = null;
        try {
            //??????excel?????????io???
            InputStream is = file.getInputStream();
            //???????????????????????????(xls???xlsx)???????????????Workbook???????????????
            if (fileName.endsWith("xls")) {
                //2003
                workbook = new HSSFWorkbook(is);
            } else if (fileName.endsWith("xlsx")) {
                //2007
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
        return workbook;
    }

    /**
     * ??????excel ???????????? ??????
     *
     * @param excel
     * @throws TakinModuleException
     */
    public void importExcelData(MultipartFile excel) throws TakinModuleException {
        checkFile(excel);
        //??????Workbook???????????????
        Workbook workbook = getWorkBook(excel);
        //?????????????????????????????????????????????????????????????????????????????????????????????
        List<TLinkTopologyInfo> topologyInfoList = new ArrayList<>();
        if (workbook != null) {
            convertToTopology(workbook, topologyInfoList);
        }
        if (CollectionUtils.isNotEmpty(topologyInfoList)) {
            tLinkTopologyInfoDao.deleteAllData();
            tLinkTopologyInfoDao.insertList(topologyInfoList);
        }
    }

    private void convertToTopology(Workbook workbook, List<TLinkTopologyInfo> topologyInfoList) {
        //????????????sheet?????????
        Sheet sheet = workbook.getSheetAt(0);
        //????????????sheet????????????
        int firstRowNum = sheet.getFirstRowNum();
        //????????????sheet????????????
        int lastRowNum = sheet.getLastRowNum();
        //????????????????????????????????? ??????????????????
        for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
            TLinkTopologyInfo topologyInfo = new TLinkTopologyInfo();
            topologyInfo.setTltiId(snowflake.next());
            //???????????????
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }
            //???????????????????????????
            int firstCellNum = row.getFirstCellNum();
            //????????????????????????
            int lastCellNum = row.getPhysicalNumberOfCells();
            //???????????????
            for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
                Cell cell = row.getCell(cellNum);
                cell.setCellType(CellType.STRING);
                switch (cellNum) {
                    case 0:
                        //????????? ??????id
                        if (StringUtils.isNotEmpty(cell.getStringCellValue())) {
                            topologyInfo.setLinkId(Long.parseLong(cell.getStringCellValue()));
                        }
                        ;
                        break;
                    case 1:
                        topologyInfo.setLinkName(cell.getStringCellValue());
                        break;
                    case 2:
                        topologyInfo.setEntranceType(cell.getStringCellValue());
                        break;
                    case 3:
                        topologyInfo.setLinkEntrance(cell.getStringCellValue());
                        break;
                    case 4:
                        topologyInfo.setNameServer(cell.getStringCellValue());
                        break;
                    case 5:
                        topologyInfo.setLinkType(cell.getStringCellValue());
                        break;
                    case 6:
                        topologyInfo.setLinkGroup(cell.getStringCellValue());
                        break;
                    case 7:
                        topologyInfo.setFromLinkIds(cell.getStringCellValue());
                        break;
                    case 8:
                        topologyInfo.setToLinkIds(cell.getStringCellValue());
                        break;
                    case 9:
                        topologyInfo.setShowFromLinkId(cell.getStringCellValue());
                        break;
                    case 10:
                        topologyInfo.setShowToLinkId(cell.getStringCellValue());
                        break;
                    case 11:
                        topologyInfo.setSecondLinkId(cell.getStringCellValue());
                        break;
                    case 12:
                        topologyInfo.setApplicationName(cell.getStringCellValue());
                        break;
                    case 13:
                        topologyInfo.setVolumeCalcStatus(cell.getStringCellValue());
                        break;
                    default:
                        break;
                }
            }
            topologyInfoList.add(topologyInfo);
        }
    }

    /**
     * ???????????? ?????? ????????????????????????
     *
     * @param linkGroup
     * @param secondLinkId
     * @return
     * @throws TakinModuleException
     */
    public TLinkTopologyShowVo queryLinkTopologyByLinkGroup(String linkGroup, String secondLinkId)
        throws TakinModuleException {
        if (StringUtils.isEmpty(linkGroup)) {
            throw new TakinModuleException(TakinErrorEnum.TRANSPARENTFLOW_LINKTOPOLOGY_LINK_GROUP_EMPTY_EXCEPTION);
        }
        TLinkTopologyShowVo vo = new TLinkTopologyShowVo();
        List<TLinkTopologyInfoVo> topologyInfoList = tLinkTopologyInfoDao.queryLinkTopologyByLinkGroup(linkGroup,
            secondLinkId);
        if (CollectionUtils.isEmpty(topologyInfoList)) {
            return vo;
        }
        Set<TopologyLink> linkList = new HashSet<>();
        List<TopologyNode> nodeList = new ArrayList<>();
        //?????? ????????????
        for (TLinkTopologyInfoVo info : topologyInfoList) {
            TopologyNode node = new TopologyNode();
            BeanUtils.copyProperties(info, node);
            String linkId = String.valueOf(info.getLinkId());
            node.setLinkId(linkId);

            String bottleLevel = getBottleLevel(info.getEntranceType(), info.getLinkEntrance(),
                info.getApplicationName(), queryBottleNeckPreOneMinute());
            node.setBottleLevel(bottleLevel);
            nodeList.add(node);
            if (StringUtils.isNotEmpty(info.getFromLinkIds())) {
                String[] fromLinkId = info.getFromLinkIds().split(",");
                for (String fromLink : fromLinkId) {
                    TopologyLink link = new TopologyLink();
                    link.setFrom(fromLink);
                    link.setTo(linkId);
                    linkList.add(link);
                }
            }
            if (StringUtils.isNotEmpty(info.getToLinkIds())) {
                String[] toinkId = info.getToLinkIds().split(",");
                for (String toLink : toinkId) {
                    TopologyLink link = new TopologyLink();
                    link.setFrom(linkId);
                    link.setTo(toLink);
                    linkList.add(link);
                }
            }
        }
        vo.setLinkList(linkList);
        vo.setNodeList(nodeList);
        return vo;
    }

    /**
     * ????????????????????????????????????
     *
     * @return
     */
    private List<LinkBottleneck> queryBottleNeckPreOneMinute() {
        //1,????????????????????????????????????
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, PRE_FIVE_MINUTE);
        return queryBottleNeckPreTime(DateUtils.dateToString(calendar.getTime(), "yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * ?????????????????????????????????
     *
     * @param startTime ??????????????????
     * @return
     */
    private List<LinkBottleneck> queryBottleNeckPreTime(String startTime) {
        return tLinkTopologyInfoDao.queryBottleNeckPreTime(startTime);
    }

    /**
     * ????????????????????????
     *
     * @param type            ??????????????????
     * @param linkEntrance    ??????????????????
     * @param applicationName ????????????
     * @return
     */
    private String getBottleLevel(String type, String linkEntrance, String applicationName,
        List<LinkBottleneck> preMinuteBottleNeckList) {

        List<LinkBottleneck> bottleNeckList = preMinuteBottleNeckList.stream().filter(
            linkBottleneck -> applicationName.equals(linkBottleneck.getAppName()) && getBottleTypeList(type)
                .contains(String.valueOf(linkBottleneck.getBottleneckType()))).collect(Collectors.toList());

        if (bottleNeckList.isEmpty()) {
            //????????????????????????,????????????????????????
            return Constants.NORMAL;
        } else {
            //????????????????????????????????????????????????
            //????????????????????????
            Integer minNodeBottleNeckLevel = bottleNeckList.stream().filter(
                linkBottleneck -> linkEntrance.equals(linkBottleneck.getKeyWords())).map(
                linkBottleneck -> linkBottleneck.getBottleneckLevel()).distinct().min(Comparator.comparingInt(o -> o))
                .orElse(3);
            //???????????????????????????????????????
            //TPS/RT?????????????????????
            Integer tpsRtBottleNeckLevel = bottleNeckList.stream().filter(
                linkBottleneck -> linkBottleneck.getBottleneckType() == 3).map(
                linkBottleneck -> linkBottleneck.getBottleneckLevel()).distinct().min(Comparator.comparingInt(o -> o))
                .orElse(3);

            //????????????????????????,
            Integer basicBottleNeckLevel = bottleNeckList.stream().filter(
                linkBottleneck -> linkBottleneck.getBottleneckType() == 1
                    && linkBottleneck.getCreateTime().getTime() > System.currentTimeMillis() - 60000).map(
                linkBottleneck -> linkBottleneck.getBottleneckLevel()).distinct().min(Comparator.comparingInt(o -> o))
                .orElse(3);
            //            Integer minValue = minNodeBottleNeckLevel > tpsRtBottleNeckLevel ? minBottleNeckLevel :
            //            minNodeBottleNeckLevel;
            List<Integer> list = Lists.newArrayList(minNodeBottleNeckLevel, tpsRtBottleNeckLevel, basicBottleNeckLevel);
            int minValue = IntStream.of(minNodeBottleNeckLevel, tpsRtBottleNeckLevel, basicBottleNeckLevel).min()
                .getAsInt();
            switch (minValue) {
                case 1:
                    return Constants.SERIOUS;
                case 2:
                    //??????, ??????????????????????????????????????????
                    return Constants.ERROR;
                default:
                    return Constants.NORMAL;
            }
        }
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param type ????????????
     * @return
     */
    private List<String> getBottleTypeList(String type) {
        List<String> bottleTypeList = Arrays.stream(BOTTLE_TYPE_DEFAULT).collect(Collectors.toList());
        String entranceType = StringUtils.upperCase(type);
        if (entranceType.contains(Constants.MQ)) {
            entranceType = Constants.MQ;
        }
        switch (entranceType) {
            case Constants.HTTP:
                bottleTypeList = Arrays.stream(BOTTLE_TYPE_HTTP).collect(Collectors.toList());
                break;
            case Constants.MQ:
                bottleTypeList = Arrays.stream(BOTTLE_TYPE_MQ).collect(Collectors.toList());
                break;
            case Constants.JOB:
                bottleTypeList = Arrays.stream(BOTTLE_TYPE_JOB).collect(Collectors.toList());
                break;
            case Constants.DUBBO:
                break;
            default:
                break;
        }
        return bottleTypeList;
    }

    /**
     * ??????????????????????????????
     *
     * @return
     */
    public BottleCountVo queryLinkBottleSummary() {
        List<LinkBottleneck> preMinuteBottleNeckList = queryBottleNeckPreOneMinute();

        BottleCountVo bottleCountVo = new BottleCountVo();
        BottleLevelCountVo summary = new BottleLevelCountVo();
        //TODO ??????????????????????????????
        List<String> totalSeriousList = Lists.newArrayList();
        List<String> totalErrorList = Lists.newArrayList();
        List<String> totalNormalList = Lists.newArrayList();

        List<BottleLevelCountVo> linkRelatedList = Lists.newArrayList();
        //TODO ????????????????????????
        List<Map<String, String>> linkGroupAppList = tLinkTopologyInfoDao.queryLinkGroupInfo();
        Map<String, List<Map<String, String>>> linkGroupMaps = linkGroupAppList.stream().collect(
            Collectors.groupingByConcurrent(map -> MapUtils.getString(map, "linkGroup")));
        linkGroupMaps.keySet().stream().sorted(Comparator.comparingInt(Integer::parseInt)).forEach(linkGroup -> {
            BottleLevelCountVo linkRelated = new BottleLevelCountVo();
            //TODO ?????????????????????????????????????????????
            List<Map<String, String>> linkGroupMapList = linkGroupMaps.get(linkGroup);

            List<String> seriousList = Lists.newArrayList();
            List<String> errorList = Lists.newArrayList();
            List<String> normalList = Lists.newArrayList();

            linkGroupMapList.stream().forEach(linkGroupMap -> {
                String appName = MapUtils.getString(linkGroupMap, "APPLICATION_NAME");
                String entranceType = MapUtils.getString(linkGroupMap, "ENTRANCE_TYPE");
                String linkEntrance = MapUtils.getString(linkGroupMap, "LINK_ENTRANCE");
                String linkId = MapUtils.getString(linkGroupMap, "LINK_ID");
                //??????linkId???????????????????????????????????????????????????????????????
                if (StringUtils.isNotEmpty(linkId)) {
                    if (StringUtils.isEmpty(appName) && StringUtils.isEmpty(linkEntrance)) {
                        normalList.add(linkId);
                    } else {
                        String bottleLevel = getBottleLevel(entranceType, linkEntrance, appName,
                            preMinuteBottleNeckList);
                        switch (bottleLevel) {
                            case Constants.SERIOUS:
                                seriousList.add(linkId);
                                break;
                            case Constants.ERROR:
                                errorList.add(linkId);
                                break;
                            case Constants.NORMAL:
                                normalList.add(linkId);
                                break;
                            default:
                                break;
                        }
                    }

                }
            });
            linkRelated.setSeriousBottle(seriousList.size());
            linkRelated.setBottle(errorList.size());
            linkRelated.setNormal(normalList.size());
            linkRelatedList.add(linkRelated);
            //????????????
            totalSeriousList.addAll(seriousList);
            totalErrorList.addAll(errorList);
            totalNormalList.addAll(normalList);

        });
        summary.setSeriousBottle(totalSeriousList.size());
        summary.setBottle(totalErrorList.size());
        summary.setNormal(totalNormalList.size());

        bottleCountVo.setSummary(summary);
        bottleCountVo.setLinkRelated(linkRelatedList);
        return bottleCountVo;
    }

    /**
     * ????????????????????????
     *
     * @param paramMap ???????????????????????????????????????
     * @return
     */
    public BottleNeckDetailVo queryLinkBottleDetail(Map<String, Object> paramMap) {
        String applicationName = MapUtils.getString(paramMap, "applicationName", "");
        String linkEntrance = MapUtils.getString(paramMap, "linkEntrance", "");
        String entranceType = MapUtils.getString(paramMap, "entranceType", "");

        List<LinkBottleneck> bottleNeckPre24Hour = queryBottleNeckPre24Hour();
        // ???????????????????????????top10
        List<BottleNeckVo> basic = bottleNeckPre24Hour.stream()
            .filter(linkBottleneck -> applicationName.equals(linkBottleneck.getAppName())
                && linkBottleneck.getBottleneckType() == 1 && linkBottleneck.getBottleneckLevel() == 1)
            .sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())).limit(10)
            .map(linkBottleNeck -> {
                BottleNeckVo bottleNeckVo = new BottleNeckVo();
                bottleNeckVo.setBottleNeckContent(linkBottleNeck.getText());
                bottleNeckVo.setCreateTime(
                    DateUtils.dateToString(linkBottleNeck.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                return bottleNeckVo;
            }).collect(Collectors.toList());

        //???????????? ??????top5 ??????top5
        List<BottleNeckVo> seriousAsync = bottleNeckPre24Hour.stream()
            .filter(linkBottleneck -> applicationName.equals(linkBottleneck.getAppName()) && linkEntrance
                .equals(linkBottleneck.getKeyWords()) && linkBottleneck.getBottleneckType() == 2
                && linkBottleneck.getBottleneckLevel() == 1)
            .sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())).limit(5)
            .map(linkBottleNeck -> {
                BottleNeckVo bottleNeckVo = new BottleNeckVo();
                bottleNeckVo.setBottleNeckContent(linkBottleNeck.getText());
                bottleNeckVo.setCreateTime(
                    DateUtils.dateToString(linkBottleNeck.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                return bottleNeckVo;
            }).collect(Collectors.toList());
        List<BottleNeckVo> errorAsync = bottleNeckPre24Hour.stream()
            .filter(linkBottleneck -> applicationName.equals(linkBottleneck.getAppName()) && linkEntrance
                .equals(linkBottleneck.getKeyWords()) && linkBottleneck.getBottleneckType() == 2
                && linkBottleneck.getBottleneckLevel() == 2)
            .sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())).limit(5)
            .map(linkBottleNeck -> {
                BottleNeckVo bottleNeckVo = new BottleNeckVo();
                bottleNeckVo.setBottleNeckContent(linkBottleNeck.getText());
                bottleNeckVo.setCreateTime(
                    DateUtils.dateToString(linkBottleNeck.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                return bottleNeckVo;
            }).collect(Collectors.toList());
        List<AsyncVo> async = Lists.newArrayList();
        async.add(new AsyncVo(Constants.SERIOUS, seriousAsync));
        async.add(new AsyncVo(Constants.ERROR, errorAsync));

        //????????? ??????top5 ??????top5
        List<BottleNeckVo> seriousStability = bottleNeckPre24Hour.stream()
            .filter(linkBottleneck -> applicationName.equals(linkBottleneck.getAppName())
                && linkBottleneck.getBottleneckType() == 3 && linkBottleneck.getBottleneckLevel() == 1)
            .sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())).limit(5)
            .map(linkBottleNeck -> {
                BottleNeckVo bottleNeckVo = new BottleNeckVo();
                bottleNeckVo.setBottleNeckContent(linkBottleNeck.getText());
                bottleNeckVo.setCreateTime(
                    DateUtils.dateToString(linkBottleNeck.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                return bottleNeckVo;
            }).collect(Collectors.toList());
        List<BottleNeckVo> errorStability = bottleNeckPre24Hour.stream()
            .filter(linkBottleneck -> applicationName.equals(linkBottleneck.getAppName())
                && linkBottleneck.getBottleneckType() == 3 && linkBottleneck.getBottleneckLevel() == 2)
            .sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())).limit(5)
            .map(linkBottleNeck -> {
                BottleNeckVo bottleNeckVo = new BottleNeckVo();
                bottleNeckVo.setBottleNeckContent(linkBottleNeck.getText());
                bottleNeckVo.setCreateTime(
                    DateUtils.dateToString(linkBottleNeck.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                return bottleNeckVo;
            }).collect(Collectors.toList());
        List<StabilityVo> stability = Lists.newArrayList();
        stability.add(new StabilityVo(Constants.SERIOUS, seriousStability));
        stability.add(new StabilityVo(Constants.ERROR, errorStability));

        //RT????????????
        List<BottleNeckVo> seriousRt = bottleNeckPre24Hour.stream()
            .filter(linkBottleneck -> applicationName.equals(linkBottleneck.getAppName()) && linkEntrance
                .equals(linkBottleneck.getKeyWords()) && linkBottleneck.getBottleneckType() == 4
                && linkBottleneck.getBottleneckLevel() == 1)
            .sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())).limit(5)
            .map(linkBottleNeck -> {
                BottleNeckVo bottleNeckVo = new BottleNeckVo();
                bottleNeckVo.setBottleNeckContent(linkBottleNeck.getText());
                bottleNeckVo.setCreateTime(
                    DateUtils.dateToString(linkBottleNeck.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                return bottleNeckVo;
            }).collect(Collectors.toList());
        List<RtVo> rt = Lists.newArrayList();
        //??????????????????????????????????????????
        String type = "";
        if (entranceType.equalsIgnoreCase(Constants.HTTP)) {
            TWList wList = whiteListDAO.getWhiteListByParam(new HashMap<String, String>(10) {{
                put("url", linkEntrance);
                put("type", "1");
            }});
            type = wList == null ? "" : wList.getHttpType();
        }
        rt.add(new RtVo(type, seriousRt));

        //????????????
        BottleNeckDetailVo bottleNeckDetail = new BottleNeckDetailVo();
        bottleNeckDetail.setBasic(basic);
        bottleNeckDetail.setAsync(async);
        bottleNeckDetail.setStability(stability);
        bottleNeckDetail.setRt(rt);
        return bottleNeckDetail;
    }

    /**
     * ??????24????????????????????????
     *
     * @return
     */
    private List<LinkBottleneck> queryBottleNeckPre24Hour() {
        //1,????????????????????????????????????
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, PRE_TWENTY_FOUR_HOUR);
        return queryBottleNeckPreTime(DateUtils.dateToString(calendar.getTime(), "yyyy-MM-dd HH:mm:ss"));
    }
}
