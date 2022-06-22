package io.shulie.takin.cloud.ext.content.asset;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 资产拓展模块
 * <p>
 * 付款单实体
 *
 * @author 张天赐
 * @author 元霸
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AssetInvoiceExt<T> extends ContextExt {
    /**
     * 场景ID
     */
    private Long sceneId;
    /**
     * 任务ID
     */
    private Long taskId;
    /**
     * 来源的详情ID
     * <ul>
     *     <li>压测报告 -> 报告ID</li>
     *     <li>业务活动流量验证 -> 业务活动ID</li>
     *     <li>脚本调试 -> 脚本ID</li>
     *     <li>巡检场景 -> </li>
     * </ul>
     */
    private Long resourceId;
    /**
     * 来源的名称
     * <ul>
     *     <li>压测报告 -> 场景名称</li>
     *     <li>业务活动流量验证 -> 业务活动名称</li>
     *     <li>脚本调试 -> 脚本名称</li>
     * </ul>
     */
    private String resourceName;
    /**
     * 数据来源
     * <ol>
     *     <li>压测报告</li>
     *     <li>业务活动流量验证</li>
     *     <li>脚本调试</li>
     * </ol>
     */
    private Integer resourceType;
    /**
     * 操作用户ID
     */
    private Long operateId;
    /**
     * 操作人
     */
    private String operateName;
    /**
     * 租户ID
     */
    private Long customerId;
    /**
     * 业务数据
     */
    private T data;
}