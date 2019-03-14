import qs from 'qs';
// 1.主端口API_BASE:/
import { API_BASE_ANALYSIS } from '../constants';
import request from '../utils/request';
//数据分析&探索：14个接口https://<ip>:<port>/iop/app/list
const RequestUrl={
  //公共URL:开发已写到justreq： CommonsUrlApi:"",
  CommonsUrlApi: API_BASE_ANALYSIS,

  //1.目录查询与获取列表是同一个：目录管理hdfs
  ListSearchApi:'/hdfs/metadata/list',
  //2.文件列表：
  GetListApi:'/hdfs/file/list',
  //3.目录新建：
  NewListApi:'/hdfs/file/new',
  //4.目录重命名：
  RenameListApi:'/hdfs/file/rename',
  //5.目录删除：
  ListDeleteApi:'/hdfs/file/delete',
  //6.文件上传：
  ListUploadApi:'/hdfs/file/upload',
  //7.文件下载：
  ListDownloadApi:'/hdfs/file/download',

  //8.存储列表查询：数据查询db  原接口
  DataListApi:'/db/storage/list',
  //8.存储列表查询：数据查询db  新接口
  DataListNewApi:'/db/storage/list',
  //9.语句执行：     原接口
  DataActionApi:'/db/sql/execute',
  //9.语句执行：    新接口
  DataActionNewApi:'/db/sql/execute',
  //10.执行结果查询：
  ResultSearchApi:'/db/sql/result',
  //11.执行历史查询：
  HistorySearchApi:'/db/sql/history',

  //12.索引创建：全文检索2.0
  ElasticNewApi:'/Elasticsearch/new',
  //13.索引删除：
  ElasticDeleteApi:'/Elasticsearch/delete',
  //14.索引重建：
  ElasticRenewApi:'/Elasticsearch/renew',
};



/*1.目录管理：异步请求方法+请求方式+请求模式+API头部信息!!!+证书+数据主体+IP接口*/
/*1.1目录查询： POST请求*/
export async function listSearchTable(obj) {
  const option = {
    method: "POST",
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(obj),
  };
  return request(RequestUrl.CommonsUrlApi+RequestUrl.ListSearchApi, option);
};
/*1.2获取目录列表： POST请求*/
export async function getListTable(obj) {
  const option = {
    method: "POST",
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(obj),
  };
  return request(RequestUrl.CommonsUrlApi+RequestUrl.GetListApi, option);
};

/*1.3新建目录： POST请求*/
export async function newListTable(obj) {
  const option = {
    method: "POST",
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(obj)
  };
  return request(RequestUrl.CommonsUrlApi+RequestUrl.NewListApi, option);
};
/*1.4重命名目录： POST请求*/
export async function renameListTable(obj) {
  const option = {
    method: "POST",
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(obj)
  };
  return request(RequestUrl.CommonsUrlApi+RequestUrl.RenameListApi, option);
};
/*1.5上传文件： POST请求*/
export async function upLoadingTable(obj) {
  const option = {
    method: "POST",
    body: qs.stringify(obj)
  };
  return request(RequestUrl.CommonsUrlApi+RequestUrl.ListUploadApi, option);
};
/*1.6下载文件： POST请求*/
export const fileDownloadApi = RequestUrl.CommonsUrlApi + RequestUrl.ListDownloadApi;

/*1.7移入回收站： POST请求*/
export async function deleteListTable(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
    // body: qs.stringify(obj)
  };
  return request(RequestUrl.CommonsUrlApi + RequestUrl.ListDeleteApi, option);
};
/*1.8回收站： POST请求*/

/*2.数据查询*/
/* 2.1获取数据列表:POST-GET*/
/*原接口 不使用*/
export async function getDataList() {
  return request(RequestUrl.CommonsUrlApi + RequestUrl.DataListApi);
};
/* 2.2语句执行:*/
/*原接口不使用*/
export async function actionDataList(obj) {
  const option = {
    method: "POST",
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(obj)
  };
  return request(RequestUrl.CommonsUrlApi + RequestUrl.DataActionApi, option);
};


/*2.数据查询*/
/* 2.1获取数据列表:POST-GET*/
export async function getDataNewList() {
  return request(RequestUrl.CommonsUrlApi + RequestUrl.DataListNewApi);
};
/* 2.2语句执行:*/
export async function actionDataNewList(obj) {
  const option = {
    method: "POST",
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(obj)
  };
  return request(RequestUrl.CommonsUrlApi + RequestUrl.DataActionNewApi, option);
};


/* 2.3结果查询:*/
export async function searchResultList(obj) {
  const option = {
    method: "POST",
    body: qs.stringify(obj)
  };
  return request(RequestUrl.CommonsUrlApi + RequestUrl.ResultSearchApi, option);
};
/* 2.4历史查询:*/
export async function searchHistoryList(obj) {
  const option = {
    method: "POST",
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(obj)
  };
  return request(RequestUrl.CommonsUrlApi + RequestUrl.HistorySearchApi, option);
};

export async function getSchemaInfo(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_ANALYSIS}/db/schemaInfo?${querystring}`);
};
