package io.shulie.takin.cloud.common.utils;

import cn.hutool.core.util.EscapeUtil;
import org.apache.commons.lang3.StringUtils;

public class PtsXmlContentUtils {

    public static String replace2Jmx(String content) {
        if(StringUtils.isBlank(content)) {
            return content;
        }
        return EscapeUtil.escapeHtml4(content);
    }

    public static String parseFromJmx(String content) {
        if(StringUtils.isBlank(content)) {
            return content;
        }
        return EscapeUtil.unescapeHtml4(content);
    }

    public static void main(String[] args) {
        String str = "<?xml version=\"1.0\" encoding=\"GB18030\" standalone=\"yes\"?> <hzbank> <RequestHeader> <SysReqHeader> <BranchNo>7571</BranchNo> <Language>CH</Language> <OperatorId>871015</OperatorId> <ProjectCode>IBD13001</ProjectCode> <SenderDate>2023-06-29</SenderDate> <SenderJournalNo>2306291010316650</SenderJournalNo> <SenderTime>20:00:24</SenderTime> <SourceChannel>6666</SourceChannel> <TerminalNo>999999152148</TerminalNo> <TransactionRef>2306291010316650</TransactionRef> <VersionNo>V1.0</VersionNo> </SysReqHeader> <TxnReqHeader> <MessageType>0000</MessageType> <RelatedAccDate>2023-06-29</RelatedAccDate> <TransactionCode>IBD13001</TransactionCode> </TxnReqHeader> </RequestHeader> <RequestBody> <VouManage_comm> <box_no>757130805</box_no> <info_type>S</info_type> <voucher_type>A</voucher_type> </VouManage_comm> </RequestBody> </hzbank>";
        String encodeStr = EscapeUtil.escapeHtml4(str);
        System.out.println("encode===" + encodeStr);

        System.out.println("decode===" + EscapeUtil.unescapeHtml4(encodeStr));
    }
}
