/*trans请求*/

import qs from "qs";
// 1.主端口API_BASE:/
import {
  API_BASE_GATHER,
  STANDALONE_ETL,
  API_BASE_SECURITY
} from "../constants";
import request from "../utils/gatherRequest";
import downRequest from "../utils/downRequest";

const url = STANDALONE_ETL ? "/cloudetl" : API_BASE_GATHER;

const runTrans = {
  /*批量停止*/
  batchStop: "/trans/execBatchStop",

  /*创建新的jndi连接*/
  getJndiList: "/db/getJndiList.do",
  /*删除JNDI源*/
  deleteJndi: "/db/deleteJndi.do",
  /*编辑JNDI源*/
  editJndi: "/db/editJndi.do",
  /*保存JNDI源*/
  createJndi: "/db/createJndi.do",
  /*检查JNDI源名称重复*/
  checkJndiName: "/db/checkJndiName.do",

  /*复制步骤*/
  copyStep: "/step/copyStep.do",

  getProcList: "/db/getProc.do",

  getSftpList: "/cloud/getSftpList.do",

  /*获取Web查询操作*/
  getWebUrl: "/ws/getOperations.do",

  /*获取版本信息*/
  getVersion: "/cloud/version.do",

  /*获取文件夹父路径*/
  getParentPath: "/cloud/getParentPath.do",

  /*获取文件树*/
  getFileList: "/cloud/getFileList.do",
  deleteFile: "/cloud/deleteFile.do",
  downloadFile: "/cloud/downloadFile.do",

  /*获取文件树*/
  getFileExist: "/cloud/fileExist.do",

  //转换列表
  trans_list: "/cloud/getTransList.do",
  trans_group: "/trans/getTransGroups.do",

  //服务器列表
  server_list: "/cloud/getServerList.do",

  /*删除数据库*/
  db_list: "/db/getDbList.do",
  db_list2: "/db/getDbList2.do",
  delete_dblist: "/db/deleteDbConnection.do",
  //测试单个数据库状态
  testDbConnection: "/db/testDbConnection.do",

  //测试服务器
  testServer: "/cloud/testServer.do",

  //得到服务器目录

  getDataStore: "/cloud/getDataStore.do",

  /*服务器模块*/
  saveServer: "/cloud/saveServer.do", //保存服务器
  checkServerName: "/cloud/checkServerName.do", //检索服务器名称是否相同
  editServer: "/cloud/editServer.do", //编辑服务器
  deleteServer: "/cloud/deleteServer.do", //编辑服务器

  /*SparkEngine*/
  getSparkEngineList: "/cloud/getSparkEngineList.do", //spark集群列表
  checkSparkName: "/cloud/checkSparkName.do", //spark集群列表
  deleteSpark: "/cloud/deleteSpark.do", //spark集群删除
  saveCluster: "/cloud/saveSpark.do", //spark集群保存
  editCluster: "/cloud/editSpark.do", //spark集群修改

  /*服务器集群*/
  cluster_list: "/cloud/getClusterList.do", //集群列表
  check_cluster: "/cloud/checkClusterName.do", //检测集群名字
  edit_cluster: "/cloud/editCluster.do", //编辑集群
  delete_list: "/cloud/deleteCluster.do", //删除集群
  save_list: "/cloud/saveCluster.do", //保存集群

  /*hadoop集群*/

  hadoop_list: "/cloud/getHadoopList.do", //hadoop 列表
  check_hadoop_name: "/cloud/checkHadoopName.do", //hadoop 列表
  edit_hadoop: "/cloud/editHadoop.do", //改
  save_hadoop: "/cloud/saveHadoop.do", //增
  delete_hadoop: "/cloud/deleteHadoop.do", //删

  /*转换流程*/
  open_trans: "/trans/openTrans.do",
  edit_trans_attributes: "/trans/editTransAttributes.do",
  save_trans_attributes: "/trans/saveTransAttributes.do",
  new_trans: "/trans/newTrans.do",
  check_trans_name: "/trans/checkTransName.do",
  delete_trans: "/trans/deleteTrans.do",
  trans_exec_result: "/trans/execTrans.do",
  trans_exec_step_measure: "/trans/getStepMeasure.do",
  trans_exec_id: "/trans/getExecId.do",
  trans_exec_step_status: "/trans/getStepStatus.do",

  getExecInfo: "/trans/getExecInfo.do",

  trans_exec_log: "/trans/getExecLog.do",
  trans_exec_finished: "/trans/getExecStatus.do",
  trans_status: "/trans/getTransStatus.do",
  exec_pause: "/trans/execPause.do",
  exec_resume: "/trans/execResume.do",
  exec_stop: "/trans/execStop.do",

  get_trans_records: "/trans/getTransRecords.do", //得到trans执行记录
  get_trans_log: "/trans/getTransLogs.do", //得到trans执行日志

  /*数据库连接*/
  db_connection_name: "/db/checkDbConnectionName.do",
  edit_db_connection: "/db/editDbConnection.do",
  save_db_connection: "/db/saveDbConnection.do",
  get_schema_name: "/db/getDbSchema.do",
  get_db_table: "/db/getDbTables.do",
  db_table_fields: "/db/getDbTableFields.do",

  /*节点连接*/
  add_hop: "/hop/addHop.do",
  invert_hop: "/hop/invertHop.do",
  delete_hop: "/hop/deleteHop.do",

  /*步骤操作*/
  add_step: "/step/addStep.do",
  edit_step: "/step/editStep.do",
  save_step: "/step/saveStep.do",
  check_step_name: "/step/checkStepName.do",
  delete_step: "/step/deleteStep.do",
  move_step: "/step/moveStep.do",
  get_output_fields: "/step/getOutputFields.do",
  get_input_fields: "/step/getInputFields.do",
  get_details: "/step/getDetails.do",

  //alisa修改
  //getMappings
  get_mappings: "/step/getMappings.do",
  //getMappingInfo
  get_mappingInfo: "/step/getMappingInfo.do",
  //createMapping
  get_createMapping: "/step/createMapping.do",
  //deleteMapping
  get_deleteMapping: "/step/deleteMapping.do",

  /*编辑步骤配置*/
  edit_stepConfigs: "/step/editStepConfigs.do",
  save_stepConfigs: "/step/saveStepConfigs.do",

  /*批量执行*/
  exec_BatchTrans: "/trans/execBatchTrans.do",

  /*执行引擎*/
  getDefaultEngineList: "/cloud/getDefaultEngineList.do" /*执行引擎列表*/,
  checkEngineName: "/cloud/checkEngineName.do" /*检索引擎名称*/,
  editEngine: "/cloud/editEngine.do" /*编辑执行引擎*/,
  saveEngine: "/cloud/saveEngine.do" /*保存执行引擎*/,
  deleteEngine: "/cloud/deleteEngine.do" /*删除执行引擎*/,

  /*查询权限*/
  getDeployMode: "/cloud/getDeployMode.do",
  getLoginUser: "/cloud/getLoginUser.do",
  //获取系统设置值
  trans_VariablesList: "/cloud/getVariables.do",

  // 角色授权
  setrole: "/cloud/saveSystemSetting.do",
  getCurrentRole: "/cloud/getSystemSetting.do"
  // 获取字典信息
  // 1. 电话 2. 日期 3. 身份证
  // getDictionary: "/analysis/dictData/{id}.do", //调用时替换id
  // getDictionaryList: "/analysis/dictAllList.do", //获取全部字典
  // getDictionaryAll: "/analysis/dictDataAllList/{id}.do"
};

/**
 * 查询字典
 * 注意替换接口地址中的{id}
 *
 */
// export async function getDictionary(obj){
//   const option = {
//     method: "GET",
//     headers: { 'Content-Type': 'application/json;charset=UTF-8' },
//     credentials: 'include'
//   };

//   // 替换getDictionary
//   const requestUrl =
//     obj.id === -1 ?
//     url+runTrans.getDictionaryList
//     :
//     (obj.all
//       ? url + runTrans.getDictionaryAll.replace("{id}",obj.id)
//       : url + runTrans.getDictionary.replace("{id}",obj.id)
//     );
//   return request( requestUrl , option );
// }

//调用安全角色接口
export async function getRoles(query) {
  const option = {
    method: "GET",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_SECURITY}/role/list.shtml`, option);
}

// 配置安全角色接口
export async function setRole(query) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(query)
  };
  return request(url + runTrans.setrole, option);
}

// 配置安全角色接口
export async function getCurrentRole(query) {
  const option = {
    method: "GET",
    body: JSON.stringify(query)
  };
  return request(url + runTrans.getCurrentRole, option);
}

/*批量停止*/
export async function batchTransStop(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.batchStop, option);
}

/*获取JNDI源名称*/
export async function getJndiList(obj) {
  const option = {
    method: "GET",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.getJndiList, option);
}

/*获取JNDI源名称*/
export async function editJndi(obj) {
  const option = {
    method: "GET",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.editJndi, option);
}

/*检测JNDI名字是否重复*/
export async function checkJndiName(obj) {
  const option = {
    method: "GET",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.checkJndiName, option);
}

/*获取JNDI源名称*/
export async function deleteJndi(obj) {
  const option = {
    method: "GET",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.deleteJndi, option);
}

/*获取JNDI源名称*/
export async function createJndi(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.createJndi, option);
}

/*获取步骤合并信息*/
export async function getTransExecInfo(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.getExecInfo, option);
}

/*复制步骤*/
export async function copyStep(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.copyStep, option);
}

/*批量执行*/
export async function execBatchTrans(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.exec_BatchTrans, option);
}

/*获取getProcList列表*/
export async function get_ProcList(obj) {
  const option = {
    method: "GET",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.getProcList, option);
}
/*获取sftp文件夹*/
export async function get_SftpList(obj) {
  const option = {
    method: "GET",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.getSftpList, option);
}

/*获取Web查询操作*/
export async function getWebUrl(obj) {
  const option = {
    method: "GET",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.getWebUrl, option);
}

/*获取文件夹父路径*/
export async function getParentPath(obj) {
  const option = {
    method: "GET",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.getParentPath, option);
}

/*查询权限*/
export async function getDataStore(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.getDataStore, option);
}

/*查询权限*/
export async function getVersion() {
  const option = {
    method: "GET"
  };
  return request(url + runTrans.getVersion, option);
}
/*查询权限*/
export async function getDeployMode() {
  const option = {
    method: "GET"
  };
  return request(url + runTrans.getDeployMode, option);
}
/*查询用户*/
export async function getLoginUser() {
  const option = {
    method: "GET"
  };
  return request(url + runTrans.getLoginUser, option);
}

/*测试单个服务器*/
export async function test_Server(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.testServer, option);
}

/*测试单个数据库*/
export async function test_DbConnection(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.testDbConnection, option);
}

/*检验文件夹是否存在*/
export async function get_FileExist(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };

  return request(url + runTrans.getFileExist, option);
}

/*删除文件*/
export async function downloadFile(obj) {
  const option = {
    method: "GET",
    body: obj
  };
  return downRequest(url + runTrans.downloadFile, option);
}

/*删除文件*/
export async function deleteFile(obj) {
  const option = {
    method: "POST",
    credentials: "include",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    body: JSON.stringify(obj)
  };

  return request(url + runTrans.deleteFile, option);
}

/*获取文件树*/
export async function getFileList(obj) {
  const option = {
    method: "GET",
    credentials: "include",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    body: JSON.stringify(obj)
  };

  return request(url + runTrans.getFileList, option);
}

/*得到trans的执行记录*/
export async function get_TransRecords(name) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(
      typeof name !== "object"
        ? {
            name: name
          }
        : name
    )
  };

  return request(url + runTrans.get_trans_records, option);
}

/*得到trans的执行日志*/
export async function get_TransLog(obj) {
  const option = {
    method: "GET",
    credentials: "include",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.get_trans_log, option);
}

/*检测spark名字*/
export async function check_SparkName(name) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(
      typeof name === "object"
        ? name
        : {
            name: name
          }
    )
  };
  return request(url + runTrans.checkSparkName, option);
}

/*检查集群名字*/
export async function check_Cluster(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(
      typeof name === "object"
        ? name
        : {
            name: name
          }
    )
  };
  return request(url + runTrans.check_cluster, option);
}

/*保存步骤配置*/
export async function save_stepConfigs(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.save_stepConfigs, option);
}

/*编辑步骤配置*/
export async function edit_stepConfigs(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.edit_stepConfigs, option);
}

/*初始化*/
/*转换列表*/
export async function getTrans_list(obj) {
  const option = {
    method: "GET",
    body: JSON.stringify(obj)
  };

  return request(url + runTrans.trans_list, option);
}

// 获取转换group列表
export async function getTrans_group(obj) {
  const option = {
    method: "GET",
    body: JSON.stringify(obj)
  };

  return request(url + runTrans.trans_group, option);
}

/*获得hadoop集群*/
export async function getHadoop_list(obj) {
  const option = {
    method: "GET",
    credentials: "include",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    body: JSON.stringify(obj)
  };

  return request(url + runTrans.hadoop_list, option);
}
/*检查hadoop是否重复*/
export async function checkHadoop_name(name) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(
      typeof name === "object"
        ? name
        : {
            name: name
          }
    )
  };
  return request(url + runTrans.check_hadoop_name, option);
}

/*编辑hadoop*/
export async function edit_hadoop(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };

  return request(url + runTrans.edit_hadoop, option);
}

/*保存hadoop*/
export async function save_hadoop(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };

  return request(url + runTrans.save_hadoop, option);
}

/*删除hadoop*/
export async function delete_hadoop(name) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(
      typeof name === "object"
        ? name
        : {
            name: name
          }
    )
  };

  return request(url + runTrans.delete_hadoop, option);
}

/*数据库连接*/
export async function getDb_list2(obj) {
  const option = {
    method: "GET",
    credentials: "include",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    body: JSON.stringify(obj)
  };


  return request(url + runTrans.db_list2, option);
}

/*数据库连接*/
export async function getDb_list() {
  const option = {
    method: "GET",
    credentials: "include",
    headers: { "Content-Type": "application/json;charset=UTF-8" }
  };

  return request(url + runTrans.db_list, option);
}
/*删除数据库*/
export async function deleteDb_list(name) {
  const option = {
    method: "POST",
    headers: {
      "Content-Type": "application/json;charset=UTF-8",
      "Cache-Control": "no-cache"
    },
    credentials: "include",
    body: JSON.stringify(
      typeof name === "object"
        ? name
        : {
            name: name
          }
    )
  };

  return request(url + runTrans.delete_dblist, option);
}

/*服务器列表*/
export async function getServer_list(obj) {
  const option = {
    method: "GET",
    credentials: "include",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    body: JSON.stringify(obj)
  };

  return request(url + runTrans.server_list, option);
}

/*保存服务器*/
export async function getSaveServerlist(obj) {
  const option = {
    method: "POST",
    headers: {
      "Content-Type": "application/json;charset=UTF-8",
      "Cache-Control": "no-cache"
    },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.saveServer, option);
}
//检索服务器名称是否相同
export async function getcheckServerName(value) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(
      typeof name === "object"
        ? name
        : {
            name: name
          }
    )
  };
  return request(url + runTrans.checkServerName, option);
}

/*编辑服务器*/
export async function geteditServer(name) {
  const option = {
    method: "POST",
    headers: {
      "Content-Type": "application/json;charset=UTF-8",
      "Cache-Control": "no-cache"
    },
    credentials: "include",
    body: JSON.stringify(
      typeof name === "object"
        ? name
        : {
            name: name
          }
    )
  };
  return request(url + runTrans.editServer, option);
}

/*编辑服务器*/
export async function getdeleteServer(name) {
  const option = {
    method: "POST",
    headers: {
      "Content-Type": "application/json;charset=UTF-8",
      "Cache-Control": "no-cache"
    },
    credentials: "include",
    body: JSON.stringify(
      typeof name === "object"
        ? name
        : {
            name: name
          }
    )
  };
  return request(url + runTrans.deleteServer, option);
}

/*集群列表*/
export async function getCluster_list(obj) {
  const option = {
    method: "GET",
    credentials: "include",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    body: JSON.stringify(obj)
  };

  return request(url + runTrans.cluster_list, option);
}

/*编辑集群*/
export async function getedit_cluster(name) {
  const option = {
    method: "POST",
    headers: {
      "Content-Type": "application/json;charset=UTF-8",
      "Cache-Control": "no-cache"
    },
    credentials: "include",
    body: JSON.stringify(
      typeof name === "object"
        ? name
        : {
            name: name
          }
    )
  };
  return request(url + runTrans.edit_cluster, option);
}

/*保存集群*/
export async function getsave_list(obj) {
  const option = {
    method: "POST",
    headers: {
      "Content-Type": "application/json;charset=UTF-8",
      "Cache-Control": "no-cache"
    },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.save_list, option);
}

/*删除集群*/
export async function getdelete_list(name) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(
      typeof name === "object"
        ? name
        : {
            name: name
          }
    )
  };
  return request(url + runTrans.delete_list, option);
}

/*编辑SparkEngine*/
export async function geteditCluster(name) {
  const option = {
    method: "POST",
    headers: {
      "Content-Type": "application/json;charset=UTF-8",
      "Cache-Control": "no-cache"
    },
    credentials: "include",
    body: JSON.stringify(
      typeof name === "object"
        ? name
        : {
            name: name
          }
    )
  };
  return request(url + runTrans.editCluster, option);
}

/*保存spark引擎*/
export async function getsaveCluster(obj) {
  const option = {
    method: "POST",
    headers: {
      "Content-Type": "application/json;charset=UTF-8",
      "Cache-Control": "no-cache"
    },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.saveCluster, option);
}

/*spark引擎列表*/
export async function getSpark_list(obj) {
  const option = {
    method: "GET",
    credentials: "include",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    body: JSON.stringify(obj)
  };

  return request(url + runTrans.getSparkEngineList, option);
}
/*spark引擎删除*/
export async function getdeleteSpark(name) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(
      typeof name === "object"
        ? name
        : {
            name: name
          }
    )
  };
  return request(url + runTrans.deleteSpark, option);
}

/*执行引擎列表*/
export async function getDefaultEngineList(obj) {
  const option = {
    method: "GET",
    credentials: "include",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.getDefaultEngineList, option);
}
/*检索执行引擎名称*/
export async function checkEngineName(name) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(
      typeof name === "object"
        ? name
        : {
            name: name
          }
    )
  };
  return request(url + runTrans.checkEngineName, option);
}

/*编辑执行引擎*/
export async function editEngine(obj) {
  const option = {
    method: "POST",
    headers: {
      "Content-Type": "application/json;charset=UTF-8",
      "Cache-Control": "no-cache"
    },
    credentials: "include",
    body: JSON.stringify(obj)
  };

  return request(url + runTrans.editEngine, option);
}

/*保存执行擎*/
export async function saveEngine(obj) {
  const option = {
    method: "POST",
    headers: {
      "Content-Type": "application/json;charset=UTF-8",
      "Cache-Control": "no-cache"
    },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.saveEngine, option);
}
/*执行引擎删除*/
export async function deleteEngine(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.deleteEngine, option);
}

/*打开转换*/
export async function getOpen_trans(name) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(
      typeof name === "string"
        ? {
            name: name
          }
        : name
    )
  };

  return request(url + runTrans.open_trans, option);
}

export async function getEdit_trans_attributes(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.edit_trans_attributes, option);
}

export async function getSave_trans_attributes(name) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify({
      name: name.name,
      newName: name.newname,
      description: name.description,
      params: name.params,
      ...name
    })
  };
  return request(url + runTrans.save_trans_attributes, option);
}

export async function getNew_trans(name) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify({
      name: name.info_name,
      newName: name.copy_name ? name.copy_name : "",
      description: name.description,
      ...name
    })
  };
  return request(url + runTrans.new_trans, option);
}

export async function getCheck_trans_name(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };

  return request(url + runTrans.check_trans_name, option);
}

export async function getDelete_trans(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };

  return request(url + runTrans.delete_trans, option);
}

export async function Trans_exec_configuration(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };

  return request(url + runTrans.trans_exec_result, option);
}
/*获取步骤统计*/
export async function getTrans_exec_step_measure(id) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify({
      executionId: id
    })
  };

  return request(url + runTrans.trans_exec_step_measure, option);
}
/*根据名字 获取id*/
export async function getTrans_exec_id(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };

  return request(url + runTrans.trans_exec_id, option);
}

/*根据名字 获取执行状态*/
export async function getTrans_status(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };

  return request(url + runTrans.trans_status, option);
}

/*步骤状态*/
export async function getTrans_exec_step_status(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };

  return request(url + runTrans.trans_exec_step_status, option);
}

/*执行是否结束*/
export async function getTrans_exec_finished(id) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify({
      executionId: id
    })
  };

  return request(url + runTrans.trans_exec_finished, option);
}

/*获取日志*/
export async function getTrans_exec_log(id) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify({
      executionId: id
    })
  };


  return request(url + runTrans.trans_exec_log, option);
}
/*检查数据库名称*/
export async function check_dbname_result(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.db_connection_name, option);
}

/*编辑数据库连接*/
export async function edit_db_connection(obj) {
  const option = {
    method: "GET",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.edit_db_connection, option);
}

/*保存数据库链接*/
export async function save_db_connection(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };

  return request(url + runTrans.save_db_connection, option);
}

/*获取数据库*/
export async function get_db_schema(obj) {
  const option = {
    method: "GET",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.get_schema_name, option);
}

/*获取表*/
export async function get_db_table(body) {
  const option = {
    method: "GET",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(body)
  };
  return request(url + runTrans.get_db_table, option);
}

/*获取表字段*/
export async function get_db_table_fields(obj) {
  const option = {
		method: "GET",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.db_table_fields, option);
}

/*添加hop节点*/
export async function add_hop(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };

  return request(url + runTrans.add_hop, option);
}

/*反转hop节点*/
export async function invert_hop(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify({
      name: obj.transname,
      from: obj.start,
      to: obj.target,
      enabled: true
    })
  };

  return request(url + runTrans.invert_hop, option);
}

/*删除hop节点*/
export async function delete_hop(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };

  return request(url + runTrans.delete_hop, option);
}

/*添加step*/
export async function add_step(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };

  return request(url + runTrans.add_step, option);
}

/*编辑step*/
export async function edit_step(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify({
      stepName: obj.stepname,
      transName: obj.transname,
      ...obj
    })
  };

  return request(url + runTrans.edit_step, option);
}

/*保存step*/
export async function save_step(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify({
      transName: obj.transname,
      stepName: obj.stepname,
      newName: obj.newname,
      type: obj.type,
      description: obj.description,
      stepParams: obj.config,
      ...obj
    })
  };
  return request(url + runTrans.save_step, option);
}

/*检查step名*/
export async function check_step_name(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify({
      transName: obj.transname,
      stepName: obj.stepname,
      ...obj
    })
  };

  return request(url + runTrans.check_step_name, option);
}

/*删除step*/
export async function delete_step(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };


  return request(url + runTrans.delete_step, option);
}

/*移动step*/
export async function move_step(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };


  return request(url + runTrans.move_step, option);
}

/*获取输出字段*/
export async function get_output_fields(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify({
      transName: obj.transname,
      stepName: obj.stepname,
      ...obj
    })
  };

  return request(url + runTrans.get_output_fields, option);
}

/*获取输入字段*/
export async function get_input_fields(obj) {

  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify({
      transName: obj.transname,
      stepName: obj.stepname,
      ...obj
    })
  };

  return request(url + runTrans.get_input_fields, option);
}

/*暂停执行*/

export async function get_exec_pause(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };

  return request(url + runTrans.exec_pause, option);
}
/*恢复执行*/
export async function get_exec_resume(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.exec_resume, option);
}
/*终止执行*/

export async function get_exec_stop(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };

  return request(url + runTrans.exec_stop, option);
}
/*获取表字段*/
export async function get_details(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  return request(url + runTrans.get_details, option);
}

export async function trans_VariablesList(obj) {
  const option = {
    method: "GET",
    body: JSON.stringify(obj)
  };

  return request(url + runTrans.trans_VariablesList, option);
}

export async function getDebugPreviewData(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  };
  return request(`${url}/trans/getDebugPreviewData`, option);
}

export async function execMorePreview(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  };
  return request(`${url}/trans/execMorePreview`, option);
}

//获取Hdfs根目录
export async function getHdfsRoots(query) {
  const option = {
    method: "GET",
    body: JSON.stringify(query)
  };
  return request(`${url}/cloud/getHdfsRoots`, option);
}
