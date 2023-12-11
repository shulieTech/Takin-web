package io.shulie.takin.cloud.data.model.mysql;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author zhangz
 * Created on 2023/12/11 22:02
 * Email: zz052831@163.com
 */

@MappedJdbcTypes(JdbcType.TINYINT)
@Slf4j
public class NumericBooleanTypeHandler extends BaseTypeHandler<Boolean> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
                                    Boolean parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter ? 1 : 0);
    }

    @Override
    public Boolean getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        int value = rs.getInt(columnName);
        return convertValue(value);
    }

    @Override
    public Boolean getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        int value = rs.getInt(columnIndex);
        return convertValue(value);
    }

    @Override
    public Boolean getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        int value = cs.getInt(columnIndex);
        return convertValue(value);
    }

    private Boolean convertValue(int value) {
        return value != 0;
    }

}
