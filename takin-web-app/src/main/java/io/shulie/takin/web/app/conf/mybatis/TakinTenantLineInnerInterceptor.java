package io.shulie.takin.web.app.conf.mybatis;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.update.Update;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.app.conf.mybatis
 * @ClassName: MyTenantLineInnerInterceptor
 * @Description: TODO
 * @Date: 2021/10/19 09:59
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class TakinTenantLineInnerInterceptor extends TenantLineInnerInterceptor {

    private String[] tableArrWithoutTenantId = new String[] {
        "t_user_third_party",
        "t_third_party",
        "t_dictionary_type",
        "t_tc_sequence",
        "t_tenant_info",
        "t_tro_resource",
        "t_config_server",
        "t_pradar_zk_config",
        "t_cache_config_template",
        "t_connectpool_config_template",
        "t_mq_config_template",
        "t_agent_config",
        "t_agent_version",
        "t_exception_info",
        "t_middleware_summary",
        "t_agent_plugin",
        "t_middleware_jar",
        "t_agent_plugin_lib_support",
        "t_plugin_library",
        "t_plugin_dependent",
        "t_plugin_tenant_ref",
        "t_interface_type_main",
        "t_interface_type_child",
        "t_interface_type_config",
        "t_remote_call_config",
        "t_middleware_type",
        "t_tro_version",
        "t_user_config",
        // cloud??????
        "t_ac_account",
        "t_ac_account_balance",
        "t_ac_account_book",
        "t_engine_plugin_files_ref",
        "t_engine_plugin_info",
        "t_engine_plugin_supported_versions",
        "t_report_business_activity_detail",
        "t_scene_big_file_slice",
        "t_scene_business_activity_ref",
        "t_scene_script_ref",
        "t_scene_sla_ref",
        "t_schedule_record",
        "t_schedule_record_engine_plugins_ref",
        "t_strategy_config",
        "t_warn_detail",
        "t_pressure_task_variety",
        "t_pressure_task_callback",
        "t_interface_performance_result"
    };

    private String[] tableArrWithoutEnvCode = new String[] {
        "t_user_third_party",
        "t_third_party",
        "t_tro_user",
        "t_tro_dept",
        "t_dictionary_type",
        "t_tro_user_dept_relation",
        "t_tc_sequence",
        "t_tenant_info",
        "t_tro_resource",
        "t_config_server",
        "t_pradar_zk_config",
        "t_cache_config_template",
        "t_connectpool_config_template",
        "t_mq_config_template",
        "t_agent_config",
        "t_agent_version",
        "t_exception_info",
        "t_middleware_summary",
        "t_agent_plugin",
        "t_middleware_jar",
        "t_agent_plugin_lib_support",
        "t_plugin_library",
        "t_plugin_dependent",
        "t_plugin_tenant_ref",
        "t_tenant_env_ref",
        "t_interface_type_main",
        "t_interface_type_child",
        "t_remote_call_config",
        "t_interface_type_config",
        "t_middleware_type",
        "t_tro_version",
        "t_user_config",
        // cloud??????
        "t_ac_account",
        "t_ac_account_balance",
        "t_ac_account_book",
        "t_engine_plugin_files_ref",
        "t_engine_plugin_info",
        "t_engine_plugin_supported_versions",
        "t_report_business_activity_detail",
        "t_scene_big_file_slice",
        "t_scene_business_activity_ref",
        "t_scene_script_ref",
        "t_scene_sla_ref",
        "t_schedule_record",
        "t_schedule_record_engine_plugins_ref",
        "t_strategy_config",
        "t_warn_detail",
        "t_pressure_task_variety",
        "t_pressure_task_callback",
        "t_interface_performance_result"
    };

    private String[] tableArrWithoutUserId = new String[] {
        "t_user_third_party",
        "t_third_party",
        "t_tro_user",
        "t_tro_dept",
        "t_dictionary_type",
        "t_config_server",
        "t_tc_sequence",
        "t_tenant_info",
        "t_tro_resource",
        "t_config_server",
        "t_pradar_zk_config",
        "t_cache_config_template",
        "t_connectpool_config_template",
        "t_app_remote_call_template_mapping",
        "t_mq_config_template",
        "t_agent_config",
        "t_agent_version",
        "t_exception_info",
        "t_middleware_summary",
        "t_agent_plugin",
        "t_middleware_jar",
        "t_agent_plugin_lib_support",
        "t_plugin_library",
        "t_plugin_dependent",
        "t_plugin_tenant_ref",
        "t_interface_type_main",
        "t_interface_type_child",
        "t_interface_type_config",
        "t_remote_call_config",
        "t_middleware_type",
        "t_tro_version",
        "t_user_config",
        // cloud??????
        "t_ac_account",
        "t_ac_account_balance",
        "t_ac_account_book",
        "t_engine_plugin_files_ref",
        "t_engine_plugin_info",
        "t_engine_plugin_supported_versions",
        "t_report_business_activity_detail",
        "t_scene_big_file_slice",
        "t_scene_business_activity_ref",
        "t_scene_script_ref",
        "t_scene_sla_ref",
        "t_schedule_record",
        "t_schedule_record_engine_plugins_ref",
        "t_strategy_config",
        "t_warn_detail",
        "t_pressure_task_variety",
        "t_pressure_task_callback",
        "t_interface_performance_result"
    };

    /**
     * ??????tenant_id ??????
     */
    private List<String> tableWithoutTenantId = Lists.newArrayList(tableArrWithoutTenantId);

    /**
     * ??????env_code ??????
     */
    private List<String> tableWithoutEnvCode = Lists.newArrayList(tableArrWithoutEnvCode);

    /**
     * ??????user_id ??????
     */
    private List<String> tableWithoutUserId = Lists.newArrayList(tableArrWithoutUserId);

    private TakinTenantLineHandler tenantLineHandler;

    public TakinTenantLineInnerInterceptor(TakinTenantLineHandler takinTenantLineHandler) {
        super.setTenantLineHandler(takinTenantLineHandler);
        this.tenantLineHandler = takinTenantLineHandler;
    }

    /**
     * ????????????
     */
    @Override
    protected Expression builderExpression(Expression currentExpression, Table table) {
        AndExpression tenantExpression = this.buildTenantExpression(table, currentExpression);
        // ???????????????
        if (tenantExpression == null) {
            return currentExpression;
        }
        if (currentExpression == null) {
            return tenantExpression;
        }

        if (currentExpression instanceof OrExpression) {
            return new AndExpression(tenantExpression, new Parenthesis(currentExpression));
        } else {
            return new AndExpression(tenantExpression, currentExpression);
        }
    }

    /**
     * delete update ?????? where ??????
     */
    @Override
    protected BinaryExpression andExpression(Table table, Expression where) {
        //??????where???????????????
        AndExpression tenantExpression = this.buildTenantExpression(table, where);

        if (tenantExpression == null) {
            EqualsTo equalsTo = new EqualsTo(new LongValue(1), new LongValue(1));
            if (null != where) {
                if (where instanceof OrExpression) {
                    return new AndExpression(equalsTo, new Parenthesis(where));
                } else {
                    return new AndExpression(equalsTo, where);
                }
            }
            return equalsTo;
        }
        if (null != where) {
            if (where instanceof OrExpression) {
                return new AndExpression(tenantExpression, new Parenthesis(where));
            } else {
                return new AndExpression(tenantExpression, where);
            }
        }
        return tenantExpression;
    }

    @Override
    protected void processPlainSelect(PlainSelect plainSelect) {
        FromItem fromItem = plainSelect.getFromItem();
        Expression where = plainSelect.getWhere();
        processWhereSubSelect(where);
        if (fromItem instanceof Table) {
            Table fromTable = (Table)fromItem;
            if (!tenantLineHandler.ignoreTable(fromTable.getName())) {
                //#1186 github
                plainSelect.setWhere(builderExpression(where, fromTable));
            }
        } else {
            processFromItem(fromItem);
        }
        //#3087 github
        List<SelectItem> selectItems = plainSelect.getSelectItems();
        if (CollectionUtils.isNotEmpty(selectItems)) {
            selectItems.forEach(this::processSelectItem);
        }
        List<Join> joins = plainSelect.getJoins();
        if (CollectionUtils.isNotEmpty(joins)) {
            processJoins(joins);
        }
        log.debug("??????select???sql???{}???", plainSelect.toString());
    }

    private void processJoins(List<Join> joins) {
        //?????? on ???????????????????????? join?????????????????????????????? on ?????????
        Deque<Table> tables = new LinkedList<>();
        for (Join join : joins) {
            // ?????? on ?????????
            FromItem fromItem = join.getRightItem();
            if (fromItem instanceof Table) {
                Table fromTable = (Table)fromItem;
                // ?????? join ????????? on ???????????????
                Collection<Expression> originOnExpressions = join.getOnExpressions();
                // ?????? join on ????????????????????????????????????
                if (originOnExpressions.size() == 1) {
                    processJoin(join);
                    continue;
                }
                // ?????????????????????
                boolean needIgnore = tenantLineHandler.ignoreTable(fromTable.getName());
                // ????????????????????????????????? null????????????????????????
                tables.push(needIgnore ? null : fromTable);
                // ???????????? on ??????????????????????????????
                if (originOnExpressions.size() > 1) {
                    Collection<Expression> onExpressions = new LinkedList<>();
                    for (Expression originOnExpression : originOnExpressions) {
                        Table currentTable = tables.poll();
                        if (currentTable == null) {
                            onExpressions.add(originOnExpression);
                        } else {
                            onExpressions.add(builderExpression(originOnExpression, currentTable));
                        }
                    }
                    join.setOnExpressions(onExpressions);
                }
            } else {
                // ?????????????????????????????????
                processFromItem(fromItem);
            }
        }
    }

    @Override
    protected void processInsert(Insert insert, int index, String sql, Object obj) {
        //if (tenantLineHandler.ignoreTable(insert.getTable().getName())) {
        //    // ??????????????????
        //    return;
        //}
        //List<Column> columns = insert.getColumns();
        //if (CollectionUtils.isEmpty(columns)) {
        //    // ?????????????????????insert ?????????
        //    return;
        //}
        //String tenantIdColumn = tenantLineHandler.getTenantIdColumn();
        //String envCodeColumn = tenantLineHandler.getEnvCodeColumn();
        //if (tenantLineHandler.ignoreInsert(columns, tenantIdColumn) || tenantLineHandler.ignoreInsert(columns,
        //    envCodeColumn)) {
        //    // ???????????????????????????insert ?????????
        //    return;
        //}
        //columns.add(new Column(tenantIdColumn));
        //columns.add(new Column(envCodeColumn));
        //
        //// fixed gitee pulls/141 duplicate update
        //List<Expression> duplicateUpdateColumns = insert.getDuplicateUpdateExpressionList();
        //if (CollectionUtils.isNotEmpty(duplicateUpdateColumns)) {
        //    AndExpression tenantExepression = this.buildTenantExpression(tenantIdColumn, envCodeColumn);
        //    duplicateUpdateColumns.add(tenantExepression);
        //}
        //
        //Select select = insert.getSelect();
        //if (select != null) {
        //    this.processInsertSelect(select.getSelectBody());
        //} else if (insert.getItemsList() != null) {
        //    // fixed github pull/295
        //    ItemsList itemsList = insert.getItemsList();
        //    if (itemsList instanceof MultiExpressionList) {
        //        ((MultiExpressionList)itemsList).getExpressionLists().forEach(
        //            el -> el.getExpressions()
        //                .addAll(Arrays.asList(tenantLineHandler.getTenantId(), tenantLineHandler.getEnvCode())));
        //    } else {
        //        ((ExpressionList)itemsList).getExpressions().addAll(
        //            Arrays.asList(tenantLineHandler.getTenantId(), tenantLineHandler.getEnvCode()));
        //    }
        //} else {
        //    throw ExceptionUtils.mpe(
        //        "Failed to process multiple-table update, please exclude the tableName or statementId");
        //}
    }

    /**
     * update ????????????
     */
    @Override
    protected void processUpdate(Update update, int index, String sql, Object obj) {
        final Table table = update.getTable();
        if (tenantLineHandler.ignoreTable(table.getName())) {
            // ??????????????????
            return;
        }
        update.setWhere(this.andExpression(table, update.getWhere()));
        log.debug("??????update???sql???{}???", update.toString());
    }

    /**
     * delete ????????????
     */
    @Override
    protected void processDelete(Delete delete, int index, String sql, Object obj) {
        if (tenantLineHandler.ignoreTable(delete.getTable().getName())) {
            // ??????????????????
            return;
        }
        delete.setWhere(this.andExpression(delete.getTable(), delete.getWhere()));
        log.debug("??????update???sql???{}???", delete.toString());
    }

    /**
     * ?????? SelectItem
     *
     * @param selectItems SelectItem
     */
    @Override
    protected void appendSelectItem(List<SelectItem> selectItems) {
        if (CollectionUtils.isEmpty(selectItems)) {
            return;
        }
        if (selectItems.size() == 1) {
            SelectItem item = selectItems.get(0);
            if (item instanceof AllColumns || item instanceof AllTableColumns) {
                return;
            }
        }
        selectItems.add(new SelectExpressionItem(new Column(tenantLineHandler.getTenantIdColumn())));
    }

    @Override
    protected Column getAliasColumn(Table table) {
        StringBuilder column = new StringBuilder();
        if (table.getAlias() != null) {
            column.append(table.getAlias().getName()).append(StringPool.DOT);
        }
        column.append(tenantLineHandler.getTenantIdColumn());
        return new Column(column.toString());
    }

    private Column getAliasColumn(Table table, String columnOrigin) {
        StringBuilder column = new StringBuilder();
        if (table.getAlias() != null) {
            column.append(table.getAlias().getName()).append(StringPool.DOT);
        }
        column.append(columnOrigin);
        return new Column(column.toString());
    }

    private AndExpression buildTenantExpression(Table table, Expression where) {
        // ????????????
        String tenantIdColumn = tenantLineHandler.getTenantIdColumn();
        String envCodeColumn = tenantLineHandler.getEnvCodeColumn();

        EqualsTo tenantIdCondition = null;
        if (!tenantLineHandler.ignoreSearch(where, tenantIdColumn) && !tableWithoutTenantId.contains(table.getName())) {
            tenantIdCondition = new EqualsTo();
            tenantIdCondition.setLeftExpression(this.getAliasColumn(table, tenantLineHandler.getTenantIdColumn()));
            tenantIdCondition.setRightExpression(tenantLineHandler.getTenantId());
        }

        EqualsTo envCodeCondition = null;
        if (!tenantLineHandler.ignoreSearch(where, envCodeColumn) && !tableWithoutEnvCode.contains(table.getName())) {
            envCodeCondition = new EqualsTo();
            envCodeCondition.setLeftExpression(this.getAliasColumn(table, tenantLineHandler.getEnvCodeColumn()));
            envCodeCondition.setRightExpression(tenantLineHandler.getEnvCode());
        }

        if (tenantIdCondition == null && envCodeCondition == null) {
            return null;
        }
        // 1 = 1
        EqualsTo equalsTo = new EqualsTo(new LongValue(1), new LongValue(1));

        //AndExpression allAndExpression = null;
        AndExpression tenantExpression = null;
        if (tenantIdCondition != null && envCodeCondition != null) {
            tenantExpression = new AndExpression(tenantIdCondition, envCodeCondition);
        } else if (tenantIdCondition != null) {
            // t_tro_user ?????? tenant_id
            tenantExpression = new AndExpression(equalsTo, tenantIdCondition);
        }else if(envCodeCondition != null) {
            tenantExpression = new AndExpression(equalsTo, envCodeCondition);
        }

        return tenantExpression;
    }

    //private AndExpression buildTenantExpression(String tenantIdColumn, String envCodeColumn) {
    //    EqualsTo tenantIdCondition = new EqualsTo();
    //    tenantIdCondition.setLeftExpression(new StringValue(tenantIdColumn));
    //    tenantIdCondition.setRightExpression(tenantLineHandler.getTenantId());
    //
    //    EqualsTo envCodeCondition = new EqualsTo();
    //    envCodeCondition.setLeftExpression(new StringValue(envCodeColumn));
    //    envCodeCondition.setRightExpression(tenantLineHandler.getEnvCode());
    //    return new AndExpression(tenantIdCondition, envCodeCondition);
    //}
}
