package io.shulie.takin.web.app.conf.mybatis;

import java.util.*;

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
import net.sf.jsqlparser.statement.select.*;
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

    private String[] tableArrWithoutTenantId = new String[]{
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
            // cloud迁移
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

    private String[] tableArrWithoutEnvCode = new String[]{
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
            // cloud迁移
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

    private String[] tableArrWithoutUserId = new String[]{
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
            // cloud迁移
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
     * 没有tenant_id 的表
     */
    private List<String> tableWithoutTenantId = Lists.newArrayList(tableArrWithoutTenantId);

    /**
     * 没有env_code 的表
     */
    private List<String> tableWithoutEnvCode = Lists.newArrayList(tableArrWithoutEnvCode);

    /**
     * 没有user_id 的表
     */
    private List<String> tableWithoutUserId = Lists.newArrayList(tableArrWithoutUserId);

    private TakinTenantLineHandler tenantLineHandler;

    public TakinTenantLineInnerInterceptor(TakinTenantLineHandler takinTenantLineHandler) {
        super.setTenantLineHandler(takinTenantLineHandler);
        this.tenantLineHandler = takinTenantLineHandler;
    }

    /**
     * 处理条件
     */
    protected Expression builderExpression(Expression currentExpression, Table table) {
        AndExpression tenantExpression = this.buildTenantExpression(table, currentExpression);
        // 没有租户的
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
     * delete update 语句 where 处理
     */
    @Override
    protected BinaryExpression andExpression(Table table, Expression where) {
        //获得where条件表达式
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

    private List<Table> processFromItem(FromItem fromItem) {
        while (fromItem instanceof ParenthesisFromItem) {
            fromItem = ((ParenthesisFromItem) fromItem).getFromItem();
        }

        List<Table> mainTables = new ArrayList();
        if (fromItem instanceof Table) {
            Table fromTable = (Table) fromItem;
            mainTables.add(fromTable);
        } else if (fromItem instanceof SubJoin) {
            List<Table> tables = this.processSubJoin((SubJoin) fromItem);
            mainTables.addAll(tables);
        } else {
            this.processOtherFromItem(fromItem);
        }

        return mainTables;
    }
    private List<Table> processJoins(List<Table> mainTables, List<Join> joins) {
        Table mainTable = null;
        Table leftTable = null;
        if (mainTables == null) {
            mainTables = new ArrayList();
        } else if (((List)mainTables).size() == 1) {
            mainTable = (Table)((List)mainTables).get(0);
            leftTable = mainTable;
        }

        Deque<List<Table>> onTableDeque = new LinkedList();
        Iterator var6 = joins.iterator();

        while(true) {
            while(true) {
                while(var6.hasNext()) {
                    Join join = (Join)var6.next();
                    FromItem joinItem = join.getRightItem();
                    List<Table> joinTables = null;
                    if (joinItem instanceof Table) {
                        joinTables = new ArrayList();
                        ((List)joinTables).add((Table)joinItem);
                    } else if (joinItem instanceof SubJoin) {
                        joinTables = this.processSubJoin((SubJoin)joinItem);
                    }

                    if (joinTables != null) {
                        if (join.isSimple()) {
                            ((List)mainTables).addAll((Collection)joinTables);
                        } else {
                            Table joinTable = (Table)((List)joinTables).get(0);
                            List<Table> onTables = null;
                            if (join.isRight()) {
                                mainTable = joinTable;
                                if (leftTable != null) {
                                    onTables = Collections.singletonList(leftTable);
                                }
                            } else if (join.isLeft()) {
                                onTables = Collections.singletonList(joinTable);
                            } else if (join.isInner()) {
                                if (mainTable == null) {
                                    onTables = Collections.singletonList(joinTable);
                                } else {
                                    onTables = Arrays.asList(mainTable, joinTable);
                                }

                                mainTable = null;
                            }

                            mainTables = new ArrayList();
                            if (mainTable != null) {
                                ((List)mainTables).add(mainTable);
                            }

                            Collection<Expression> originOnExpressions = join.getOnExpressions();
                            LinkedList onExpressions;
                            if (originOnExpressions.size() == 1 && onTables != null) {
                                onExpressions = new LinkedList();
                                onExpressions.add(this.builderExpression((Expression)originOnExpressions.iterator().next(), onTables));
                                join.setOnExpressions(onExpressions);
                                leftTable = joinTable;
                            } else {
                                onTableDeque.push(onTables);
                                if (originOnExpressions.size() > 1) {
                                    onExpressions = new LinkedList();
                                    Iterator var14 = originOnExpressions.iterator();

                                    while(var14.hasNext()) {
                                        Expression originOnExpression = (Expression)var14.next();
                                        List<Table> currentTableList = (List)onTableDeque.poll();
                                        if (CollectionUtils.isEmpty(currentTableList)) {
                                            onExpressions.add(originOnExpression);
                                        } else {
                                            onExpressions.add(this.builderExpression(originOnExpression, currentTableList));
                                        }
                                    }

                                    join.setOnExpressions(onExpressions);
                                }

                                leftTable = joinTable;
                            }
                        }
                    } else {
                        this.processOtherFromItem(joinItem);
                        leftTable = null;
                    }
                }

                return (List)mainTables;
            }
        }
    }

    private List<Table> processSubJoin(SubJoin subJoin) {
        List<Table> mainTables = new ArrayList();
        if (subJoin.getJoinList() != null) {
            List<Table> list = this.processFromItem(subJoin.getLeft());
            ((List) mainTables).addAll(list);
            mainTables = processJoins((List) mainTables, subJoin.getJoinList());
        }

        return (List) mainTables;
    }

    @Override
    protected void processPlainSelect(PlainSelect plainSelect) {
        FromItem fromItem = plainSelect.getFromItem();
        Expression where = plainSelect.getWhere();
        processWhereSubSelect(where);
        if (fromItem instanceof Table) {
            Table fromTable = (Table) fromItem;
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
        log.debug("组装select的sql【{}】", plainSelect.toString());
    }

    protected void processJoin(Join join) {
        if (join.getRightItem() instanceof Table) {
            Table fromTable = (Table) join.getRightItem();
            if (this.tenantLineHandler.ignoreTable(fromTable.getName())) {
                return;
            }

            Collection<Expression> originOnExpressions = join.getOnExpressions();
            List<Expression> onExpressions = new LinkedList();
            onExpressions.add(this.builderExpression((Expression) originOnExpressions.iterator().next(), fromTable));
            join.setOnExpressions(onExpressions);
        }

    }

    private void processJoins(List<Join> joins) {
        //对于 on 表达式写在最后的 join，需要记录下前面多个 on 的表名
        Deque<Table> tables = new LinkedList<>();
        for (Join join : joins) {
            // 处理 on 表达式
            FromItem fromItem = join.getRightItem();
            if (fromItem instanceof Table) {
                Table fromTable = (Table) fromItem;
                // 获取 join 尾缀的 on 表达式列表
                Collection<Expression> originOnExpressions = join.getOnExpressions();
                // 正常 join on 表达式只有一个，立刻处理
                if (originOnExpressions.size() == 1) {
                    processJoin(join);
                    continue;
                }
                // 当前表是否忽略
                boolean needIgnore = tenantLineHandler.ignoreTable(fromTable.getName());
                // 表名压栈，忽略的表压入 null，以便后续不处理
                tables.push(needIgnore ? null : fromTable);
                // 尾缀多个 on 表达式的时候统一处理
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
                // 处理右边连接的子表达式
                processFromItem(fromItem);
            }
        }
    }

    @Override
    protected void processInsert(Insert insert, int index, String sql, Object obj) {
        //if (tenantLineHandler.ignoreTable(insert.getTable().getName())) {
        //    // 过滤退出执行
        //    return;
        //}
        //List<Column> columns = insert.getColumns();
        //if (CollectionUtils.isEmpty(columns)) {
        //    // 针对不给列名的insert 不处理
        //    return;
        //}
        //String tenantIdColumn = tenantLineHandler.getTenantIdColumn();
        //String envCodeColumn = tenantLineHandler.getEnvCodeColumn();
        //if (tenantLineHandler.ignoreInsert(columns, tenantIdColumn) || tenantLineHandler.ignoreInsert(columns,
        //    envCodeColumn)) {
        //    // 针对已给出租户列的insert 不处理
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
     * update 语句处理
     */
    @Override
    protected void processUpdate(Update update, int index, String sql, Object obj) {
        final Table table = update.getTable();
        if (tenantLineHandler.ignoreTable(table.getName())) {
            // 过滤退出执行
            return;
        }
        update.setWhere(this.andExpression(table, update.getWhere()));
        log.debug("组装update的sql【{}】", update.toString());
    }

    /**
     * delete 语句处理
     */
    @Override
    protected void processDelete(Delete delete, int index, String sql, Object obj) {
        if (tenantLineHandler.ignoreTable(delete.getTable().getName())) {
            // 过滤退出执行
            return;
        }
        delete.setWhere(this.andExpression(delete.getTable(), delete.getWhere()));
        log.debug("组装update的sql【{}】", delete.toString());
    }

    /**
     * 追加 SelectItem
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
        // 已经存在
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
            // t_tro_user 只有 tenant_id
            tenantExpression = new AndExpression(equalsTo, tenantIdCondition);
        } else if (envCodeCondition != null) {
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
