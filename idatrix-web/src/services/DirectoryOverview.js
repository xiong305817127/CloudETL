import qs from 'qs';
import { API_BASE_CATALOG,API_BASE_RESOURCE } from '../constants';
import request from '../utils/request';

const RequestUrl={
  CommonsUrlApi: API_BASE_CATALOG,

  ListDownloadApi:'/dataUpload/download',

};
/*数据量信息*/
export async function MLZYgetOverview(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/overview/getOverview?${querystring}`)
}
/*最新资源底部消息*/
export async function MLZYgetOverall() {
    return request(`${API_BASE_CATALOG}/overview/getOverall`)
}
 /*展示数据量信息（注册量，订阅量，发布量）*/
export async function MLZYgetLatest(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/overview/getLatest?${querystring}`)
}
/*获取更多资源*/
export async function MLZYgetPublishedAll(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/overview/getPublishedAll?${querystring}`)
}
/*获取数据量，注册量，订阅量数据集*/
export async function MLZYgetLib(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/overview/getLib?${querystring}`)
}
//根据id编辑
export async function getResourceAll(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_CATALOG}/overview/getResource?${querystring}`);
}

/*源服务*/


// 服务获取接口
export async function getWSDLContents(query) {
    const querystring = qs.stringify(query);
       return request(`${API_BASE_CATALOG}/service/getWSDLContents?${querystring}`);
  }
  

/*源服务 编辑，保存*/
export async function MLZYsaveOrUpdate(query) {
    const option = {
    method: "POST",
    body: JSON.stringify(query)
  }
    return request(`${API_BASE_CATALOG}/srcService/saveOrUpdate`,option)
}
/*源服务查询列表*/
export async function MLZYgetAllSourceServicePages(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/srcService/getAllSourceServicePages?${querystring}`)
}
/*源服务删除*/
export async function MLZYdeleteSourceServiceById(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/srcService/deleteSourceServiceById?${querystring}`)
}
/*原服务修改*/
export async function MLZYgetSourceServiceById(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/srcService/getSourceServiceById?${querystring}`)
}


/*资源共享*/


/*资源服务,新增,修改*/
export async function MLZYsaveOrUpdateServer(query) {
    const option = {
	    method: "POST",
	    body: JSON.stringify(query)
	  }
    return request(`${API_BASE_CATALOG}/service/saveOrUpdate`,option)
}

/*资源服务单体查询 id*/
export async function MLZYgetServiceById(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/service/getServiceById?${querystring}`)
}
/*资源服务删除*/
export async function MLZYdeleteServiceById(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/service/deleteServiceById?${querystring}`)
}


/*资源服务查询*/
export async function MLZYgetAllServicePages(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/service/getAllServicePages?${querystring}`)
}


/*上报数据*/

/*上报数据删除*/
export async function MLZYdeleteDLRecordById(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/dataUpload/deleteDLRecordById?${querystring}`)
}
/*上报数据查询条件*/
export async function MLZYgetAllDateUploadRecords(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/dataUpload/getAllDateUploadRecords?${querystring}`)
}

/*下载ex模板*/
export async function MLZYdownloadTemplate(query) {
    const option = {
    method: "POST",
    body: JSON.stringify(query)
  }
    return request(`${API_BASE_CATALOG}/dataUpload/downloadTemplate`,option)
}

/*下载ex文件*/
export async function ListDownloadApi(query) {
    const option = {
    method: "POST",
    body: JSON.stringify(query)
  }
    return request(`${API_BASE_CATALOG}/dataUpload/download`,option)
}


/*校验文件是否存在*/
export async function MLZYisExistedResourceFile(query) {
   const option = {
    method: "POST",
    body: JSON.stringify(query)
  }
    return request(`${API_BASE_CATALOG}/dataUpload/isExistedResourceFile`,option)
}

/*数据库或文件类*/
export async function MLZYsaveOrUpdateUploadData(query) {
    const option = {
    method: "POST",
    body: JSON.stringify(query)
  }
    return request(`${API_BASE_CATALOG}/dataUpload/saveOrUpdateUploadData`,option)
}

/*获取资源类型*/
export async function MLZYgetPub(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/resource/getPub?${querystring}`)
}
/*上报记录 日志*/
export async function getETLTaskDetailInfoById(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/dataUpload/getETLTaskDetailInfoById?${querystring}`)
}


/*上报记录 日志*/
export async function getTempDataUploadRecords(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/dataUpload/getTempDataUploadRecords?${querystring}`)
}


/*上传文件保存接口*/
export async function MLZYupdateBatchDataUploadDetails(query) {
    const option = {
    method: "POST",
    body: JSON.stringify(query)
  }
    return request(`${API_BASE_CATALOG}/dataUpload/updateBatchDataUploadDetails`,option)
}
/*上传文件 数据库类型文件*/
export async function MLZYsaveOrUpdateUploadDataForDB(query) {
    const option = {
    method: "POST",
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: JSON.stringify(query)
  }
    return request(`${API_BASE_CATALOG}/dataUpload/saveOrUpdateUploadDataForDB`,option)
}

/*
   交换管理
   前置管理
 */
/* 根据id删除数据*/
export async function deleteTerminalManageRecordById(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/terminalManage/deleteTerminalManageRecordById?${querystring}`)
}

/*渲染表格数据*/
export async function getTerminalManageRecordsByCondition(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/terminalManage/getTerminalManageRecordsByCondition?${querystring}`)
}
/*根据id获取编辑数据*/
export async function getTerminalManageRecordById(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/terminalManage/getTerminalManageRecordById?${querystring}`)
}
/*保存数据接口数据*/
export async function saveOrUpdateTerminalManage(query) {
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  }
    return request(`${API_BASE_CATALOG}/terminalManage/saveOrUpdateTerminalManage`,option)
}

/*前置机查询*/
export async function getDeptServer(query) {
   
/*  const querystring = qs.stringify(query);*/
    return request(`${API_BASE_RESOURCE}/dataSwap/getDeptServer/${query}`)
}

/*数据库查询*/
export async function getFSDatabase(query) {
    return request(`${API_BASE_RESOURCE}/dataSwap/getFSDatabase/${query}`)
}

/*sFTP查询*/
export async function getFSSftp(query) {
    return request(`${API_BASE_RESOURCE}/sftp/getFSSftp/${query}`)
}

/*提交校验接口*/
export async function isExistedTerminalManageRecord(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/terminalManage/isExistedTerminalManageRecord?${querystring}`)
}

/*日志管理*/
export async function getAllServiceLog(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/serviceLog/getAllServiceLog?${querystring}`)
}

/*服务详情接口*/
export async function getServiceLogDetailById(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/serviceLog/getServiceLogDetailById?${querystring}`)
}


/*上报作业*/
/*服务详情接口*/
export async function getStatistics(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/uploadTask/getStatistics?${querystring}`)
}
/*正在执行接口*/
export async function getRunning(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/uploadTask/getRunning?${querystring}`)
}

/*查询接口*/
export async function getOverview(query) {
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  }
    return request(`${API_BASE_CATALOG}/uploadTask/getOverview`,option)
}


/*交换作业  查询接口*/
/*服务详情接口*/
export async function getStatisticsReact(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/subTask/getStatistics?${querystring}`)
}
/*正在执行接口*/
export async function getRunningReact(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/subTask/getRunning?${querystring}`)
}
export async function getOverviewTask(query) {
  const option = {
    method: "POST",
    body: JSON.stringify(query)
  }
    return request(`${API_BASE_CATALOG}/subTask/getOverview`,option)
}
/*历史详情接口*/
export async function getHistory(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/subTask/getHistory?${querystring}`)
}
/*订阅关系管理 查询*/
export async function getManage(query) {
    const option = {
      method: "POST",
      body: JSON.stringify(query)
    }
    return request(`${API_BASE_CATALOG}/subscribe/getManage`,option)
}

/*订阅关系管理 启动*/
export async function stop(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/subscribe/stop?${querystring}`)
}
/*订阅关系管理 终止*/
export async function resume(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/subscribe/resume?${querystring}`)
}

/*订阅关系 服务描述*/
export async function getWebservice(query) {
   const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/subscribe/getWebservice?${querystring}`)
}

/*订阅管理
  订阅关系管理 
  订阅关系 服务描述
   alisa 2019-9-26日填写*/
export async function getOwnManage(query) {
     const option = {
      method: "POST",
      body: JSON.stringify(query)
    }
    return request(`${API_BASE_CATALOG}/subscribe/getOwnManage`,option)
}


//系统管理
//基础数据

//数据格式类型字典
export async function addType(query) {
    const option = {
     method: "POST",
     body: JSON.stringify(query)
   }
   return request(`${API_BASE_CATALOG}/dictionary/addType`,option)
}

//数据共享类型字典
export async function addShare(query) {
    const option = {
     method: "POST",
     body: JSON.stringify(query)
   }
   return request(`${API_BASE_CATALOG}/dictionary/addShare`,option)
}

//数据分类型字典
export async function addClassify(query) {
    const option = {
     method: "POST",
     body: JSON.stringify(query)
   }
   return request(`${API_BASE_CATALOG}/dictionary/addClassify`,option)
}


//查询字典不同类型字典
export async function getDict(query) {
    const querystring = qs.stringify(query);
     return request(`${API_BASE_CATALOG}/dictionary/getDict?${querystring}`)
 }
 //删除分类字典
 export async function deletelist(query) {
    const querystring = qs.stringify(query);
     return request(`${API_BASE_CATALOG}/dictionary/delete?${querystring}`)
 }

 //强制删除分类字典
 export async function forceDelete(query) {
    const querystring = qs.stringify(query);
     return request(`${API_BASE_CATALOG}/dictionary/forceDelete?${querystring}`)
 }

  //强制删除分类字典
  export async function ServerdDelete(query) {
    const option = {
        method: 'DELETE',
        body: JSON.stringify(query),
      };
    return request(`${API_BASE_CATALOG}/file/delete?id=`+query,option)
 }

  //共享服务查看详情下载
  export async function downloadDetile(query) {
    const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/file/download?${querystring}`)
 }



  //资源配置里面使用-资源类型获取  获取第一层
  export async function getResourceShareDict(query) {
    const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/dictionary/getResourceShareDict?${querystring}`)
 }

  //资源配置里面使用-资源类型获取  获取第二层
  export async function getResourceTypeDict(query) {
    const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/dictionary/getResourceTypeDict?${querystring}`)
 }
 //获取网页填报字段标题内容  数据上报第三步
 export async function getBrowseFormDataTitle(query) {
    const querystring = qs.stringify(query);
    return request(`${API_BASE_CATALOG}/dataUpload/getBrowseFormDataTitle?${querystring}`)
 }

   //通过web页面生成上报数据  保存
   export async function updateDateByBrowse(query) {
    const option = {
        method: 'POST',
        body: JSON.stringify(query),
      };
    return request(`${API_BASE_CATALOG}/dataUpload/updateDateByBrowse`,option)
 }

 
