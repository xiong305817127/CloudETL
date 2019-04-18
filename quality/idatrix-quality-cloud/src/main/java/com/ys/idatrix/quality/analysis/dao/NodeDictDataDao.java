package com.ys.idatrix.quality.analysis.dao;

import com.google.common.collect.Lists;
import com.ys.idatrix.quality.analysis.dto.NodeDictDataDto;
import com.ys.idatrix.quality.ext.utils.DatabaseHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.util.Utils;

import java.util.List;

/**
 * @ClassName NodeDictDataDao
 * @Description TODO
 * @Author ouyang
 * @Date 2018/10/15 14:16
 * @Version 1.0
 */
public class NodeDictDataDao {

	public static final Log logger = LogFactory.getLog("NodeDictDataDao");

	private static final Class<NodeDictDataDto> Table_Class = NodeDictDataDto.class;

	private static boolean isDatabaseinit = false;
	private static NodeDictDataDao instance;

	public static void init() throws Exception {
		if (!isDatabaseinit) {

			List<NodeDictDataDto> initData = Lists.newArrayList(
					new NodeDictDataDto(1L, "", "Card18", "Card15", null, null, null, null, null, null, null, null),
					new NodeDictDataDto(2L, "", "NNNNNNNNNNN", "NNNNNNN", "NNNN-NNNNNNN", "NNN-NNNNNNNN", "+86NNNNNNNNNNN", "0086-NNNNNNNNNNN", "NNNNN", "NNN", null, null),
					new NodeDictDataDto(3L, "", "yyyyMMdd", "yyyy-MM-dd", "yyyy/MM/dd", "yyyy年MM月dd日", "yyyyMMdd HH:mm:ss", "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss", "yyyy年MM月dd日 HH时mm分ss秒","HH:mm:ss", "HH时mm分ss秒"));

			DatabaseHelper.createOrUpdateTableifNonexist(Table_Class,  initData);
			isDatabaseinit = true;
		}
	}

	public NodeDictDataDao() {
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static NodeDictDataDao getInstance() {
		if (instance == null) {
			synchronized (Table_Class) {
				if (instance == null) {
					instance = new NodeDictDataDao();
				}
			}
		}
		return instance;
	}

	public NodeDictDataDto getDictDataById(Long id) throws Exception {
		return  DatabaseHelper.queryFirst(Table_Class, Table_Class, null, new String[] {"id"}, new String[] {"="}, new Object[] {id}, null );
	}

	public void insertDictData(NodeDictDataDto dictData) throws Exception {
		if (dictData == null || StringUtils.isEmpty(dictData.getDictId()) || StringUtils.isEmpty(dictData.getStdVal1()) ) {
			throw new Exception("必要的参数[字典ID,标准值]不能为空.");
		}
		DatabaseHelper.insert(Table_Class,  dictData);
	}

	public void updateDictData(NodeDictDataDto dictData) throws Exception {
		DatabaseHelper.update(Table_Class,  dictData, "ID");
	}

	public Boolean isExistDictData(NodeDictDataDto dictData) throws Exception {
		if (dictData == null || StringUtils.isEmpty(dictData.getDictId()) || StringUtils.isEmpty(dictData.getStdVal1())) {
			throw new Exception("id和标准值不能为空.");
		}
		StringBuffer sql = new StringBuffer();
		sql.append( "select count(*) from " ).append( DatabaseHelper.getTableName(Table_Class)   ).append( " where dictId='"  ).append(dictData.getDictId() ).append( "' and stdVal1='").append( dictData.getStdVal1()  ).append( "'" );
		if (dictData.getId() != null) {
		  sql.append( " and id != "  ).append( dictData.getId() );
		}
		Long count = DatabaseHelper.queryFirst(Long.class, sql.toString() );
		if (count != null && count > 0) {
			return true;
		}
		return false;
	}

	public Boolean isRepeatDictData(String dictId, String[] simVals, Long id) throws Exception {
		if (Utils.isEmpty(dictId) || Utils.isEmpty(simVals)) {
			throw new Exception("判断重复的 字典id和标准值列表不能为空.");
		}

		String inVal = "\"" + String.join("\",\"", simVals) + "\"";
		
		StringBuffer sql = new StringBuffer();
		sql.append( " SELECT count(*) FROM " ).append( DatabaseHelper.getTableName(Table_Class)   ).append( "  where dictId='"  ).append( dictId  ).append( "'  ");
		if (id != null) {
			sql.append( " and id != "  ).append( id);
		}
		sql.append( "   and   ( ");
		sql.append(" stdVal1 in (" ).append( inVal ).append( ") or ");
		sql.append(" simVal2 in (" ).append( inVal ).append( ") or ");
		sql.append(" simVal3 in (" ).append( inVal ).append( ") or ");
		sql.append(" simVal4 in (" ).append( inVal ).append( ") or ");
		sql.append(" simVal5 in (" ).append( inVal ).append( ") or ");
		sql.append(" simVal6 in (" ).append( inVal ).append( ") or ");
		sql.append(" simVal7 in (" ).append( inVal ).append( ") or ");
		sql.append(" simVal8 in (" ).append( inVal ).append( ") or ");
		sql.append(" simVal9 in (" ).append( inVal ).append( ") or ");
		sql.append(" simVal10 in (" ).append( inVal ).append( ") ") ;
		sql.append( " ) ");
		sql.append( "  GROUP BY dictId " );

		Long count = DatabaseHelper.queryFirst(Long.class, sql.toString());
		if (count != null && count > 0) {
			return true;
		}
		return false;
	}

	public List<NodeDictDataDto> getDictDataListBySearch(String dictId, String stdVal1, Integer page, Integer size)
			throws Exception {
		String sql = "select * from " + DatabaseHelper.getTableName(Table_Class) ;
		String whereSql = whereSql(dictId, stdVal1);
		sql = sql + whereSql + " order by ID " ;
		if (page > 0) {
			sql = sql + " limit " + (page - 1) * size + ", " + size + ";";
		}
		List<NodeDictDataDto> list = DatabaseHelper.queryList(Table_Class, sql);
		return list;
	}

	public Long getDictDataCount(String dictId, String stdVal1) throws Exception {
		String sql = "select count(*) from " + DatabaseHelper.getTableName(Table_Class) ;
		String whereSql = whereSql(dictId, stdVal1);
		sql = sql + whereSql;
		Long count = DatabaseHelper.queryFirst(Long.class, sql);
		return count == null ? 0L : count;
	}

	public String whereSql(String dictId, String stdVal1) {
		String whereSql = " where 1=1";
		if (StringUtils.isNotEmpty(dictId)) {
			whereSql += " and dictId = '" + dictId + "'";
		}
		if (StringUtils.isNotEmpty(stdVal1)) {
			whereSql += " and stdVal1 like '%" + stdVal1 + "%'";
		}
		return whereSql;
	}

	public List<NodeDictDataDto> findDictDataListByDictId(String dictId) throws Exception {
		String sql = "select * from " + DatabaseHelper.getTableName(Table_Class)  + " where dictId='" + dictId + "' order by ID ";
		return DatabaseHelper.queryList(Table_Class, sql);
	}
	
	
	public void deleteDictData(String dictId, Long id) throws Exception {
		DatabaseHelper.delete(Table_Class, new String[] {"id","dictId"}, new String[] {"="}, new Object[] {id,dictId});
	}
}
