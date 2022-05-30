package io.shulie.takin.adapter.api.constant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FormulaSymbol {
    GREATER_THAN_OR_EQUAL_TO(20, ">=", "大于等于"),
    GREATER_THAN(21, ">", "大于"),
    EQUAL(0, "=", "等于"),
    LESS_THAN_OR_EQUAL_TO(10, "<=", "小于等于"),
    LESS_THAN(11, ">", "小于");

    @JsonValue
    private final Integer code;
    private final String symbol;
    private final String description;
    private static final Map<Integer, FormulaSymbol> ORDINAL_MAPPING = new HashMap<>(6);
    private static final Map<Integer, FormulaSymbol> CODE_MAPPING = new HashMap<>(6);

    @JsonCreator
    public static FormulaSymbol of(Integer code) {
        return CODE_MAPPING.get(code);
    }

    public static FormulaSymbol ofValue(Integer code) {
        return ORDINAL_MAPPING.get(code);
    }

    FormulaSymbol(Integer code, String symbol, String description) {
        this.code = code;
        this.symbol = symbol;
        this.description = description;
    }

    static {
        Arrays.stream(values()).forEach(t -> {
            ORDINAL_MAPPING.put(t.ordinal(), t);
            CODE_MAPPING.put(t.getCode(), t);
        });
    }

    public Integer getCode() {
        return code;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getDescription() {
        return description;
    }
}