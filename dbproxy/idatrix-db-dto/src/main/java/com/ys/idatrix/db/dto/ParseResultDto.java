package com.ys.idatrix.db.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Map;


/**
 * @ClassName: ParseResultDto
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ParseResultDto {

    /**
     * sql语句中的限制记录条数（主sql）
     */
    private int actualRowLimit = -1;

    /**
     * sql操作类型
     */
    private String mainOperator = null;

    /**
     * 表操作。存储格式：key：table_name，value：insert|select
      */
    private Map<String,String> tabOperators = null;

    /**
     * 非表操作。如：HBase中的schema、function、sequence。暂时保存，目前版本忽略此操作。存储格式：key：schema，value：create|use
     */
    private Map<String,String> unTabOperators = null;


    public ParseResultDto() {
    }

    public ParseResultDto(int actualRowLimit, Map<String,String> tabOperators) {
        this.actualRowLimit = actualRowLimit;
        this.tabOperators = tabOperators;
    }

    public ParseResultDto(int actualRowLimit, String mainOperator, Map<String,String> tabOperators) {
        this.actualRowLimit = actualRowLimit;
        this.mainOperator = mainOperator;
        this.tabOperators = tabOperators;
    }

    public ParseResultDto(int actualRowLimit, Map<String,String> tabOperators, Map<String, String> unTabOperators) {
        this.actualRowLimit = actualRowLimit;
        this.tabOperators = tabOperators;
        this.unTabOperators = unTabOperators;
    }

    public ParseResultDto(int actualRowLimit, String mainOperator, Map<String,String> tabOperators, Map<String, String> unTabOperators) {
        this.actualRowLimit = actualRowLimit;
        this.mainOperator = mainOperator;
        this.tabOperators = tabOperators;
        this.unTabOperators = unTabOperators;
    }

}
