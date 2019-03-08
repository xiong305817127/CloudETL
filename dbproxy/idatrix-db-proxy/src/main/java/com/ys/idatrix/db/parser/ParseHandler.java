package com.ys.idatrix.db.parser;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName: ParseHandler
 * @Description:
 * @Author: ZhouJian
 * @Date: 2017/8/9
 */
public class ParseHandler {


    /**
     * Hive 和 HBase
     * 字段内容可用“'”或“"”
     * 判断是否多条SQL
     * ①获取非断句的分号符
     * 分号在输出字段或输入字段或条件语句中
     * e.g: select ';' from sb_app  或 select ";" from sb_app
     * insert into sb_app(name,code,secret) values('ab\\  // \ /;dd',"df;sdd",'ddddd');
     * <p>
     * ②找出断句分号
     */
    public static boolean validateMultiSql(String sql, int sqlLimit) {
        //非断句的分号符正则表达式
        String regEx = "('.*;.*'|\".*;.*\")";
        String[] arrays = sql.trim().split(regEx);
        for (String array : arrays) {
            String[] multiArray = array.split(";");
            if (multiArray.length > sqlLimit) {
                return true;
            }
        }
        return false;
    }


    /**
     * 添加 表 及 操作，同一个表多种操作以“|”隔开；同一个表的相同操作保留一个
     *
     * @param tbOpMap
     * @param newTable
     * @param newOperator
     */
    public static void pushTbOperatorToMap(Map<String, String> tbOpMap, String newTable, String newOperator) {
        if (tbOpMap.containsKey(newTable)) {
            String oldOperators = tbOpMap.get(newTable);
            String[] _oldOperators = oldOperators.split("\\|");
            boolean bContains = false;
            for (String oldOperator : _oldOperators) {
                if (newOperator.equals(oldOperator)) {
                    bContains = true;
                }
            }
            if (!bContains) {
                tbOpMap.put(newTable, oldOperators + "|" + newOperator);
            }
        } else {
            tbOpMap.put(newTable, newOperator);
        }
    }


    /**
     * clone set 返回对象
     *
     * @param set
     * @return
     */
    public static Set<String> cloneSet(Set<String> set) {
        Set<String> newSet = new HashSet<String>(set.size());
        if (CollectionUtils.isNotEmpty(set)) {
            for (String string : set) {
                newSet.add(string);
            }
        }
        return newSet;
    }

    /**
     * clone map 返回对象
     *
     * @param map
     * @return
     */
    public static Map<String, String> cloneMap(Map<String, String> map) {
        Map<String, String> newMap = new HashMap<>();
        if (MapUtils.isNotEmpty(map)) {
            for (String key : map.keySet()) {
                newMap.put(key, map.get(key));
            }
        }
        return newMap;

    }

}
