/**
 * 质量分析所有services接口
 * @author pwj 2018/9/27
 */

import request from "utils/request";
import { API_BASE_QUALITY } from "../constants";
import qs from "qs";

const url = API_BASE_QUALITY;
const runTrans = {
  // 获取数据质量报告
  getRecordList: "/analysis/getRecordList",
  getRecordInfo: "/analysis/getRecordInfo",
  getResultInfo: "/analysis/getResultInfo",

  // 根据单个节点获取报告
  getAnalysisReportByNode: "/analysis/getResultInfo",

  // 获取字典信息
  // 1. 电话 2. 日期 3. 身份证
  getDictionary: "/analysis/dictData/{id}.do", //调用时替换id
  getDictionaryList: "/analysis/dictAllList.do", //获取全部字典
  getDictionaryAll: "/analysis/dictDataAllList/{id}.do"
};

//新建页面新增 保存接口
export async function Getupdate(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  };
  return request(`${API_BASE_QUALITY}/analysis/dict/update.do?`, option);
}
//新建页面新增 保存接口
export async function GetdictDatastatus(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  };
  return request(`${API_BASE_QUALITY}/analysis/dict/status.do?`, option);
}

//新建页面新增 保存接口
export async function GetdictData(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  };
  return request(`${API_BASE_QUALITY}/analysis/dictData/update.do?`, option);
}
//编辑标准值页面查询
export async function GetdictAllList(query, obj) {
  return request(
    `${API_BASE_QUALITY}/analysis/dictDataList/${query.name}.do?` +
      "page=" +
      query.page,
    "size=" + query.size
  );
}

//新建页面新增 保存接口
export async function GetSibmitdictNew(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  };
  return request(`${API_BASE_QUALITY}/analysis/dict.do?`, option);
}

/*获取数据字典查询  alisa修改*/
export async function dataDictList(query) {
  const queryString = qs.stringify(query);

  return request(`${API_BASE_QUALITY}/analysis/dictList.do?${queryString}`);
}

/*获取数据字典编辑  alisa修改*/
export async function getdataDict(query) {
  return requests(`${API_BASE_QUALITY}/analysis/dataDict/${query.name}.do`);
}

//新建页面新增 按id查询，获取复制字典名称的下拉列表
export async function getdictNewlist(query) {
  console.log(query, "query======");
  return requests(`${API_BASE_QUALITY}/analysis/dict/${query.id}.do`);
}
/*获取数据字典保存   alisa修改*/
export async function postdataDict(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  };
  return request(`${API_BASE_QUALITY}/analysis/dataDict/update.do?`, option);
}
/*获取数据字典导出   alisa修改*/
export async function analysisCsvFile(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  };
  return request(`${API_BASE_QUALITY}/analysis/analysisCsvFile.do?${option}`);
}

/*获取数据字典导出   alisa修改*/
export async function GetdictNew(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  };
  return request(`${API_BASE_QUALITY}/analysis/dict.do?${option}`);
}
/*数据字典 编辑页面删除  alisa修改*/
export async function deletedictData(query) {
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_QUALITY}/analysis/deletedictData.do?`, option);
}

/**
 * 查询字典
 * 注意替换接口地址中的{id}
 *
 */
export async function getDictionary(obj) {
  // 替换getDictionary
  const requestUrl =
    obj.id === -1
      ? url + runTrans.getDictionaryList
      : obj.all
      ? url + runTrans.getDictionaryAll.replace("{id}", obj.id)
      : url + runTrans.getDictionary.replace("{id}", obj.id);
  return request(requestUrl);
}

/**
 * 获取质量报告的三种节点
 * @param {*} obj
 */
export async function getAnalysisReports(obj, n) {
  let node = "";

  // 使用同一个方法转发三种节点的数据
  switch (n) {
    case "List":
      node = runTrans.getRecordList;
      break;
    case "Record":
      node = runTrans.getRecordInfo;
      break;
    case "Result":
      node = runTrans.getResultInfo;
      break;
    default:
      return;
  }

  const urlQuery = url + node + "?" + qs.stringify(obj);

  return request(urlQuery);
}

/**
 * 获取单个node的报告信息
 * @param {*} obj
 */

export async function getAnalysisReportsByNode(obj) {
  const option = {
    method: "GET",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include"
  };

  const urlQuery =
    url + runTrans.getAnalysisReportByNode + "?" + qs.stringify(obj);
  return request(urlQuery, option);
}

/**
 * 获取质量分析列表
 * @param {Object} query
 */
export async function getTransList(query) {
  const queryString = qs.stringify(query);

  console.log(query, queryString);
  return request(`${API_BASE_QUALITY}/cloud/getTransList.do?${queryString}`);
}

/*执行引擎列表*/
export async function getDefaultEngineList(query) {
  const queryString = qs.stringify(query);
  return request(
    `${API_BASE_QUALITY}/cloud/getDefaultEngineList.do?${queryString}`
  );
}

/**
 * 获取执行属性
 * @param {String} name
 */
export async function editTransAttributes(query) {
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_QUALITY}/trans/editTransAttributes.do`, option);
}

/**
 * 批量执行
 * @param {Object} query
 */
export async function execBatchTrans(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  };
  return request(`${API_BASE_QUALITY}/trans/execBatchTrans.do`, option);
}

/**
 * 检查名字是否存在
 */
export async function checkName(name) {
  const option = {
    method: "POST",
    body: JSON.stringify({ name })
  };
  return request(`${API_BASE_QUALITY}/trans/checkTransName.do`, option);
}

/**
 * 保存分析属性
 * @param {Object} query
 */
export async function saveTransAttributes(query) {
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_QUALITY}/trans/saveTransAttributes.do`, option);
}

/**
 * 新建分析
 * @param {Object} query
 */
export async function newTrans(query) {
  // name: name.info_name,
  // newName: name.copy_name?name.copy_name:"",
  // description: name.description
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_QUALITY}/trans/newTrans.do`, option);
}

/**
 * 删除分析
 * @param {Object} name
 */
export async function getDelete_trans(query) {
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_QUALITY}/trans/deleteTrans.do`, option);
}

/**
 * 获取分析列表名
 * @param {Object} obj
 */
export async function getTrans_list(query) {
  const queryString = qs.stringify(query);
  return request(`${API_BASE_QUALITY}/cloud/getTransList.do?${queryString}`);
}

/*打开分析名称*/
export async function getOpen_trans(query) {
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_QUALITY}/trans/openTrans.do`, option);
}

/*添加step*/
export async function add_step(query) {
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_QUALITY}/step/addStep.do`, option);
}

/*移动step*/
export async function move_step(query) {
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_QUALITY}/step/moveStep.do`, option);
}

/*删除step*/
export async function delete_step(query) {
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_QUALITY}/step/deleteStep.do`, option);
}

/*编辑step*/
export async function edit_step(query) {
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_QUALITY}/step/editStep.do`, option);
}

/*获取Web查询操作*/
export async function getWebUrl(query) {
  const queryString = qs.stringify(query);
  return request(`${API_BASE_QUALITY}/ws/getOperations.do?${queryString}`);
}

/*获取getProcList列表*/
export async function get_ProcList(obj) {
  const queryString = qs.stringify(query);
  return request(`${API_BASE_QUALITY}/db/getProc.do?${queryString}`);
}

/*数据库连接*/
export async function getDb_list2(query) {
  const queryString = qs.stringify(query);
  return request(`${API_BASE_QUALITY}/db/getDbList2.do?${queryString}`);
}
/*获得hadoop集群*/
export async function getHadoop_list(query) {
  const queryString = qs.stringify(query);
  return request(`${API_BASE_QUALITY}/cloud/getHadoopList.do?${queryString}`);
}
/*获取表*/
export async function get_db_table(query) {
  const queryString = qs.stringify(query);
  return request(`${API_BASE_QUALITY}/db/getDbTables.do?${queryString}`);
}
/*获取表字段*/
export async function get_db_table_fields(query) {
  const queryString = qs.stringify(query);
  return request(`${API_BASE_QUALITY}/db/getDbTableFields.do?${queryString}`);
}
/*查询权限*/
export async function getDataStore(query) {
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_QUALITY}/cloud/getDataStore.do`, option);
}
/*获取sftp文件夹*/
export async function get_SftpList() {
  return request(`${API_BASE_QUALITY}/cloud/getSftpList.do`);
}
//获取系统设置值
export async function trans_VariablesList(query) {
  const queryString = qs.stringify(query);
  return request(`${API_BASE_QUALITY}/cloud/getVariables.do?${queryString}`);
}

/*检验文件夹是否存在*/
export async function get_FileExist(query) {
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  };

  return request(`${API_BASE_QUALITY}/cloud/fileExist.do`, option);
}

/*获取输出字段*/
export async function get_output_fields(query) {
  const option = {
    method: "POST",
    body: JSON.stringify({
      transName: query.transname,
      stepName: query.stepname
    })
  };
  return request(`${API_BASE_QUALITY}/step/getOutputFields.do`, option);
}
/*检查step名*/
export async function check_step_name(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify({
      transName: obj.transname,
      stepName: obj.stepname
    })
  };
  return request(`${API_BASE_QUALITY}/step/checkStepName.do`, option);
}
/*保存step*/
export async function save_step(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify({
      transName: obj.transname,
      stepName: obj.stepname,
      newName: obj.newname,
      type: obj.type,
      description: obj.description,
      stepParams: obj.config
    })
  };
  return request(`${API_BASE_QUALITY}/step/saveStep.do`, option);
}
/*获取输入字段*/
export async function get_input_fields(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify({
      transName: obj.transname,
      stepName: obj.stepname
    })
  };
  return request(`${API_BASE_QUALITY}/step/getInputFields.do`, option);
}
/*获取表字段*/
export async function get_details(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  };
  return request(`${API_BASE_QUALITY}/step/getDetails.do`, option);
}
/*获取数据库*/
export async function get_db_schema(query) {
	const queryString = qs.stringify(query);
  return request(`${API_BASE_QUALITY}/db/getDbSchema.do?${queryString}`);
}
/*服务器列表*/
export async function getServer_list(query) {
  const queryString = qs.stringify(query);
  return request(`${API_BASE_QUALITY}/cloud/getServerList.do?${queryString}`);
}
/*添加连线节点*/
export async function addLine(query) {
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_QUALITY}/hop/addHop.do`, option);
}

/*保存步骤配置*/
export async function save_stepConfigs(query) {
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_QUALITY}/step/saveStepConfigs.do`, option);
}

/*删除连线*/
export async function delete_hop(query) {
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  };

  return request(`${API_BASE_QUALITY}/hop/deleteHop.do`, option);
}

/*获取集群列表*/
export async function getCluster_list(query) {
  const queryString = qs.stringify(query);
  return request(`${API_BASE_QUALITY}/cloud/getClusterList.do?${queryString}`);
}

/*编辑步骤配置*/
export async function edit_stepConfigs(query) {
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_QUALITY}/step/editStepConfigs.do`, option);
}

/*根据名字 获取状态*/
export async function getTrans_status(query) {
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_QUALITY}/trans/getTransStatus.do`, option);
}

/*根据名字 获取执行状态*/
export async function getTrans_exec_status(query) {
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_QUALITY}/trans/getExecStatus.do`, option);
}


/*根据名字 获取id*/
export async function getTrans_exec_id(query) {
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_QUALITY}/trans/getExecId.do`, option);
}

/*获取步骤合并信息*/
export async function getTransExecInfo(query) {
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_QUALITY}/trans/getExecInfo.do`, option);
}

/*执行分析*/
export async function Trans_exec_configuration(query) {
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_QUALITY}/trans/execTrans.do`, option);
}

/*得到trans的执行记录*/
export async function get_TransRecords(query) {
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_QUALITY}/trans/getTransRecords.do`, option);
}

/*得到trans的执行日志*/
export async function get_TransLog(query) {
  const queryString = qs.stringify(query);
  return request(`${API_BASE_QUALITY}/trans/getTransLogs.do?${queryString}`);
}

// 暂停分析
export async function get_exec_pause(query) {
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  };

  return request(`${API_BASE_QUALITY}/trans/execPause.do`, option);
}

/*终止执行*/
export async function get_exec_stop(query) {
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  };

  return request(`${API_BASE_QUALITY}/trans/execStop.do`, option);
}

/*恢复执行*/
export async function get_exec_resume(query) {
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_QUALITY}/trans/execResume.do`, option);
}

/*获取文件树*/
export async function getQuaFileList(query) {
  const queryString = qs.stringify(query);
  return request(`${API_BASE_QUALITY}/cloud/getFileList.do?${queryString}`);
}

/*获取文件夹父路径*/
export async function getQuaParentPath(query) {
  const queryString = qs.stringify(query);
  return request(`${API_BASE_QUALITY}/cloud/getParentPath.do?${queryString}`);
}

//获取稽核总览 flag=year
export async function getNodeTypes(query) {
  const queryString = qs.stringify(query);
  return request(`${API_BASE_QUALITY}/statistics/getNodeTypes?${queryString}`);
}

//获取稽核总览 flag=year
export async function getNodeTotal() {
  return request(`${API_BASE_QUALITY}/statistics/getNodeTotal`);
}

//获取稽核总览 flag=year
export async function getTaskByNodeType(query) {
  const queryString = qs.stringify(query);
  return request(
    `${API_BASE_QUALITY}/statistics/getTaskByNodeType?${queryString}`
  );
}

//获取稽核总览 flag=year
export async function getStatisticsReferenceByNodeType(query) {
  const queryString = qs.stringify(query);
  return request(
    `${API_BASE_QUALITY}/statistics/getStatisticsReferenceByNodeType?${queryString}`
  );
}

//获取稽核4w列表
export async function get4WListByRenter(query) {
  const queryString = qs.stringify(query);
  return request(`${API_BASE_QUALITY}/etl4w/get4WListByRenter?${queryString}`);
}

//获取稽核4w列表
export async function get4WByExecId(query) {
  const queryString = qs.stringify(query);
  return request(`${API_BASE_QUALITY}/etl4w/get4WByExecId?${queryString}`);
}

//获取稽核4w列表
export async function getRedundanceDetail(query) {
  const queryString = qs.stringify(query);
  return request(
    `${API_BASE_QUALITY}/analysis/getRedundanceDetail?${queryString}`
  );
}
