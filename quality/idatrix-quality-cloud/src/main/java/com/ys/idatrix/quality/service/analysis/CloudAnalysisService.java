/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.service.analysis;

import com.ys.idatrix.quality.analysis.dto.NodeDictDataDto;
import com.ys.idatrix.quality.analysis.dto.NodeDictDto;
import com.ys.idatrix.quality.analysis.dto.NodeRecordDto;
import com.ys.idatrix.quality.analysis.dto.NodeResultDto;
import com.ys.idatrix.quality.dto.common.ReturnCodeDto;

import java.util.List;
import java.util.Map;

public interface CloudAnalysisService {
	
	/**
	 * 获取分析记录
	 * @param execId
	 * @return
	 * @throws Exception 
	 */
	public List<NodeRecordDto> getAnalysisRecords( String execId) throws Exception;
	/**
	 * 获取记录信息
	 * @param execId
	 * @param nodId
	 * @return
	 */
	public NodeRecordDto getAnalysisRecordsInfo(String execId,String nodId)  throws Exception;
	
	/**
	 * 获取记录信息
	 * @param uuid
	 * @return
	 */
	public NodeRecordDto getAnalysisRecordsInfo(String uuid)  throws Exception ;
	
	
	/**
	 * 获取记录的所有节点的结果信息 (合并域信息)
	 * @param execId 执行id
	 * @param isList 是否返回List, true:返回 该次执行的列表数据 , false:返回Map解析对象: {"nodeId":{"参考值1":NodeResultDto,"参考值2":NodeResultDto , ...}, ...}
	 * @return 两种类型 List 或者 Map
	 */
	public Object getAnalysisResult(String execId,boolean isList)  throws Exception ;
	/**
	 * 获取记录的某个节点的结果信息  (合并域信息)
	 * @param execId 执行id
	 * @param nodId 节点Id
	 * @param isList 是否返回List, true:返回 该次执行的该节点的列表数据 , false:返回Map解析对象: {"参考值1":NodeResultDto,"参考值2":NodeResultDto , ...}
	 * @return 两种类型 List 或者 Map
	 */
	public Object getAnalysisResult(String execId,String nodId,boolean isList)  throws Exception ;
	
	/**
	 * 获取记录的某个节点的某个参考值的信息  (合并域信息)
	 * @param execId 执行id
	 * @param nodId 节点Id
	 * @param referenceValue 参考值
	 * @return 
	 */
	public NodeResultDto getAnalysisResult(String execId,String nodId,String referenceValue)  throws Exception ;

	
	/**
	 * 获取冗余详情列表
	 * @param execId 执行id
	 * @param nodId 节点Id
	 * @param referenceValue 参考值
	 * @return 
	 */
	public Map<String,String> getRedundanceDetail(String execId,String nodId)  throws Exception ;
	
	/**
	 * 根据name获取字典对象
	 * @param name
	 * @return
	 */
    NodeDictDto findDataDictByName(String name) throws Exception;

	/**
	 * 修改数据字典
	 * @param dataDict
	 */
	void updateDataDict(NodeDictDto dataDict) throws Exception;

	/**
	 * 根据id获取字典信息
	 * @param id
	 * @return
	 */
    NodeDictDto findDictById(String id) throws Exception;


	/**
	 * 数据字典 获取数据条数
	 * @param value
	 * @param dictName
	 * @return
	 * @throws Exception
	 */
	Long dictFindCount(String dictName) throws Exception;

	/**
	 * 数据字典分页
	 * @param dictName
	 * @param page
	 * @param size
	 * @return
	 * @throws Exception
	 */
    List<NodeDictDto> dictFindPage(String dictName, Integer page, Integer size) throws Exception;

	/**
	 * 获取所有的字典信息，不带分页
	 * @return
	 * @throws Exception
	 */
	List<NodeDictDto> findDictListByStatus(Integer status) throws Exception;

	/**
	 * 判断当前字典名是否相同
	 * @param dictName
	 * @return
	 */
	Boolean isExistDictName(String dictName) throws Exception;

	/**
	 * 新增数据字典
	 * @param dataDict
	 * @throws Exception
	 */
	void addDataDict(NodeDictDto dataDict) throws Exception;

	/**
	 * 修改字典的生效状态
	 * @param dataDict
	 */
	void updateDictStatus(NodeDictDto dataDict) throws Exception;

	/**
	 * 字典数据，获取数据数量
	 * @param dictId
	 * @param value
	 * @return
	 */
    Long dictDataFindCount(String dictId, String value) throws Exception;

	/**
	 * 字典数据列表，带分页
	 * @param dictId
	 * @param value
	 * @param page
	 * @param size
	 * @return
	 */
	List<NodeDictDataDto> dictDataFindPage(String dictId, String value, Integer page, Integer size) throws Exception;

	/**
	 * 根据 字典id 和 标准值判断是否存在这样的一条数据
 	 * @param dictData
	 * @return
	 * @throws Exception
	 */
	Boolean isExistDictData(NodeDictDataDto dictData) throws Exception;

	/**
	 * 修改某条字典数据
	 * @param dictData
	 * @throws Exception
	 */
	void updateDictData(NodeDictDataDto dictData) throws Exception;

	/**
	 * 根据id获取字典数据的某条
	 * @param id
	 * @return
	 */
    NodeDictDataDto findDictDataById(Long id) throws Exception;

	/**
	 * 根据字典ID，查询字典下的所有数据，不带分页（标准值页面使用）
	 * @param dictId
	 * @return
	 */
	List<NodeDictDataDto> findDictDataListByDictId(String dictId) throws Exception;

	/**
	 * 新增字典数据 
	 * @param dictData
	 * @param isrefreshStatus 是否刷新字典状态到  待更新
	 * @throws Exception
	 */
    void insertDictData(NodeDictDataDto dictData,Boolean isrefreshStatus) throws Exception;
    
    /**
     * 通过数组对象 批量新增字典数据
     * @param dictId
     * @param rows 包含 数据标准值和参考值
     * @return key: errorMessage , successMessage ,successIds
     * @throws Exception
     */
    public Map<String,Object> insertBatchDictData(String dictId,  List<Object[]> rows) throws Exception;
    
    /**
     * 批量删除 字典数据
     * @param dictId
     * @param ids
     * @return
     * @throws Exception
     */
    public ReturnCodeDto deleteBatchDictData(String dictId,  Integer[] ids) throws Exception;

	/**
	 * 根据字典 id 获取出cs中所需要的值
	 * @param dictId
	 */
	String getDictDataString(String dictId ) throws Exception;
}