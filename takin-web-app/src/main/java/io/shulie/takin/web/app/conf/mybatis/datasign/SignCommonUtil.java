package io.shulie.takin.web.app.conf.mybatis.datasign;

import io.shulie.takin.utils.security.MD5Utils;
import io.shulie.takin.web.data.annocation.EnableSign;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.insert.Insert;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: 南风
 * @Date: 2022/3/2 2:52 下午
 */
@Slf4j
@Component
public class SignCommonUtil {

    public static String SIGN_FIELD = "sign";


    public static boolean checkSignData(Class<?> clz, List<?> result) throws Exception {

        SignFieldCache signFieldCache = new SignFieldCache();

        List<Field> signFields = signFieldCache.getSignFields(clz);
        Map<String, String> signMap = new LinkedHashMap<>();

        String oldSign = "";
        boolean check = true;
        for (int i = 0; i < result.size(); i++) {
            for (Field field : signFields) {
                if (SIGN_FIELD.equals(field.getName())) {
                    if (field.get(result.get(i)) == null) {
                        oldSign = null;
                        break;
                    }else if(Objects.equals(field.getName(), "")){
                        oldSign = null;
                        break;
                    } else {
                        oldSign = String.valueOf(field.get(result.get(i)));
                    }
                } else {
                    signMap.put(field.getName(), String.valueOf(field.get(result.get(i))));
                }
            }

            //历史数据没有签名,放行
            if (oldSign == null) {
                continue;
            }

            //验签
            String newSign = MD5Utils.getInstance().getMD5(signMap.toString());

            if (!oldSign.equals(newSign)) {
                log.error("【sign operation fail】select SQL 第【{}】行数据 签名验证失败", i + 1);
                check = false;
                break;
            }
        }
        return check;
    }

    public static List<Field> getSignFields(Class<?> clz) {
        SignFieldCache signFieldCache = new SignFieldCache();
        return signFieldCache.getSignFields(clz);
    }

    public static String buildSign(Object et, Class<?> clz, List<?> result) throws Exception {
        //获取更新前表数据中的签名key-value
        Map<String, String> signFieldData = getSignFieldData(clz, result);
        List<Field> signFields = SignCommonUtil.getSignFields(clz);
        Map<String, String> signMap = new LinkedHashMap<>();
        for (Field field : signFields) {
            if (!SIGN_FIELD.equals(field.getName())) {
                if (field.get(et) == null) {
                    //补齐缺失的签名参数
                    signMap.put(field.getName(), signFieldData.get(field.getName()));
                } else {
                    signMap.put(field.getName(), field.get(et).toString());
                }
            }
        }
        if(signMap.isEmpty()){
            return "";
        }
        return MD5Utils.getInstance().getMD5(signMap.toString());
    }

    public static Map<String, String> getSignFieldData(Class<?> clz, List<?> result) throws IllegalAccessException {
        SignFieldCache signFieldCache = new SignFieldCache();
        List<Field> signFields = signFieldCache.getSignFields(clz);
        Map<String, String> signMap = new LinkedHashMap<>();

        for (Field field : signFields) {
            if (!SIGN_FIELD.equals(field.getName())) {
                signMap.put(field.getName(), String.valueOf(field.get(result.get(0))));
            }
        }
        return signMap;
    }

    public void setSign(MappedStatement mappedStatement, Object parameterObject, Statement statement, BoundSql boundSql) throws IllegalAccessException, SQLException, JSQLParserException {


        Class<?> clz = mappedStatement.getParameterMap().getType();
        boolean isSign = clz.isAnnotationPresent(EnableSign.class);
        if (isSign) {

            if (SqlCommandType.INSERT.equals(mappedStatement.getSqlCommandType())) {
                //新增方法获取id
                Field[] fields = clz.getDeclaredFields();
                Long id = null;
                for (Field field : fields) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }

                    if (field.getName().equals("id")) {
                        id = Long.valueOf(field.get(parameterObject).toString());
                    }
                }
                Insert insert = (Insert) CCJSqlParserUtil.parse(boundSql.getSql());
                String tableName = insert.getTable().getName();
                String whereStr = " where id = " + id;
                String querySql = "select * from " + tableName + whereStr;


                List<?> list = new ArrayList();

                ResultSet rs = statement.executeQuery(querySql);
                ResultSetMetaData md = rs.getMetaData();
                int count = md.getColumnCount();
                while (rs.next()) {
                    int anInt = rs.getInt(1);

                }
//            if (result.isEmpty()) {
//                log.error("【sign operation fail】update SQL 签名验证失败,查不到更新的数据");
//                throw new TakinWebException(TakinWebExceptionEnum.DATA_SIGN_ERROR, "update SQL 签名验证失败");
//            }
//
//            if (result.size() > 1) {
//                log.error("【sign operation fail】update SQL 签名验证失败,查不到更新的数据");
//                throw new TakinWebException(TakinWebExceptionEnum.DATA_SIGN_ERROR, "update SQL 签名验证失败");
//            }
//
//            //计算签名后再更新回去
//            Map<String, String> signMap = new HashMap<>();
//            for (Field field : fields) {
//                if (!SIGN_FIELD.equals(field.getName())) {
//                    signMap.put(field.getName(), String.valueOf(field.get(result.get(0))));
//                }
//            }
//            String sign = MD5Utils.getInstance().getMD5(MapUtil.sort(signMap).toString());
//
//            String updateSql = "update "+tableName+"  SET sign = "+sign+ " where id = "+id;
//            int update = statement.executeUpdate(updateSql);
//            if(update<1){
//                log.error("【sign operation fail】update SQL 签名验证失败,查不到更新的数据");
//                throw new TakinWebException(TakinWebExceptionEnum.DATA_SIGN_ERROR, "update SQL 签名验证失败");
//            }

                return;
            }

            if (SqlCommandType.UPDATE.equals(mappedStatement.getSqlCommandType())) {
                return;
            }

        }


    }

}
