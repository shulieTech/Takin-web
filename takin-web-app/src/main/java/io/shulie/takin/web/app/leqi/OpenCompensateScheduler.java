//package com.ygsoft.dap.mapp.tax.iip.service.impl.application.scheduler;
//
//import cn.hutool.core.util.ObjectUtil;
//import com.google.common.util.concurrent.ThreadFactoryBuilder;
//import com.ygsoft.dap.mapp.tax.iip.infrastructure.cache.common.IipCacheService;
//import com.ygsoft.dap.mapp.tax.iip.infrastructure.type.IipCacheType;
//import com.ygsoft.dap.mapp.tax.iip.model.bo.invoicing.invelec.PushInvoiceResultBo;
//import com.ygsoft.dap.mapp.tax.iip.model.type.AllSpecinvTypeEnum;
//import com.ygsoft.dap.mapp.tax.iip.model.vo.invoicing.invelec.UploadSerialNumQueueVO;
//import com.ygsoft.dap.mapp.tax.iip.repositories.application.invoicing.invelec.IAllInvPushInvoiceRepository;
//import com.ygsoft.dap.mapp.tax.iip.service.application.invoicing.invelec.IAllCreditLimitService;
//import com.ygsoft.dap.mapp.tax.iip.service.application.invoicing.invelec.IAllInvAuthorManageService;
//import com.ygsoft.dap.mapp.tax.iip.service.application.invoicing.invelec.IAllInvSpecInvoiceService;
//import com.ygsoft.dap.mapp.tax.iip.service.application.invoicing.invelec.IIipInvoiceFailService;
//import com.ygsoft.dap.mapp.tax.iip.service.application.invoicing.invelec.IIipOpenerResultService;
//import com.ygsoft.dap.mapp.tax.iip.service.application.invoicing.invelec.IIvcTdcCompensateService;
//import com.ygsoft.dap.mapp.tax.iip.service.application.invoicing.invelec.IIvcTdcRedConfirmService;
//import com.ygsoft.dap.mapp.tax.iip.service.application.invoicing.invelec.dppt.IIipBuildingWriteInvoiceService;
//import com.ygsoft.dap.mapp.tax.iip.service.application.invoicing.invelec.dppt.IIipHWYSWriteInvoiceService;
//import com.ygsoft.dap.mapp.tax.iip.service.application.invoicing.invelec.dppt.IIipLKYSWriteInvoiceService;
//import com.ygsoft.dap.mapp.tax.iip.service.application.invoicing.invelec.dppt.IIipPVWriteInvoiceService;
//import com.ygsoft.dap.mapp.tax.iip.service.application.invoicing.invelec.dppt.IIipWriteInvoiceService;
//import com.ygsoft.dap.mapp.tax.iip.service.application.scheduler.IIipSchedulingService;
//import com.ygsoft.dap.mapp.tax.iip.service.impl.application.invoicing.invelec.dppt.IipNCPXSWriteInvoiceService;
//import com.ygsoft.dap.mapp.tax.iip.service.impl.application.invoicing.invelec.dppt.IipRentWriteInvoiceService;
//import com.ygsoft.dap.mapp.tax.iip.service.redisson.RedissonLockService;
//import com.ygsoft.dap.mapp.tax.iip.service.redisson.RedissonManager;
//import com.ygsoft.dap.mapp.tax.iip.thirdinter.dppt.RequestBase;
//import com.ygsoft.dap.mapp.tax.iip.thirdinter.dppt.RequestQdfpsc;
//import com.ygsoft.dap.mapp.tax.iip.thirdinter.dppt.ResponseBase;
//import com.ygsoft.dap.mapp.tax.iip.thirdinter.dppt.building.RequestBuildQdfpsc;
//import com.ygsoft.dap.mapp.tax.iip.thirdinter.dppt.pv.RequestPVQdfpsc;
//import com.ygsoft.dap.mapp.tax.iip.thirdinter.dppt.rent.RequestRentQdfpsc;
//import com.ygsoft.dap.mapp.tax.iip.utils.DoInvoiceUtils;
//import com.ygsoft.dap.mapp.tax.iip.utils.SystemConst;
//import com.ygsoft.ecp.service.dataaccess.exception.DataaccessException;
//import com.ygsoft.ecp.service.log.EcpLogFactory;
//import com.ygsoft.ecp.service.log.IEcpLog;
//import com.ygsoft.ecp.service.tool.DateUtil;
//import com.ygsoft.ecp.service.tool.JSONUtil;
//import com.ygsoft.ecp.service.tool.StringUtil;
//import com.ygsoft.ecp.service.tool.UuidUtil;
//import com.ygsoft.jt.teng.fw.core.base.sandbox.SandboxUtil;
//import com.ygsoft.jt.teng.fw.core.base.token.TokenHolder;
//import com.ygsoft.jt.teng.fw.core.dataaccess.util.TransactionUtil;
//import com.ygsoft.jt.teng.fw.core.log.memorychainlog.util.MCLogUtil;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//import org.apache.commons.lang.ObjectUtils;
//import org.redisson.api.RLock;
//import org.redisson.api.RQueue;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class OpenCompensateScheduler
//        implements IIipSchedulingService {
//    private static final IEcpLog LOG = EcpLogFactory.getLog(OpenCompensateScheduler.class);
//    public static final String lockName = "open-compensate:key";
//    public static final String queueName = "IIP:uploadSerialNumQueue:";
//    private static final int maxThread = 20;
//    private static final int maxCallNum = 3;
//    private static final Map<String, Function<List<? extends RequestBase>, ResponseBase>> HANDLER_MAP = new HashMap<String, Function<List<? extends RequestBase>, ResponseBase>>();
//    private static volatile ThreadPoolExecutor executor = new ThreadPoolExecutor(15, 15, 45L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10000000), new ThreadFactoryBuilder().setNameFormat("OpenCompensate-%d").build());
//    private static volatile ThreadPoolExecutor splitUpExecutor = new ThreadPoolExecutor(10, 10, 45L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10000), new ThreadFactoryBuilder().setNameFormat("OpenSplitComp-%d").build());
//    @Autowired
//    private IIipWriteInvoiceService iIipWriteInvoiceService;
//    @Autowired
//    private IIipPVWriteInvoiceService iIipPVWriteInvoiceService;
//    @Autowired
//    private IIipInvoiceFailService iipInvoiceFailService;
//    @Autowired
//    private IIipOpenerResultService iipOpenerResultService;
//    @Autowired
//    private IIvcTdcCompensateService ivcTdcCompensateService;
//    @Autowired
//    private IIvcTdcRedConfirmService ivcTdcRedConfirmService;
//    @Autowired
//    private IAllInvAuthorManageService allInvAuthorManageService;
//    @Autowired
//    private RedissonLockService redissonLockService;
//    @Autowired
//    private IipRentWriteInvoiceService iipRentWriteInvoiceService;
//    @Autowired
//    private IIipBuildingWriteInvoiceService iIipBuildingWriteInvoiceService;
//    @Autowired
//    private IAllInvSpecInvoiceService allInvSpecInvoiceService;
//    @Autowired
//    private IIipHWYSWriteInvoiceService iIipHWYSWriteInvoiceService;
//    @Autowired
//    private IipNCPXSWriteInvoiceService iipNCPXSWriteInvoiceService;
//    @Autowired
//    private IIipLKYSWriteInvoiceService iIipLKYSWriteInvoiceService;
//    @Autowired
//    private RedissonManager redissonManager;
//    @Autowired
//    private IAllInvPushInvoiceRepository allInvPushInvoiceRepository;
//    @Autowired
//    private IAllCreditLimitService allCreditLimitService;
//
//    public void execute(String orgCode) {
//        List list;
//        String maxOperateNum;
//        String thirdOpenService;
//        int size = executor.getQueue().size();
//        /*178*/         if (size > 10000) {
//            /*179*/             return;
//        }
//        /*181*/         if (LOG.isInfoEnabled()) {
//            /*182*/             LOG.info((Object)("????????????????????????:" + orgCode));
//        }
//        /*185*/         if ("1".equals(thirdOpenService = SystemConst.getSystemParamValue((String)"ALL_INV_THIRD_OPEN_SERVICE"))) {
//            /*187*/             return;
//        }
//        String uploadFailHttpStatusCode = "," + SystemConst.getSystemParamValue((String)"ALL_INV_UPLOAD_FAIL_HTTP_STATUS_CODE") + ",";
//        String maxUploadNum = SystemConst.getSystemParamValue((String)"ALL_INV_UPLOAD_MAX_CALL_NUM");
//        /*194*/         if (StringUtil.isEmpty((String)maxUploadNum)) {
//            /*195*/             maxUploadNum = "3";
//        }
//        /*198*/         if (StringUtil.isEmpty((String)(maxOperateNum = SystemConst.getSystemParamValue((String)"ALL_INV_UPLOAD_MAX_OPERATE_NUM")))) {
//            /*200*/             maxOperateNum = "20";
//        }
//        /*202*/         if ((list = this.ivcTdcCompensateService.queryInvoiceWaitGroupTin()) == null || list.isEmpty()) {
//            /*204*/             return;
//        }
//        /*207*/         List<String> sellerTinList = this.filterTinList(list, "00", "seller_tin");
//        /*208*/         this.uploadInvoice(sellerTinList, maxOperateNum, maxUploadNum, uploadFailHttpStatusCode, orgCode, "00");
//        /*210*/         List<String> sellerTinListRe = this.filterTinList(list, AllSpecinvTypeEnum.BDCFWFP.getValue(), "seller_tin");
//        /*211*/         this.uploadInvoice(sellerTinListRe, maxOperateNum, maxUploadNum, uploadFailHttpStatusCode, orgCode, AllSpecinvTypeEnum.BDCFWFP.getValue());
//        /*213*/         List<String> sellerTinListBuild = this.filterTinList(list, AllSpecinvTypeEnum.JZFWFP.getValue(), "seller_tin");
//        /*214*/         this.uploadInvoice(sellerTinListBuild, maxOperateNum, maxUploadNum, uploadFailHttpStatusCode, orgCode, AllSpecinvTypeEnum.JZFWFP.getValue());
//        /*216*/         List<String> sellerTinListTransport = this.filterTinList(list, AllSpecinvTypeEnum.HWYSFWFP.getValue(), "seller_tin");
//        /*217*/         this.uploadInvoice(sellerTinListTransport, maxOperateNum, maxUploadNum, uploadFailHttpStatusCode, orgCode, AllSpecinvTypeEnum.HWYSFWFP.getValue());
//        /*219*/         List<String> sellerTinListArgProduct = this.filterTinList(list, AllSpecinvTypeEnum.ZCNCPXSFP.getValue(), "seller_tin");
//        /*220*/         this.uploadInvoice(sellerTinListArgProduct, maxOperateNum, maxUploadNum, uploadFailHttpStatusCode, orgCode, AllSpecinvTypeEnum.ZCNCPXSFP.getValue());
//        /*222*/         List<String> sellerTinListTravel = this.filterTinList(list, AllSpecinvTypeEnum.LKYSFWFP.getValue(), "seller_tin");
//        /*223*/         this.uploadInvoice(sellerTinListTravel, maxOperateNum, maxUploadNum, uploadFailHttpStatusCode, orgCode, AllSpecinvTypeEnum.LKYSFWFP.getValue());
//        /*225*/         List<String> buyerTinList = this.filterTinList(list, AllSpecinvTypeEnum.GFSGFP.getValue(), "buy_tx_pyr_no");
//        /*226*/         this.uploadInvoice(buyerTinList, maxOperateNum, maxUploadNum, uploadFailHttpStatusCode, orgCode, AllSpecinvTypeEnum.GFSGFP.getValue());
//    }
//
//    private List<String> filterTinList(List<Map> list, String specInvType, String key) {
//        /*240*/         return list.stream().filter(map -> {
//            /*242*/             if (StringUtil.isEmptyString((String)ObjectUtils.toString(map.get("spec_inv_type")))) {
//                /*243*/                 return "00".equals(specInvType);
//            }
//            /*245*/             return specInvType.equals(ObjectUtils.toString(map.get("spec_inv_type")));
//        }).map(map -> ObjectUtils.toString(map.get(key))).distinct().collect(Collectors.toList());
//    }
//
//    private void uploadInvoice(List<String> taxNolist, final String maxOperateNum, final String maxUploadNum, final String uploadFailHttpStatusCode, final String orgCode, final String specInvType) {
//        /*260*/         if (taxNolist == null || taxNolist.isEmpty()) {
//            /*261*/             return;
//        }
//        /*263*/         for (final String sellerTin : taxNolist) {
//            /*264*/             if (!StringUtil.isNotEmptyString((String)sellerTin)) continue;
//            /*265*/             executor.execute(new Runnable(){
//
//                @Override
//                public void run() {
//                    /*268*/                     MCLogUtil.writeCustomStartLog((String)"??????????????????", (String)"OpenCompensateScheduler", (String)("??????????????????????????????????????????????????????" + orgCode));
//                    /*269*/                     TokenHolder.INSTANCE.setTenantId(orgCode);
//                    try {
//                        /*271*/                         Map authorManage = OpenCompensateScheduler.this.allInvAuthorManageService.findAuthorManageByTaxNo(sellerTin);
//                        /*272*/                         if (authorManage == null || authorManage.get("accesscompcode") == null || StringUtil.isEmptyString((String)authorManage.get("accesscompcode").toString())) {
//                            /*274*/                             TokenHolder.INSTANCE.clearTenantId();
//                            /*275*/                             return;
//                        }
//                        /*278*/                         String accesscompcode = authorManage.get("accesscompcode").toString();
//                        List list = OpenCompensateScheduler.this.queryInvoiceList(sellerTin, specInvType, maxOperateNum, maxUploadNum);
//                        if (null != list && !list.isEmpty()) {
//                            /*282*/                             OpenCompensateScheduler.this.checkInvoiceDate(list, sellerTin, specInvType);
//                        }
//                        /*284*/                         if (null != list && !list.isEmpty()) {
//                            /*285*/                             OpenCompensateScheduler.this.upInvoice(sellerTin, list, accesscompcode, uploadFailHttpStatusCode, specInvType, orgCode);
//                        }
//                    }
//                    catch (Exception e) {
//                        /*288*/                         LOG.error((Object)"???????????????????????????", (Throwable)e);
//                    }
//                    /*290*/                     TokenHolder.INSTANCE.clearTenantId();
//                    /*291*/                     MCLogUtil.writeCustomEndLog((String)"??????????????????", (String)"OpenCompensateScheduler", (String)("??????????????????????????????????????????????????????" + orgCode));
//                }
//            });
//        }
//    }
//
//    public void pushInvoiceInfoIntoQue(Map map) {
//        /*301*/         if (!ObjectUtil.isEmpty(map.get("data_source")) && !"00".equals(map.get("data_source"))) {
//            ArrayList<PushInvoiceResultBo> boList = new ArrayList<PushInvoiceResultBo>();
//            PushInvoiceResultBo info = new PushInvoiceResultBo();
//            /*304*/             info.setGid(UuidUtil.newUUID());
//            /*305*/             info.setInvoiceStatus(Integer.valueOf(Integer.parseInt(ObjectUtils.toString(map.get("invoStatus")))));
//            /*306*/             info.setFailMsg(ObjectUtil.isEmpty(map.get("message")) ? null : map.get("message").toString());
//            /*307*/             info.setPushState(Integer.valueOf(0));
//            /*308*/             info.setPushFailMsg(null);
//            /*309*/             info.setFounder(map.get("create_user") != null ? map.get("create_user").toString() : null);
//            /*310*/             info.setLsModifier(map.get("create_user") != null ? map.get("create_user").toString() : null);
//            /*311*/             info.setOwnerId(map.get("ownerid") != null ? map.get("ownerid").toString() : null);
//            /*312*/             info.setDataSource(map.get("data_source") != null ? map.get("data_source").toString() : null);
//            /*313*/             info.setInvoiceNo(ObjectUtil.isEmpty(map.get("fphm")) ? null : map.get("fphm").toString());
//            /*314*/             boList.add(info);
//            /*316*/             this.allInvPushInvoiceRepository.savePushInvoice(boList);
//        }
//    }
//
//    private void fail(List<String> gids, Map dataSourceMap, List<String> failFphms, List<String> failHzqrdbms, Map<String, BigDecimal> periodSumAmt, String doState, String returnMsg, String sellerTin, String specInvType) {
//        String period;
//        BigDecimal sumAmt;
//        /*330*/         String finalReturnMsg = returnMsg;
//        /*331*/         List failReasons = gids.stream().map(id -> {
//            HashMap<String, String> failReasonMap = new HashMap<String, String>();
//            /*333*/             failReasonMap.put("gid", (String)id);
//            /*334*/             failReasonMap.put("failReason", finalReturnMsg);
//            /*335*/             return failReasonMap;
//        }).collect(Collectors.toList());
//        /*337*/         if ("00".equals(specInvType) || AllSpecinvTypeEnum.GFSGFP.getValue().equals(specInvType)) {
//            /*338*/             this.iipInvoiceFailService.insertFromWait(failReasons, doState);
//        } else {
//            /*340*/             this.allInvSpecInvoiceService.insertFromWait(failReasons, doState);
//        }
//        /*342*/         if (failHzqrdbms != null && failHzqrdbms.size() > 0) {
//            /*344*/             String infoHasOpen = "F";
//            /*345*/             this.ivcTdcRedConfirmService.upInfoHasOpenByinvoiceNos(failHzqrdbms, infoHasOpen);
//        }
//        /*348*/         if ((sumAmt = periodSumAmt.get(period = DateUtil.date2Str((String)"yyyyMM", (Date)DateUtil.getCurrentSystemTime()))) != null) {
//            /*351*/             this.allCreditLimitService.returnReceive(sellerTin, period, sumAmt);
//        }
//        /*353*/         String value = SystemConst.getSystemParamValue((String)"ALL_INV_PUSH_MID_GROUND_SWITCH");
//        /*355*/         if (null != failFphms && failFphms.size() > 0 && "1".equals(value)) {
//            /*356*/             for (String fphm : failFphms) {
//                HashMap<String, Object> pushMap = new HashMap<String, Object>();
//                /*358*/                 pushMap.put("invoStatus", 2);
//                /*359*/                 pushMap.put("data_source", dataSourceMap.get(fphm));
//                /*360*/                 pushMap.put("fphm", fphm);
//                /*361*/                 pushMap.put("message", returnMsg);
//                /*362*/                 this.pushInvoiceInfoIntoQue(pushMap);
//            }
//        }
//    }
//
//    public List<Map> checkInvoiceDate(List<Map> list, String sellerTin, String specInvType) {
//        Map invoice;
//        Date invDate;
//        ArrayList<String> gids = new ArrayList<String>();
//        HashMap<String, List<String>> serialNoMap = new HashMap<String, List<String>>();
//        ArrayList<String> failFphms = new ArrayList<String>();
//        ArrayList<String> failHzqrdbms = new ArrayList<String>();
//        HashMap dataSourceMap = new HashMap();
//        HashMap<String, BigDecimal> periodSumAmt = new HashMap<String, BigDecimal>();
//        Iterator<Map> iterator = list.iterator();
//        /*383*/         if (iterator.hasNext() && !this.isInvDateWithin48Hours(invDate = (Date)(invoice = iterator.next()).get("inv_date"))) {
//            /*387*/             this.dataProcessing(gids, serialNoMap, failFphms, failHzqrdbms, dataSourceMap, periodSumAmt, invoice, invDate);
//            /*389*/             iterator.remove();
//        }
//        /*392*/         if (gids.size() > 0) {
//            /*393*/             this.fail(gids, dataSourceMap, failFphms, failHzqrdbms, periodSumAmt, "8", "????????????48?????????????????????", sellerTin, specInvType);
//            for (Map.Entry entry : serialNoMap.entrySet()) {
//                /*397*/                 this.iipOpenerResultService.updateDoStateBySerialNos(Integer.valueOf(6), "????????????48?????????????????????", (List)entry.getValue(), (String)entry.getKey());
//            }
//        }
//        /*401*/         return list;
//    }
//
//    private void dataProcessing(List<String> gids, Map<String, List<String>> serialNoMap, List<String> failFphms, List<String> failHzqrdbms, Map dataSourceMap, Map<String, BigDecimal> periodSumAmt, Map invoice, Date invDate) {
//        /*413*/         dataSourceMap.put(ObjectUtils.toString(invoice.get("invoice_no")), invoice.get("data_source"));
//        /*414*/         String year = DateUtil.date2Str((String)"yyyy", (Date)invDate);
//        /*415*/         List<String> serialNos = serialNoMap.get(year);
//        /*416*/         if (serialNos == null) {
//            serialNos = new ArrayList<String>();
//        }
//        /*419*/         serialNos.add(ObjectUtils.toString(invoice.get("invoice_req_ser_no")));
//        /*420*/         serialNoMap.put(year, serialNos);
//        /*421*/         String gid = ObjectUtils.toString(invoice.get("gid"));
//        /*422*/         gids.add(gid);
//        /*423*/         String invongType = ObjectUtils.toString(invoice.get("invong_type"));
//        /*424*/         String fphm = ObjectUtils.toString(invoice.get("invoice_no"));
//        /*425*/         failFphms.add(fphm);
//        /*427*/         if (invongType.equals("1")) {
//            /*428*/             String redConfirmNo = ObjectUtils.toString(invoice.get("red_confirm_no"));
//            /*429*/             failHzqrdbms.add(redConfirmNo);
//        } else {
//            String period = DateUtil.date2Str((String)"yyyyMM", (Date)invDate);
//            BigDecimal sumAmt = periodSumAmt.get(period);
//            /*434*/             if (sumAmt == null) {
//                sumAmt = new BigDecimal(0);
//            }
//            /*437*/             sumAmt = sumAmt.add(new BigDecimal(ObjectUtils.toString(invoice.get("amou_in_tot"))));
//            /*438*/             periodSumAmt.put(period, sumAmt);
//        }
//    }
//
//    public boolean isInvDateWithin48Hours(Date invDate) {
//        Date now = new Date();
//        /*452*/         long fortyEightHoursInMillis = 172800000L;
//        long diff = now.getTime() - invDate.getTime();
//        return diff < 172800000L;
//    }
//
//    private boolean needRetryUploadInv(String taxNo, String specInvType) {
//        /*467*/         Object object = IipCacheService.cacheGet((IipCacheType)IipCacheType.IIP_INV_UPLOAD_RETRY_TIME_CACHE, (String)(taxNo + "_" + specInvType));
//        /*468*/         long now = System.currentTimeMillis();
//        /*470*/         String retryTime = SystemConst.getSystemParamValue((String)"ALL_INV_UPLOAD_RETRY_TIME");
//        /*472*/         long retryTimeLong = 600000L;
//        /*473*/         if (StringUtil.isNotEmptyString((String)retryTime)) {
//            try {
//                /*475*/                 retryTimeLong = Long.parseLong(retryTime) * 60L * 1000L;
//            }
//            catch (NumberFormatException e) {
//                /*477*/                 LOG.error((Object)"??????????????????????????????????????????????????????", (Throwable)e);
//            }
//        }
//        /*480*/         if (object != null) {
//            /*481*/             long lastTime = (Long)object;
//            /*482*/             if (now - lastTime > retryTimeLong) {
//                /*483*/                 IipCacheService.cachePut((IipCacheType)IipCacheType.IIP_INV_UPLOAD_RETRY_TIME_CACHE, (String)(taxNo + "_" + specInvType), (Object)now);
//                /*484*/                 return true;
//            }
//            /*486*/             return false;
//        }
//        /*489*/         IipCacheService.cachePut((IipCacheType)IipCacheType.IIP_INV_UPLOAD_RETRY_TIME_CACHE, (String)(taxNo + "_" + specInvType), (Object)now);
//        /*490*/         return true;
//    }
//
//    private void upInvoice(String taxNo, List<Map> list, String accesscompcode, String uploadFailHttpStatusCode, String specInvType, String orgCode) {
//        block15: {
//            ArrayList<String> gids = new ArrayList<String>();
//            HashMap<String, List<String>> serialNoMap = new HashMap<String, List<String>>();
//            /*508*/             String sllsh = null;
//            HashMap dataSourceMap = new HashMap();
//            /*510*/             boolean addFail = false;
//            try {
//                boolean flag;
//                String doState;
//                ArrayList<String> failFphms = new ArrayList<String>();
//                ArrayList<String> failHzqrdbms = new ArrayList<String>();
//                serialNoMap = new HashMap();
//                HashMap<String, BigDecimal> periodSumAmt = new HashMap<String, BigDecimal>();
//                /*517*/                 for (Map invoice : list) {
//                    /*518*/                     Date invDate = (Date)invoice.get("inv_date");
//                    /*519*/                     this.dataProcessing(gids, serialNoMap, failFphms, failHzqrdbms, dataSourceMap, periodSumAmt, invoice, invDate);
//                    /*520*/                     invoice.put("accesscompcode", accesscompcode);
//                    /*521*/                     String string = ObjectUtils.toString(invoice.get("invoice_no"));
//                }
//                /*523*/                 List<? extends RequestBase> rqs = this.invoiceToDpptVO(list, specInvType);
//                /*525*/                 UploadSerialNumQueueVO vo = null;
//                /*528*/                 String returnMsg = "";
//                /*529*/                 if (rqs.isEmpty()) {
//                    /*530*/                     doState = "8";
//                    returnMsg = "?????????????????????" + taxNo + "?????????????????????????????????mac???????????????????????????????????????????????????????????????";
//                } else {
//                    ResponseBase retBase = this.invUploadDppt(rqs, specInvType);
//                    /*535*/                     if (retBase.isSuccess().booleanValue()) {
//                        /*536*/                         doState = "7";
//                        /*538*/                         String data = ObjectUtils.toString((Object)retBase.getData().toString());
//                        /*539*/                         Map dataMap = (Map)JSONUtil.fromJsonString((String)data, Map.class);
//                        /*540*/                         sllsh = ObjectUtils.toString(dataMap.get("sllsh"));
//                        vo = new UploadSerialNumQueueVO();
//                        /*542*/                         vo.setSerialNum(sllsh);
//                        /*543*/                         String invoiceNos = rqs.stream().map(e -> {
//                            /*545*/                             if (e instanceof RequestQdfpsc) {
//                                /*546*/                                 return ((RequestQdfpsc)e).getFphm();
//                            }
//                            /*547*/                             if (e instanceof RequestPVQdfpsc) {
//                                /*548*/                                 return ((RequestPVQdfpsc)e).getFphm();
//                            }
//                            /*549*/                             if (e instanceof RequestRentQdfpsc) {
//                                /*550*/                                 return ((RequestRentQdfpsc)e).getFphm();
//                            }
//                            /*552*/                             return ((RequestBuildQdfpsc)e).getFphm();
//                        }).collect(Collectors.joining(","));
//                        /*555*/                         vo.setInvoiceNos(invoiceNos);
//                        /*556*/                         vo.setTaxNo(taxNo);
//                        /*557*/                         vo.setSpecInvType("00");
//                    } else {
//                        /*559*/                         String returnCode = retBase.getReturnCode();
//                        /*560*/                         if (uploadFailHttpStatusCode.indexOf("," + returnCode + ",") > -1) {
//                            /*562*/                             doState = "5";
//                        } else {
//                            /*564*/                             doState = "8";
//                            /*565*/                             if ("200".equals(retBase.getHttpStatusCode())) {
//                                /*566*/                                 if (list.size() <= 10 && list.size() > 1) {
//                                    list.forEach(e -> splitUpExecutor.execute(() -> SandboxUtil.doInTenant((String)orgCode, () -> {
//                                        /*570*/                                         this.upInvoice(taxNo, Collections.singletonList(e), accesscompcode, uploadFailHttpStatusCode, specInvType, orgCode);
//                                        /*571*/                                         return null;
//                                    })));
//                                    /*573*/                                     return;
//                                }
//                                /*574*/                                 if (list.size() > 10) {
//                                    /*576*/                                     for (int i = 0; i < list.size(); i += 10) {
//                                        /*577*/                                         List<Map> subList = list.subList(i, Math.min(i + 10, list.size()));
//                                        splitUpExecutor.execute(() -> SandboxUtil.doInTenant((String)orgCode, () -> {
//                                            /*579*/                                             this.upInvoice(taxNo, subList, accesscompcode, uploadFailHttpStatusCode, specInvType, orgCode);
//                                            /*580*/                                             return null;
//                                        }));
//                                    }
//                                    /*583*/                                     return;
//                                }
//                            }
//                        }
//                    }
//                    /*588*/                     returnMsg = ObjectUtils.toString((Object)retBase.getReturnMsg());
//                }
//                /*591*/                 String finalReturnMsg = returnMsg;
//                /*592*/                 String finalSllsh = sllsh;
//                /*593*/                 HashMap<String, List<String>> finalSerialNoMap = serialNoMap;
//                /*594*/                 TransactionUtil.newTransaction(() -> {
//                    /*595*/                     if ("8".equals(doState)) {
//                        /*596*/                         this.fail(gids, dataSourceMap, failFphms, failHzqrdbms, periodSumAmt, doState, finalReturnMsg, taxNo, specInvType);
//                        /*597*/                     } else if ("5".equals(doState)) {
//                        /*598*/                         this.ivcTdcCompensateService.updateInvoiceWaitByGids(gids, doState, finalSllsh, finalReturnMsg);
//                    }
//                    for (Map.Entry entry : finalSerialNoMap.entrySet()) {
//                        /*602*/                         this.iipOpenerResultService.updateDoStateBySerialNos(Integer.valueOf(doState), finalReturnMsg, (List)entry.getValue(), (String)entry.getKey());
//                    }
//                    /*605*/                     return null;
//                });
//                /*608*/                 if (vo != null && (flag = this.addVOToQueue(vo, taxNo))) {
//                    /*611*/                     addFail = true;
//                    /*612*/                     LOG.error((Object)"????????????????????????????????????????????????");
//                    throw new RuntimeException("????????????????????????????????????????????????");
//                }
//                /*616*/                 if (LOG.isInfoEnabled()) {
//                    /*617*/                     LOG.info((Object)"????????????????????????.");
//                }
//            }
//            catch (Exception e2) {
//                /*621*/                 LOG.error((Object)"????????????????????????", (Throwable)e2);
//                /*622*/                 if (!StringUtil.isEmptyString(sllsh) && !addFail) break block15;
//                /*623*/                 this.resetStateTo5(gids, serialNoMap);
//            }
//        }
//    }
//
//    private List<? extends RequestBase> invoiceToDpptVO(List<Map> list, String specInvType) {
//        /*636*/         if ("00".equals(specInvType)) {
//            /*637*/             return this.getRqs(list, specInvType, DoInvoiceUtils::getRequestQdfpsc);
//        }
//        /*638*/         if (AllSpecinvTypeEnum.GFSGFP.getValue().equals(specInvType)) {
//            /*639*/             List<RequestQdfpsc> rqs = this.getRqs(list, specInvType, DoInvoiceUtils::getRequestQdfpsc);
//            ArrayList<RequestPVQdfpsc> pvRqs = new ArrayList<RequestPVQdfpsc>();
//            /*641*/             for (RequestQdfpsc rq : rqs) {
//                RequestPVQdfpsc pvRq = new RequestPVQdfpsc();
//                /*643*/                 BeanUtils.copyProperties((Object)rq, (Object)pvRq);
//                /*644*/                 pvRqs.add(pvRq);
//            }
//            /*646*/             return pvRqs;
//        }
//        /*647*/         if (AllSpecinvTypeEnum.BDCFWFP.getValue().equals(specInvType)) {
//            /*648*/             return this.getRqs(list, specInvType, DoInvoiceUtils::getRequestQdfpscForRe);
//        }
//        /*649*/         if (AllSpecinvTypeEnum.JZFWFP.getValue().equals(specInvType)) {
//            /*650*/             return this.getRqs(list, specInvType, DoInvoiceUtils::getRequestQdfpscForBuild);
//        }
//        /*651*/         if (AllSpecinvTypeEnum.HWYSFWFP.getValue().equals(specInvType)) {
//            /*652*/             return this.getRqs(list, specInvType, DoInvoiceUtils::getRequestQdfpscForTransport);
//        }
//        /*653*/         if (AllSpecinvTypeEnum.ZCNCPXSFP.getValue().equals(specInvType)) {
//            /*654*/             return this.getRqs(list, specInvType, DoInvoiceUtils::getRequestQdfpsc);
//        }
//        /*655*/         if (AllSpecinvTypeEnum.LKYSFWFP.getValue().equals(specInvType)) {
//            /*656*/             return this.getRqs(list, specInvType, DoInvoiceUtils::getRequestQdfpscForTravel);
//        }
//        return new ArrayList();
//    }
//
//    private <T extends RequestBase> List<T> getRqs(List<Map> list, String specInvType, Function<Map, T> doInvoiceFun) {
//        ArrayList<RequestBase> rqs = new ArrayList<RequestBase>();
//        /*673*/         for (Map invoice : list) {
//            /*674*/             RequestBase rq = null;
//            try {
//                /*676*/                 rq = (RequestBase)doInvoiceFun.apply(invoice);
//            }
//            catch (Exception e) {
//                /*678*/                 LOG.error((Object)("?????????????????????????????????????????????" + specInvType + "??????????????????" + JSONUtil.toJsonString((Object)invoice)), (Throwable)e);
//                /*679*/                 continue;
//            }
//            /*681*/             if (rq instanceof RequestQdfpsc) {
//                /*682*/                 RequestQdfpsc requestQdfpsc = (RequestQdfpsc)rq;
//                /*683*/                 if (requestQdfpsc.getIp() != null && requestQdfpsc.getMacdz() != null && requestQdfpsc.getQyDm() != null) {
//                    /*684*/                     rqs.add(rq);
//                    continue;
//                }
//                /*686*/                 LOG.error((Object)("?????????????????????" + requestQdfpsc.getInvokeSh() + "??????????????????" + specInvType + "?????????????????????????????????mac???????????????????????????????????????????????????????????????"));
//                /*688*/                 continue;
//            }
//            if (rq instanceof RequestRentQdfpsc) {
//                /*689*/                 RequestRentQdfpsc requestRentQdfpsc = (RequestRentQdfpsc)rq;
//                /*690*/                 if (requestRentQdfpsc.getIp() != null && requestRentQdfpsc.getMacdz() != null && requestRentQdfpsc.getQyDm() != null) {
//                    /*691*/                     rqs.add(rq);
//                    continue;
//                }
//                /*693*/                 LOG.error((Object)("?????????????????????" + requestRentQdfpsc.getInvokeSh() + "??????????????????" + specInvType + "?????????????????????????????????mac???????????????????????????????????????????????????????????????"));
//                /*695*/                 continue;
//            }
//            if (rq instanceof RequestBuildQdfpsc) {
//                /*696*/                 RequestBuildQdfpsc requestBuildQdfpsc = (RequestBuildQdfpsc)rq;
//                /*697*/                 if (requestBuildQdfpsc.getIp() != null && requestBuildQdfpsc.getMacdz() != null && requestBuildQdfpsc.getQyDm() != null) {
//                    /*698*/                     rqs.add(rq);
//                    continue;
//                }
//                /*700*/                 LOG.error((Object)("?????????????????????" + requestBuildQdfpsc.getInvokeSh() + "??????????????????" + specInvType + "?????????????????????????????????mac???????????????????????????????????????????????????????????????"));
//                /*702*/                 continue;
//            }
//            /*703*/             LOG.error((Object)("????????????????????????" + rq.getClass().getName()));
//        }
//        /*706*/         return rqs;
//    }
//
//    /*
//     * WARNING - Removed try catching itself - possible behaviour change.
//     */
//    private synchronized Function<List<? extends RequestBase>, ResponseBase> getFunction(String specInvType) {
//        /*716*/         if (HANDLER_MAP.isEmpty()) {
//            /*717*/             OpenCompensateScheduler openCompensateScheduler = this;
//            synchronized (openCompensateScheduler) {
//                /*718*/                 if (HANDLER_MAP.isEmpty()) {
//                    /*719*/                     HANDLER_MAP.put("00", rqList -> this.iIipWriteInvoiceService.qdfpsc(rqList));
//                    /*721*/                     HANDLER_MAP.put(AllSpecinvTypeEnum.GFSGFP.getValue(), rqList -> this.iIipPVWriteInvoiceService.qdfpsc(rqList));
//                    /*723*/                     HANDLER_MAP.put(AllSpecinvTypeEnum.BDCFWFP.getValue(), rqList -> this.iipRentWriteInvoiceService.qdfpsc(rqList));
//                    /*725*/                     HANDLER_MAP.put(AllSpecinvTypeEnum.JZFWFP.getValue(), rqList -> this.iIipBuildingWriteInvoiceService.qdfpsc(rqList));
//                    /*727*/                     HANDLER_MAP.put(AllSpecinvTypeEnum.HWYSFWFP.getValue(), rqList -> this.iIipHWYSWriteInvoiceService.hwysfpsc(rqList));
//                    /*729*/                     HANDLER_MAP.put(AllSpecinvTypeEnum.ZCNCPXSFP.getValue(), rqList -> this.iipNCPXSWriteInvoiceService.ncpxsfpsc(rqList));
//                    /*731*/                     HANDLER_MAP.put(AllSpecinvTypeEnum.LKYSFWFP.getValue(), rqList -> this.iIipLKYSWriteInvoiceService.lkysfpsc(rqList));
//                }
//            }
//        }
//        /*735*/         return HANDLER_MAP.get(specInvType);
//    }
//
//    private ResponseBase invUploadDppt(List<? extends RequestBase> rqs, String specInvType) {
//        ResponseBase response;
//        Function<List<? extends RequestBase>, ResponseBase> handler = this.getFunction(specInvType);
//        if (handler != null && (response = handler.apply(rqs)) != null) {
//            /*750*/             return response;
//        }
//        return new ResponseBase("E0003", "??????????????????????????????");
//    }
//
//    /*
//     * WARNING - Removed try catching itself - possible behaviour change.
//     */
//    private List<Map> queryInvoiceList(String taxNo, String specInvType, String maxOperateNum, String maxUploadNum) {
//        List list;
//        block9: {
//            /*766*/             list = null;
//            try {
//                /*768*/                 LOG.info((Object)("?????????????????????????????????:" + taxNo));
//                /*769*/                 RLock lock = this.redissonLockService.getLock(lockName + taxNo + "_" + specInvType);
//                /*770*/                 if (this.redissonLockService.tryLock(lock)) {
//                    try {
//                        /*772*/                         list = this.ivcTdcCompensateService.queryInvoiceWaitWaitingForUpload(maxOperateNum, maxUploadNum, specInvType, taxNo);
//                        /*774*/                         if (this.needRetryUploadInv(taxNo, specInvType)) {
//                            /*775*/                             this.ivcTdcCompensateService.updateUploadNumByTins(taxNo, specInvType);
//                        }
//                        break block9;
//                    }
//                    catch (Exception e) {
//                        /*778*/                         LOG.error((Object)"???????????????????????????", (Throwable)e);
//                        break block9;
//                    }
//                    finally {
//                        /*780*/                         this.redissonLockService.unlock(lock);
//                        /*781*/                         LOG.info((Object)("????????????????????????????????????" + taxNo));
//                    }
//                }
//                /*784*/                 LOG.info((Object)("??????????????????????????????????????????" + taxNo));
//            }
//            catch (Exception e) {
//                /*787*/                 LOG.error((Object)("??????????????????????????????????????????" + taxNo));
//            }
//        }
//        /*789*/         return list;
//    }
//
//    private boolean addVOToQueue(UploadSerialNumQueueVO vo, String taxNo) {
//        /*801*/         boolean flag = true;
//        /*802*/         int i = 0;
//        /*803*/         while (flag && i < 10) {
//            try {
//                /*805*/                 RQueue queue = this.redissonManager.getRedissonClient().getQueue(queueName + taxNo);
//                /*806*/                 queue.add((Object)vo);
//                /*807*/                 flag = false;
//            }
//            catch (Exception e) {
//                /*809*/                 LOG.error((Object)("???" + ++i + "????????????????????????????????????????????????"), (Throwable)e);
//                try {
//                    /*812*/                     Thread.sleep(500L);
//                }
//                catch (InterruptedException ie) {
//                    /*815*/                     Thread.currentThread().interrupt();
//                    /*816*/                     LOG.error((Object)"????????????????????????????????????????????????????????????:", (Throwable)ie);
//                }
//            }
//        }
//        /*820*/         return flag;
//    }
//
//    private void resetStateTo5(List<String> gids, Map<String, List<String>> serialNoMap) {
//        /*831*/         boolean flag = true;
//        /*832*/         String doState = "5";
//        /*833*/         int i = 0;
//        /*834*/         while (flag && i < 10) {
//            /*835*/             ++i;
//            try {
//                /*837*/                 this.ivcTdcCompensateService.updateInvoiceWaitByGids(gids, doState, null, "????????????????????????????????????????????????5");
//                for (Map.Entry<String, List<String>> entry : serialNoMap.entrySet()) {
//                    /*841*/                     this.iipOpenerResultService.updateDoStateBySerialNos(Integer.valueOf(doState), "????????????????????????", entry.getValue(), entry.getKey());
//                }
//                /*845*/                 flag = false;
//            }
//            catch (DataaccessException de) {
//                /*847*/                 LOG.error((Object)"???????????????????????????????????????????????????,SQLException:", (Throwable)de);
//                try {
//                    /*849*/                     Thread.sleep(1000L);
//                }
//                catch (InterruptedException ie) {
//                    /*852*/                     Thread.currentThread().interrupt();
//                    /*853*/                     LOG.error((Object)"???????????????????????????????????????????????????????????????:", (Throwable)ie);
//                }
//            }
//        }
//    }
//}