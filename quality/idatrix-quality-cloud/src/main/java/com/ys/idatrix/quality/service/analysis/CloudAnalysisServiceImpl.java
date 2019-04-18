/**
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.service.analysis;

import com.google.common.collect.Lists;
import com.ys.idatrix.quality.analysis.dao.NodeDictDao;
import com.ys.idatrix.quality.analysis.dao.NodeDictDataDao;
import com.ys.idatrix.quality.analysis.dao.NodeRecordDao;
import com.ys.idatrix.quality.analysis.dao.NodeResultDao;
import com.ys.idatrix.quality.analysis.dto.NodeDictDataDto;
import com.ys.idatrix.quality.analysis.dto.NodeDictDto;
import com.ys.idatrix.quality.analysis.dto.NodeRecordDto;
import com.ys.idatrix.quality.analysis.dto.NodeResultDto;
import com.ys.idatrix.quality.dto.common.ReturnCodeDto;
import com.ys.idatrix.quality.enums.AnalysisEnum;
import com.ys.idatrix.quality.ext.CloudSession;
import com.ys.idatrix.quality.ext.utils.FilePathUtil;
import com.ys.idatrix.quality.reference.agentproxy.EsAgentService;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.metrics2.sink.relocated.google.common.collect.Maps;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.vfs.KettleVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Service
public class CloudAnalysisServiceImpl implements CloudAnalysisService {

	@Value("dictPreKey:")
	private String dictPre;

	@Autowired
	private EsAgentService esAgentService;
	
	@Override
	public List<NodeRecordDto> getAnalysisRecords(String execId) throws Exception {
		return NodeRecordDao.getInstance().getRecordListByExecId(execId);
	}

	@Override
	public NodeRecordDto getAnalysisRecordsInfo(String execId, String nodeId) throws Exception {
		return NodeRecordDao.getInstance().getRecordInfo(execId, nodeId);
	}

	@Override
	public NodeRecordDto getAnalysisRecordsInfo(String uuid) throws Exception {
		return NodeRecordDao.getInstance().getRecordById(uuid);
	}

	@Override
	public Object getAnalysisResult(String execId, boolean isList) throws Exception {

		List<NodeResultDto> list = NodeResultDao.getInstance().getResultByExecId(execId);
		if (list != null && list.size() > 0) {
			if (isList) {
				return list;
			} else {
				// 解析list 为 {"nodeId":{"参考值1":NodeResultDto,"参考值2":NodeResultDto , ...}, ...}
				Map<String, Map<String, NodeResultDto>> result = Maps.newHashMap();
				Map<String, List<NodeResultDto>> nodeMapList = list.stream()
						.collect(Collectors.groupingBy(NodeResultDto::getNodId));
				for (Entry<String, List<NodeResultDto>> entry : nodeMapList.entrySet()) {
					Map<String, List<NodeResultDto>> referMap = entry.getValue().stream()
							.collect(Collectors.groupingBy(NodeResultDto::getReferenceValue));
					Map<String, NodeResultDto> nodeMap = referMap.entrySet().stream()
							.collect(Collectors.toMap(entryKey -> {
								return entryKey.getKey();
							}, entryValue -> {
								List<NodeResultDto> values = entryValue.getValue();
								if (values != null && values.size() > 0) {
									NodeResultDto v = values.get(0);
									if (values.size() > 1) {
										long total = values.stream().mapToLong(NodeResultDto::getNumber).sum();
										v.setNumber(total);
									}
									return v;
								}

								return null;
							}));
					result.put(entry.getKey(), nodeMap);
				}
				return result;
			}
		}
		return list;
	}

	@Override
	public Object getAnalysisResult(String execId, String nodId, boolean isList) throws Exception {

		List<NodeResultDto> list = NodeResultDao.getInstance().getResultList(execId, nodId);
		if (list != null && list.size() > 0) {
			if (isList) {
				return list;
			} else {
				// 解析list 为 {"参考值1":NodeResultDto,"参考值2":NodeResultDto , ...}
				Map<String, List<NodeResultDto>> referMap = list.stream()
						.collect(Collectors.groupingBy(NodeResultDto::getReferenceValue));
				Map<String, NodeResultDto> result = referMap.entrySet().stream().collect(Collectors.toMap(entry -> {
					return entry.getKey();
				}, entryValue -> {
					List<NodeResultDto> values = entryValue.getValue();
					if (values != null && values.size() > 0) {
						NodeResultDto v = values.get(0);
						if (values.size() > 1) {
							long total = values.stream().mapToLong(NodeResultDto::getNumber).sum();
							v.setNumber(total);
						}
						return v;
					}

					return null;
				}));
				return result;
			}
		}
		return list;
	}

	@Override
	public NodeResultDto getAnalysisResult(String execId, String nodId, String referenceValue) throws Exception {
		return NodeResultDao.getInstance().getResultReferenceTotal(execId, nodId, referenceValue);
	}
	
	@Override
	public Map<String,String> getRedundanceDetail(String execId, String nodId ) throws Exception {
		
		 Map<String,String>  map = Maps.newHashMap();
		 
		List<NodeResultDto> list = NodeResultDao.getInstance().getResultList(execId, nodId);
		if( list != null && list.size() > 0 ) {
			 Map<String, Object> result = list.get( 0 ).getOptional2();
			if(result != null  && result.containsKey("detailPath") ) {
				String  detailPath = (String) result.get("detailPath") ;
				if( FilePathUtil.fileIsExist(detailPath, false)) {
					InputStream in = null ;
					BufferedReader bf = null ;
					try {
						in = KettleVFS.getInputStream(detailPath) ;
						bf = new BufferedReader( new InputStreamReader(in) );
						String str;
						while ((str = bf.readLine()) != null) {
							if( Utils.isEmpty(str) || !str.contains(",")) {
								continue ;
							}
							int index = str.lastIndexOf(",");
							map.put(str.substring(0, index), str.substring(index+1).trim());
						}
					}finally {
						if( in != null ) {
							in.close();
						}
						if( bf != null ) {
							bf.close();
						}
					}
				}
			}
			
		}
		return map;
	}

	

	@Override
	public NodeDictDto findDictById(String id) throws Exception {
		return NodeDictDao.getInstance().getDictById(id);
	}

	@Override
	public NodeDictDto findDataDictByName(String name) throws Exception {
		NodeDictDto dictByName = NodeDictDao.getInstance().getDictByName(name);
		return dictByName;
	}

	public Long dictFindCount(String dictName) throws Exception {
		return NodeDictDao.getInstance().getDictCount(dictName);
	}

	public List<NodeDictDto> dictFindPage(String dictName, Integer page, Integer size) throws Exception {
		List<NodeDictDto> list = NodeDictDao.getInstance().getDictListBySearch(dictName, page, size);
		return list;
	}

	@Override
	public List<NodeDictDto> findDictListByStatus(Integer status) throws Exception {
		List<NodeDictDto> dictList = NodeDictDao.getInstance().getDictListByStatus(status);
		return dictList;
	}

	@Override
	public Boolean isExistDictName(String dictName) throws Exception {
		return NodeDictDao.getInstance().isExistDictName(dictName);
	}

	@Override
	public void addDataDict(NodeDictDto dict) throws Exception {

		// 先判断是否已经存在当前字典名
		if (isExistDictName(dict.getDictName())) {
			throw new Exception("当前字典名[" + dict.getDictName() + "]已经存在.");
		}
		NodeDictDao.getInstance().insertDict(dict);

		// es新建索引
		esAgentService.dictCreateIndex(CloudSession.getLoginUser(),  CloudSession.getLoginRenterId(), Lists.newArrayList(dict.getId()));
	}

	@Override
	public void updateDataDict(NodeDictDto dictParam) throws Exception {
		if (!isAllowOperation(dictParam)) {
			throw new Exception("没权限,请检查您是否有权限操作.");
		}
		NodeDictDao.getInstance().updateDict(dictParam);
	}

	@Override
	public Long dictDataFindCount(String dictId, String value) throws Exception {
		return NodeDictDataDao.getInstance().getDictDataCount(dictId, value);
	}

	@Override
	public List<NodeDictDataDto> dictDataFindPage(String dictId, String value, Integer page, Integer size)
			throws Exception {
		return NodeDictDataDao.getInstance().getDictDataListBySearch(dictId, value, page, size);
	}

	@Override
	public Boolean isExistDictData(NodeDictDataDto dictData) throws Exception {
		return NodeDictDataDao.getInstance().isExistDictData(dictData);
	}

	@Override
	public void updateDictData(NodeDictDataDto dictData) throws Exception {
		if (dictData == null || StringUtils.isEmpty(dictData.getDictId()) || dictData.getId() == null
				|| StringUtils.isEmpty(dictData.getStdVal1())) {
			throw new Exception("必要的参数(字典ID,字典数据ID,数据标准值)不能为空.");
		}

		NodeDictDto nodeDict = NodeDictDao.getInstance().getDictById(dictData.getDictId());
		if (nodeDict == null) {
			throw new Exception("未找到当前字典[" + dictData.getDictId() + "]信息");
		}
		if (!isAllowOperation(nodeDict)) {
			throw new Exception("没权限,请检查您是否有权限操作.");
		}

		// 判断当前字典下是否有此标准值了
		if (isExistDictData(dictData)) {
			throw new Exception("当前字典[" + dictData.getDictId() + "]已存在此标准值[" + dictData.getStdVal1() + "]了.");
		}

		String[] vals = dictData.getAllValue();
		if (vals != null && vals.length > 0) {
			if (vals.length != Arrays.asList(vals).stream().distinct().count()) {
				throw new Exception("当前数据中参考值有重复,请检查.");
			}
			// 判断参考值是否有重复
			boolean isRepeat = NodeDictDataDao.getInstance().isRepeatDictData(dictData.getDictId(), vals,
					dictData.getId());
			if (isRepeat) {
				throw new Exception("当前字典中另外的标准值和当前标准值 有相同的参考值,请检查.");
			}

		}
		// 修改字典数据
		NodeDictDataDao.getInstance().updateDictData(dictData);
		updateDictToModify(nodeDict);
	}

	// 更新字典数据时，要同步字典状态，（如果是生效状态需要改成待更新）
	public void updateDictToModify(NodeDictDto dict) throws Exception {
		// 如果字典状态为已生效状态，应该修改成待更新状态
		if (dict.getStatus().intValue() == AnalysisEnum.ACTIVE.getCode().intValue()) {
			dict.setStatus(2l);
		}
		dict.setUpdateTime(new Date());
		dict.setModifier(CloudSession.getLoginUser());
		NodeDictDao.getInstance().updateDict(dict);
	}

	@Override
	public NodeDictDataDto findDictDataById(Long id) throws Exception {
		return NodeDictDataDao.getInstance().getDictDataById(id);
	}

	@Override
	public List<NodeDictDataDto> findDictDataListByDictId(String dictId) throws Exception {
		return NodeDictDataDao.getInstance().findDictDataListByDictId(dictId);
	}

	@Override
	public void insertDictData(NodeDictDataDto dictData,Boolean isrefreshStatus) throws Exception {

		if (dictData == null || StringUtils.isEmpty(dictData.getDictId())
				|| StringUtils.isEmpty(dictData.getStdVal1())) {
			throw new Exception("必要的参数(字典ID,数据标准值)不能为空.");
		}

		String[] vals = dictData.getAllValue();
		if (vals != null && vals.length > 0) {
			if (vals.length != Arrays.asList(vals).stream().distinct().count()) {
				throw new Exception("当前数据[标准值:"+dictData.getStdVal1()+"]中参考值有重复,请检查.");
			}
			// 判断参考值是否有重复
			boolean isRepeat = NodeDictDataDao.getInstance().isRepeatDictData(dictData.getDictId(), vals,dictData.getId());
			if (isRepeat) {
				throw new Exception("当前字典中另外的标准值和当前标准值 有相同的参考值,请检查.");
			}
		}

		NodeDictDataDao.getInstance().insertDictData(dictData);
		if( isrefreshStatus == null || isrefreshStatus ) {
			NodeDictDto nodeDict = NodeDictDao.getInstance().getDictById(dictData.getDictId());
			if (nodeDict == null) {
				throw new Exception("未找到当前字典[" + dictData.getDictId() + "]信息");
			}
			updateDictToModify(nodeDict);
		}
		
	}

	@Override
	public  Map<String,Object>  insertBatchDictData(String dictId, List<Object[]> data) throws Exception {
		if (Utils.isEmpty(dictId)) {
			throw new Exception("字典ID为空.");
		}
		if (data == null || data.isEmpty()) {
			return null;
		}
		
		NodeDictDto nodeDict = NodeDictDao.getInstance().getDictById(dictId);
		if (nodeDict == null) {
			throw new Exception("未找到当前字典[" + dictId + "]信息");
		}
		if (!isAllowOperation(nodeDict)) {
			throw new Exception("没权限,请检查您是否有权限操作.");
		}

		Map<String, Object> result = Maps.newHashMap();
		List<Long>  successIds = Lists.newArrayList() ;
		
		Map<String, String> errorMessae =  Maps.newLinkedHashMap();
		int successNum = 0;
		int errorNum = 0;

		try {
			//设置提交数量,暂时不使用,因为判断重复使用了数据库sql方法,使用批量提交,造成数据未提交,sql方式的重复判断会失效
			//DatabaseHelper.setCommitSize(1000);
			
			// 从第二行开始,第一行是头
			for (int i = 1; i < data.size(); i++) {
				Object[] rows = data.get(i);
				if( rows == null || rows.length < 1 || rows[0] == null || Utils.isEmpty(rows[0].toString())) {
					continue ;
				}
				
				try {

					NodeDictDataDto dictData = new NodeDictDataDto();
					dictData.setDictId(dictId);// 字典id
					dictData.setStdVal1(rows[0].toString());// 标准值

					Object[] references = Arrays.copyOfRange(rows, 1, rows.length);
					dictData.setValueArr(StringUtils.join(references, ","));

					insertDictData(dictData,false);
					successIds.add( dictData.getId() );

					successNum++;
				} catch (Exception e) {
					String key = e.getMessage();
					if (!Utils.isEmpty(key)) {
						if (errorMessae.keySet().contains(key)) {
							String value = errorMessae.get(key);
							errorMessae.put(key, value + "," + (i + 1));
						} else {
							errorMessae.put(key, (i + 1) + "");
						}
					}
					errorNum++;
				}
			}
			
			//更新状态到 未更新 
			updateDictToModify(nodeDict);
			
			result.put("successIds", successIds);
			result.put("successMessage", "成功处理 "+successNum+" 行,失败处理 "+errorNum+" 行.");
			
			String error = "";
			for( String key : errorMessae.keySet()) {
				error += "第" + errorMessae.get(key) + "条数据错误,"+key+";\n" ;
			}
			result.put("errorMessage", error);
			return result;
			
		}finally {
			//因为判断重复使用了数据库sql方法,使用批量提交,造成数据未提交,sql方式的重复判断会失效
			//DatabaseHelper.closeBatchCommit();
		}
	}
	
	@Override
	public ReturnCodeDto deleteBatchDictData(String dictId, Integer[] ids) throws Exception {
		if( Utils.isEmpty(dictId) || Utils.isEmpty(ids)  ) {
			return new ReturnCodeDto(0);
		}
		for(Integer id : ids) {
			NodeDictDataDao.getInstance().deleteDictData(dictId, id.longValue());
		}
		return new ReturnCodeDto(0);
	}


	@Override
	public String getDictDataString(String dictId) throws Exception {
		if (dictId == null ) {
			return null;
		}
		// 需要更新
		List<NodeDictDataDto> dictDataList = NodeDictDataDao.getInstance().findDictDataListByDictId(dictId);
		StringBuffer value = new StringBuffer();
		if( dictDataList != null && dictDataList.size() >0 ) {
			for (int i = 0; i < dictDataList.size(); i++) {
				NodeDictDataDto dictData = dictDataList.get(i);
				value.append(StringUtils.join(dictData.getAllValue(), ","));
				if (i != dictDataList.size() - 1) {
					value.append("\n");
				}
			}
		}
		return value.toString();
	}

	@Override
	public void updateDictStatus(NodeDictDto dict) throws Exception {
		// 先查询
		NodeDictDto nodeDict = NodeDictDao.getInstance().getDictById(dict.getId());
		if (nodeDict == null) {
			throw new Exception("未找到当前字典数据[" + dict.getId() + "]信息");
		}
		if (!isAllowOperation(nodeDict)) {
			throw new Exception("没权限,请检查您是否有权限操作.");
		}

		nodeDict.setModifier(CloudSession.getLoginUser());
		nodeDict.setUpdateTime(new Date());

		// 设置状态
		if (dict.getStatus().intValue() == AnalysisEnum.ACTIVE.getCode().intValue()) {// 生效
			nodeDict.setStatus(AnalysisEnum.NOT_ACTIVE.getCode().longValue());
		} else {// 未生效 或 待更新
			nodeDict.setStatus(AnalysisEnum.ACTIVE.getCode().longValue());
		}

		// 判断当前状态是否生效的，修改最后的生效时间
		if (nodeDict.getStatus().intValue() == AnalysisEnum.ACTIVE.getCode().intValue()) {
			nodeDict.setActiveTime(new Date());
		} else {
			// 当前是不生效 或 待更新，生效时间制空
			nodeDict.setActiveTime(null);
		}
		NodeDictDao.getInstance().updateDictActiveStatus(nodeDict);

		// 判断当前状态是否为生效，如果是生效则需要去删除ES索引重新创建
		if (nodeDict.getStatus().intValue() == AnalysisEnum.ACTIVE.getCode().intValue()) {

			esAgentService.dictCreateIndex(CloudSession.getLoginUser(),  CloudSession.getLoginRenterId(), Lists.newArrayList( nodeDict.getId() ));
		}
	}
	
	
	private boolean isAllowOperation(NodeDictDto dict) throws Exception {
		
		if( Utils.isEmpty(dict.getCreator())) {
			NodeDictDto nodeDict = NodeDictDao.getInstance().getDictById(dict.getId());
			if (nodeDict == null) {
				throw new Exception("未找到当前字典数据[" + dict.getId() + "]信息");
			}
			dict = nodeDict ;
		}
		
		if ( dict.isShare() ) {
			if("root".equalsIgnoreCase(CloudSession.getLoginUser())) {
				//root有权限
				return true ;
			}
			if( dict.getRenterId()!= null && dict.getRenterId().equals(CloudSession.getLoginRenterId()) ) {
				//租户Id相同
				return true ;
			}
		} else if ( !dict.isShare() && dict.getRenterId().equals(CloudSession.getLoginRenterId())){
			// 相同租户才能修改
			return true;
		}
		return false;
	}

	

}
