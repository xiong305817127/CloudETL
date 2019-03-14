import qs from 'qs';
import { API_BASE_METADATA, DEFAULT_PAGE_SIZE } from '../constants';
import request from '../utils/request';

const qingqiuUrl={
 // getMetaTable1: API_BASE_METADATA + '/metadataTable/search',
  getDepartmentTree: API_BASE_METADATA + '/userInfo/queryDeptTreeByUserId',
  //基本信息修改递交
  JiBenXinXiDiJiaoxiugai: API_BASE_METADATA + '/metadataTable/InsertHisVersion',
  xinjiantankuang:API_BASE_METADATA + '/abc/xinjiantankuang',
  WJMLxinjiantankuang:API_BASE_METADATA + '/abc/WJMLxuanxiang',
  Yiruhuishouzhan: API_BASE_METADATA + '/metadataTable/moveToRecycle',
  Huishouzhanget: API_BASE_METADATA + '/metadataTable/getRecycle',
  Xianyoubiaoxuanze: API_BASE_METADATA + '/metadataTable/queryByName',
  //这个是错的
  GetBiaoziduan: API_BASE_METADATA + '/metadataPros/queryByMetaId',
  ShangChuanYangLi: API_BASE_METADATA + '/FileUploadServlet',

  //历史版本
  GetLishibanben: API_BASE_METADATA + '/metadataHistory/getHistoryVersion',
  HistoryVersionDelete: API_BASE_METADATA + '/metadataHistory/deleteHistoryVersion',
  //永久删除
  YongjiushanchuPUT: API_BASE_METADATA + '/metadataTable/batchToDelete',
  //还原
  HuanYuanPUT: API_BASE_METADATA + '/metadataTable/restore',
  //文件目录里的请求
  //查询文件信息
  WenJianSearch: API_BASE_METADATA + '/metadataFile/search',
  //文件新建接口
  WenJianXinjian: API_BASE_METADATA + '/metadataFile/batchInsert',
  //文件批量修改接口
  WenJianXiuGai: API_BASE_METADATA + '/metadataFile/batchUpdate',
  //文件回收站查询
  WenJianHuishouzhanSearch: API_BASE_METADATA + '/metadataFile/getRecycle',
  //元文件导出接口
  WenJianDaochu: API_BASE_METADATA + '/metadataFile/exportFileByIds',
  //元文件移入回收站
  WenJianYiruHuishouzhan: API_BASE_METADATA + '/metadataFile/moveToRecycle',
  //元文件回收站永久删除
  WenJianHuishouzhanShanChu: API_BASE_METADATA + '/metadataFile/batchToDelete',
  //数据标准查询接口
  //搜索
  SJBZCXsearch: API_BASE_METADATA + '/dataStandard/search',
  //下载
  SJBZCXxiazai: API_BASE_METADATA + '/uploads',
    //删除
  SJBZCXDelete: API_BASE_METADATA + '/dataStandard/delete',
  //数据关系管理
  SJGXGLjianli:API_BASE_METADATA + "/dataRelation/createTablesRelation",
  //搜索
  SJGXGLsearch: API_BASE_METADATA + '/dataRelation/search',
  //新建
  SJGXGLxiazai: API_BASE_METADATA + '/dataRelation/createRelation',
  //数据关系关联字段
  SJGXGLguanlianziduan: API_BASE_METADATA + '/dataRelation/queryById',

  //数据关系关联字段删除
  SJGXGLGLZDshanchu: API_BASE_METADATA + '/dataRelation/deleteByMetaAndChildId',
  GetHistoryVersionField:API_BASE_METADATA + '/metadataPros/queryByMetaIdAndVersion',
  SJGXGLqueryTableAndFiledById:API_BASE_METADATA + '/dataRelation/queryTableAndFiledById',
  SJGXGLbatchUpdateTableAndField:API_BASE_METADATA + '/dataRelation/batchUpdateTableAndField',

  //平台侧
  SJGXGLMetadataInfosearch:API_BASE_METADATA+'/metadataTable/search',
  //数据关系关联字段删除
  SJGXGLdeleteById: API_BASE_METADATA + '/dataRelation/deleteById',
  SJGXGLqueryById:API_BASE_METADATA + '/metadataTable/queryById',
   SJGXGByTwoId: API_BASE_METADATA + '/dataRelation/queryTableAndFiledByTwoId',
  //校验表英文名称
  //dataRelation/isExists
  //metadataTable/isExists
   SJGXGLisExists:API_BASE_METADATA + '/dataRelation/isExists',
   //数据关系新建
  /* SJGXGtableAndFiledRelation: API_BASE_METADATA + '/dataRelation/createTablesRelation',*/
   //数据关系批量更新
   /*SJGXGUpdateTableAndField: API_BASE_METADATA + '/dataRelation/batchUpdateTableAndField',*/
  SJGXGdeleteById:API_BASE_METADATA +'/dataRelation/deleteById',


   /*前置机IP地址校验  serverIp，serverPort，dbUser，dbPassword*/
   SJGXGTestLink:API_BASE_METADATA + '/frontEndServer/testLink',
   /*ip重复接口   参数 renterId，serverIp*/   
   SJGXGisDuplicateIp:API_BASE_METADATA + '/frontEndServer/serverIsExistsByRenterId',
}


export async function SJGXGisDuplicateIp(obj) { 
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }      
  return request(qingqiuUrl.SJGXGisDuplicateIp,option);
}

export async function SJGXGTestLink(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }      
  return request(qingqiuUrl.SJGXGTestLink,option);
}

//搜索
export async function WenJianSearch(obj,paper) {

  console.log(paper);
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }
  return request(qingqiuUrl.WenJianSearch+'?rows='+paper.pageSize+'&page='+paper.current,option);
}

//搜索
export async function SJGXGLMetadataInfosearch(obj,pager) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }
  return request(qingqiuUrl.SJGXGLMetadataInfosearch+'?rows='+pager.pageSize+'&page='+pager.current,option)
}
  //数据关系管理选择表时做筛选  (旧)
export async function SJGXGByTwoId(obj) {
  /* const option = {
    method: 'GET',
    body: JSON.stringify(obj)
  };*/

   return request(qingqiuUrl.SJGXGByTwoId+'?metaid='+obj.metaid+"&childId="+obj.childId);
}
  //数据关系管理选择表时做筛选(新)
/*export async function SJGXGLisExists(obj) {
   /*const option = {
    method: 'GET',
    body: JSON.stringify(obj)
  };*/
  /* console.log(option,"SJGXGLisExists");
   return request(qingqiuUrl.SJGXGLisExists+'?metaid='+obj.metaid+"&childId="+obj.childId+"&rsType="+obj.rsType+"&tableType="+obj.tableType);
}*/
export async function SJGXGLisExists(formData) {

  const option = {
    method: "POST",
    body: JSON.stringify(formData)
  }
  return request(`${API_BASE_METADATA}/dataRelation/isExists`,option);
}
//数据关系删除SJGXGLdeleteById
export async function deleteFiledById(id) {
   const option = {
    method: 'POST'
  };
  return request(qingqiuUrl.SJGXGLdeleteById+'?id='+id,option);
}
//数据关系关联字段删除SJGXGLdeleteById
export async function SJGXGLdeleteById(obj) {
   const option = {
    method: 'POST'
  };
  return request(qingqiuUrl.SJGXGLdeleteById+'?id='+obj.id+"&status="+obj.status,option);
}
//数据关系关联字段删除
export async function SJGXGdeleteById(id) {
   const option = {
 /*   method: 'POST',*/
   /*  body: JSON.stringify(id)*/
  };
  return request(qingqiuUrl.SJGXGdeleteById+'?id='+id,option);
}

//数据关系关联字段删除
export async function SJGXGLGLZDshanchu(obj) {
   const option = {
    method: 'POST'
  };
  return request(qingqiuUrl.SJGXGLGLZDshanchu+'?metaid='+obj.metaid+"&childId="+obj.childId,option);
}

//数据关系管理点击修改获取表数据
export async function SJGXGLqueryTableAndFiledById(metaid) {
  return request(qingqiuUrl.SJGXGLqueryTableAndFiledById+'?id='+metaid);
}

//数据关系管理修改数据
export async function SJGXGLbatchUpdateTableAndField(formData) {
   const option = {
    method: 'POST',
    body: JSON.stringify(formData)
  };
  return request(`${API_BASE_METADATA}/dataRelation/batchUpdateTableAndField`,option);
}
/*export async function SJGXGLbatchUpdateTableAndField(data) {
  const option = {
    method: "POST",
    body: JSON.stringify([data])
  }
  return request(qingqiuUrl.SJGXGLbatchUpdateTableAndField,option)
}*/



//数据关系管理建立关系
export async function SJGXGLjianli(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }
  return request(qingqiuUrl.SJGXGLjianli,option)
}
//搜索
export async function SJGXGLsearch(obj,pager) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }
  return request(qingqiuUrl.SJGXGLsearch+'?rows='+pager.pageSize+'&page='+pager.current,option)
}

//数据标准查询
//搜索
export async function SJBZCXsearch(obj,paper) {
  const option = {
    method: "POST",
    body: JSON.stringify(
   obj
    )
  }
  return request(qingqiuUrl.SJBZCXsearch+'?rows='+paper.pageSize+'&page='+paper.current,option)
}
//元数据删除接口
export async function SJBZCXDelete(metaids) {
   const option = {
    method: 'GET',
  }
  return request(qingqiuUrl.SJBZCXDelete+'?id='+metaids,option);
}
//元数据下载接口
  export async function SJBZCXxiazai(metaids) {
  return request(qingqiuUrl.SJBZCXxiazai);
}
//文件批量修改接口
export async function WenJianXiuGai(data) {
  const option = {
    method: "POST",
    body: JSON.stringify([data])
  }
  return request(qingqiuUrl.WenJianXinjian,option)
}

//文件回收站搜索
export async function WenJianHuishouzhanSearch(dept,keyword,pager) {
  const option = {
    method: "POST",
    body: JSON.stringify({
      dept:dept,
      keyword:keyword,
      renterId: account.renterId,
    })
  }
  return request(qingqiuUrl.WenJianHuishouzhanSearch+'?rows='+pager.pageSize+'&page='+pager.current,option)
}
//文件回收站永久删除PUT
export async function WenJianHuishouzhanShanChu(fileids) {
  const option = {
    method: "PUT",
    body: JSON.stringify(fileids)
  }
  return request(qingqiuUrl.WenJianHuishouzhanShanChu,option)
}
//文件移入回收站PUT
export async function WenJianYiruHuishouzhan(fileids) {
  const option = {
    method: "PUT",
    body: JSON.stringify(fileids)
  }
  return request(qingqiuUrl.WenJianYiruHuishouzhan,option)
}
//元文件导出接口
export async function WenJianDaochu(fileids) {
  return request(qingqiuUrl.WenJianDaochu+'?ids='+fileids);
}
//历史版本GET请求
export async function GetLishibanben(metaids,pager) {
  return request(qingqiuUrl.GetLishibanben+'?ids='+metaids+'&rows='+pager.pageSize+'&page='+pager.current);
}
//历史版本表字段
export async function GetHistoryVersionField(metaid,versionid,pager) {
  return request(qingqiuUrl.GetHistoryVersionField+'?metaid='+metaid+'&versionid='+versionid+'&rows='+pager.pageSize+'&page='+pager.current);
}
//历史版本删除
export async function HistoryVersionDelete(metaids) {
  return request(qingqiuUrl.HistoryVersionDelete+'?ids='+metaids);
}
//还原PUT
export async function HuanYuanPUT(metaids) {
  const option = {
    method: "PUT",
    body: JSON.stringify(metaids)
  }
  return request(qingqiuUrl.HuanYuanPUT,option)
}
//永久删除PUT
export async function YongjiushanchuPUT(metaids) {
  const option = {
    method: "PUT",
    body: JSON.stringify(metaids)
  }
  return request(qingqiuUrl.YongjiushanchuPUT,option)
}

//上传样例
export async function ShangChuanYangLi(file) {
  const option = {
    method: "POST",
    body: JSON.stringify({
      file:file
    })
  }
  return request(qingqiuUrl.ShangChuanYangLi,option)
}

//get表字段,这个是精确查询，不是模糊查询。结果为一个数组
export async function GetBiaoziduan(metaid) {
  return request(qingqiuUrl.GetBiaoziduan+'?metaid='+metaid);
}


export async function Xianyoubiaoxuanze(metaNameCn,dept) {
  const option = {
    method: "POST",
    body: JSON.stringify({
      metaNameCn:metaNameCn,
      dept:dept
    })
  }
  return request(qingqiuUrl.Xianyoubiaoxuanze,option)
}
//回收站get请求
// export async function Huishouzhanget() {
//   const option = {
//     method: 'GET',  mode: 'cors',credentials: 'include', headers: { 'Content-Type': 'application/json;charset=UTF-8' }
//   }
//   return request(qingqiuUrl.Huishouzhanget,option);
// }
//回收站post请求
export async function Huishouzhanget(dept,metaName,pager) {
  const option = {
    method: "POST",
    body:JSON.stringify(
      {
        dept:dept,
        metaName:metaName,
        renterId: account.renterId, // skip
      }
    )
  }
  return request(qingqiuUrl.Huishouzhanget+'?rows='+pager.pageSize+'&page='+pager.current,option)
}

//文件目录表格数据get请求
export async function Wenjianmulu() {
  return request(qingqiuUrl.Wenjianmulu);
}

//表结构定义弹框
export async function Yiruhuishouzhan(xinxi) {
  const option = {
    method: "PUT",
    body: JSON.stringify(xinxi)
  }

  return request(qingqiuUrl.Yiruhuishouzhan,option)
}

export async function query() {
  return request('/api/users');
}
//首页表搜索和切换
//export async function getMetaTable1(dept,metaNameCn,metaType,pager) {
 // const option = {
 //   method: "POST",
 //   body: JSON.stringify({
 //     dept:dept,
 //     metaNameCn:metaNameCn,
 //     metaType:metaType,
 //     sourceId: 2,
 //   })
 // }
 // return request(qingqiuUrl.getMetaTable1+'?rows='+pager.pageSize+'&page='+pager.current,option)
//}
//首页表初始化
// export async function getMetaTable() {
//   const option = {
//     method: 'GET',  mode: 'cors',credentials: 'include', headers: { 'Content-Type': 'application/json;charset=UTF-8' }
//   }
//   return request(qingqiuUrl.shouyebiao,option);
// }

// 获得选择部门的树
export async function getDepartmentTree(id) {
  return request(qingqiuUrl.getDepartmentTree + '?userId=' + id);
}

//基本信息修改递交
export async function JiBenXinXiDiJiaoxiugai(xinxi) {
  const option = {
    method: "POST",
    body: JSON.stringify(xinxi)
  }
  /*实际项目路径，黄色改为实际路径，并加上option,例 return request("api/buttonText1.json",option)*/
  return request(qingqiuUrl.JiBenXinXiDiJiaoxiugai,option)
}

//新建弹框选项
// const optionsData1 = {accountId: null, s1: [], s2: [], s3: [], s4: [], s5: [], s6: [], s7: []};
// if (optionsData1.accountId !== account.id) { // 切换用户时重置
//   optionsData1.accountId = account.id;
//   Object.keys(optionsData1).forEach(key => {
//     if (key !== 'accountId') optionsData1[key].splice(0);
//   });
// }
// export async function getGeneralOptions(query = {}) {
//   // 获取数据来源选项列表
//   if (optionsData1.s1.length === 0) {
//     // const { data } = await request(`${API_BASE_METADATA}/frontEndServer/getall`);
//     const { data } = await request(`${API_BASE_METADATA}/metadataTable/search?rows=10000000`, {
//       method: "POST",
//       body: JSON.stringify({
//         renterId: account.renterId,
//         sourceId: 1,
//       }),
//     });
//     if (data && data.data && Array.isArray(data.data.rows)) {
//       optionsData1.s1 = data.data.rows.map(item => ({
//         label: `${item.metaNameCn}(${item.metaNameEn})`,
//         value: String(item.metaid),
//       }));
//     }
//   }
//   // 获取部门选项列表
//   if (optionsData1.s2.length === 0) {
//     const { data } = await request(`${API_BASE_METADATA}/UserServiceController/findDeptByUserId?userid=${account.id}`);
//     if (data && Array.isArray(data.data)) {
//       optionsData1.s2 = data.data.map(item => ({
//         label: item.deptName,
//         value: String(item.id),
//       }));
//     }
//   }
//   // 获取存储的数据库
//   if (optionsData1.s3.length === 0) {
//     const option = {method:'POST', body:JSON.stringify({sourceId: 2, renterId: account.renterId})};
//     const { data } = await request(`${API_BASE_METADATA}/dataSource/search?page=1&rows=10000000&renterId=${account.renterId}&userId=${account.id}`, option);
//     if (data && data.data && Array.isArray(data.data.rows)) {
//       optionsData1.s3 = data.data.rows.map(item => ({
//         label: item.dbDatabasename,
//         value: String(item.dsId),
//       }));
//     }
//   }
//   // 获取表拥有者（根据部门id）
//   if (query.deptId) {
//     const { data } = await request(`${API_BASE_METADATA}/UserServiceController/findUsersByDeptId?deptid=${query.deptId}`)
//     if (data && Array.isArray(data.data)) {
//       optionsData1.s4 = data.data.map(item => ({
//         label: item.username,
//         value: String(item.id),
//       }));
//     }
//   }
//   // 获取行业、主题、标签选项列表
//   if (optionsData1.s5.length === 0 && optionsData1.s6.length === 0 && optionsData1.s7.length === 0) {
//     const { data } = await request(`${API_BASE_METADATA}/DataResourceController/getAllResource`);
//     if (data && Array.isArray(data.data)) {
//       data.data.forEach(item => {
//         switch (item.type) {
//           case '行业':
//             optionsData1.s5.push({label: item.keyword, value: String(item.id)});
//           break;
//           case '主题':
//             optionsData1.s6.push({label: item.keyword, value: String(item.id)});
//           break;
//           case '标签':
//             optionsData1.s7.push({label: item.keyword, value: String(item.id)});
//           break;
//         }
//       });
//     }
//   }
//   return {
//     data: optionsData1
//   };
// }

//选择前置机数据来源表
export async function getDataOriginTable(obj,paper) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj),
  }
  return request(API_BASE_METADATA +"/metadataTable/search?page="+paper.current+"&rows="+paper.pageSize,option);
}
//数据来源部门
export async function getDepartment() {
  return request(API_BASE_METADATA + "/userInfo/findDeptByUserId?userid=" + account.id);
}
//选择存储的数据库
export async function getStoreDatabase(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify({sourceId: 1, renterId: obj.renterId})
  }
  return request(API_BASE_METADATA +`/dataSource/search?page=1&rows=10&renterId=${obj.renterId}&userId=${obj.userId}`,option);
}

//文件目录新建弹框选项
export async function WJMLxinjiantankuang() {
  await getGeneralOptions();
  return {data: {
    "s1":["HDFS路径1","HDFS路径2","HDFS路径3","HDFS路径4"],
    "s2":optionsData1.s2,
    "s4":optionsData1.s4,
    "s5":optionsData1.s5,
    "s6":optionsData1.s6,
    "s7":optionsData1.s7,
  }}
}

/*获取表*/
export async function get_db_table() {
  const option = {
    method: "POST",
    body: JSON.stringify({
      "connection": "11",
      "schema":"1"
    })
  }
  return request("./api/getDbTables.json");
}

/*获取表字段*/
export async function get_db_table_fields() {
  const option = {
    method: "POST",
    body: JSON.stringify({
      "connection": "1",
      "schema":"1",
      "table":"1"
    })
  }
  return request("./api/getDbTableFields.json")
}


/*前置机与数据系统注册*/

/*获取前置机表*/
export async function get_frontserver_table_fields(obj,data) {
  const option = {
    method: "POST",
    body: JSON.stringify(data)
  }
  return request(API_BASE_METADATA + "/frontEndServer/search?page="+obj.current+"&rows="+obj.pageSize, option);
}

/*获取前置机表*/
export async function search_frontserver_table_fields(paper,obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }
  return request(API_BASE_METADATA + "/frontEndServer/search?page="+paper.current+"&rows="+paper.pageSize,option)
}


/*新增前置机*/
export async function new_front_server(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }

  return request(API_BASE_METADATA + "/frontEndServer/insert",option)
}

/*更新前置机*/
export async function update_front_server(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }
  return request(API_BASE_METADATA + "/frontEndServer/update",option)
}

/*删除前置机*/
export async function delete_front_server(obj) {

  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }

  return request(API_BASE_METADATA + "/frontEndServer/updateStatus",option)
}


/*检验前置机名称*/
export async function getMetaTable(obj) {
  obj.renterId = account.renterId;
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }
  return request(API_BASE_METADATA + "/frontEndServer/serverNameIsExists",option)
}

/*检测名字重复*/
export async function check_front_server(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }
  return request(API_BASE_METADATA + "/frontEndServer/serverNameIsExists",option)
}

/*检测表名字重复*/
export async function check_get_name(obj) {
 /* obj.renterId = account.renterId;*/
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }
  return request(API_BASE_METADATA + "/dataSource/getname",option)
}

/**
 * （新）检测表格的`数据库中文名`和`数据库名称`
 * 此接口将会替换部分check_get_name的功能
 */
export async function check_if_dsname_exists(obj,type){
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }

  const url = typeof type !== "undefined" && type.type === "dbname" ? "/dataSource/dbNameIsExists" : "/dataSource/dsNameIsExists";
  return request(API_BASE_METADATA + url, option);
}

/*自动补全前置机位置*/
export async function get_front_pos(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify({type:obj && obj.type ? obj.type : ""})
  }
  const querystring = qs.stringify({page:1, rows: 10000});
  return request(`${API_BASE_METADATA}/frontEndServer/search?${querystring}`, option)
}

/*获取数据库表*/
export async function get_database_table_fields(obj,obj1) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj1)
  }
  return request(API_BASE_METADATA + "/dataSource/get?page="+obj.current+"&rows="+obj.pageSize,option)
}
/*新增数据库表字段*/
export async function insert_database_table_fields(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }
  return request(API_BASE_METADATA + "/dataSource/create",option);
}
/*新增数据库表字段*/
export async function update_database_table_fields(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }
  return request(API_BASE_METADATA + "/dataSource/update",option);
}
/*新增数据库表字段*/
export async function delete_database_table_fields(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }
  return request(API_BASE_METADATA + "/dataSource/updateStatus",option);
}


/*获取FTP表  get请求*/
export async function get_ftp_table_fiele_get(formData,query) {
  const querystring = qs.stringify(query);
  const option = {
    method: "POST",
    body: JSON.stringify(formData)
  }
  return request(`${API_BASE_METADATA}/sftp/search?${querystring}`, option);
}

/*获取FTP表*/
export async function get_ftp_table_fields(query, formData) {
  const querystring = qs.stringify(query);
  const option = {
    method: "POST",
    body: JSON.stringify(formData)
  }
  return request(`${API_BASE_METADATA}/sftp/search?${querystring}`, option);
}

/*搜索FTP表*/
export async function search_ftp_table_fields(paper,obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }
  return request(API_BASE_METADATA + "/sftp/search?page="+paper.current+"&rows="+paper.pageSize,option)
}


/*新增FTP字段*/
export async function insert_ftp_table_fields(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }
  return request(API_BASE_METADATA + "/sftp/insert",option)
}
/*修改FTP字段*/
export async function update_ftp_table_fields(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }
  return request(API_BASE_METADATA + "/sftp/update",option)
}
/*删除FTP字段*/
export async function delete_ftp_table_fields(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }
  return request(API_BASE_METADATA + "/sftp/updateStatus",option)
}
/*元数据修改基本信息接口（不产生历史版本）*/
export async function TableInsertBasicInfo(arr) {
  const option = {
    method: "POST",
    body: JSON.stringify(arr)
  }
  return request(API_BASE_METADATA + "/metadataTable/batchUpdate",option)
}
/*元数据修改基本信息接口（产生历史版本）*/
export async function TableInsertHisVersion(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }
  return request(API_BASE_METADATA + "/metadataTable/InsertHisVersion",option)
}
/*元数据表字段属性更新*/
export async function TableStructUpdate(arr) {
  const option = {
    method: "POST",
    body: JSON.stringify(arr)
  }
  return request(API_BASE_METADATA + "/metadataPros/update",option)
}
/*表结构注册*/
export async function get_table_struct(obj,dsId) {
  const option = {
    method: "POST",
    body: JSON.stringify({
      dsId: dsId,
      sourceId: 1
    }),
  }
  const querystring = qs.stringify({
    page: obj.current || 1,
    rows: obj.pageSize || DEFAULT_PAGE_SIZE,
  });
  return request(API_BASE_METADATA + "/metadataTable/search?" + querystring, option)
}
/*表结构修改*/
export async function edit_table_struct(id) {
  return request(API_BASE_METADATA + "/metadataPros/queryByMetaId?metaid="+id)
}
/*增加表结构*/
export async function add_table_struct(arr) {
  const option = {
    method: "POST",
    body: JSON.stringify(arr)
  }
  return request(API_BASE_METADATA + "/metadataPros/batchInsert",option)
}
/*导出表结构*/
export async function export_table_struct(obj) {
  return request(API_BASE_METADATA + "/metadataTable/exportMetadata")
}

/*删除表结构*/
export async function delete_table_struct(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }
  return request(API_BASE_METADATA + "/metadataTable/update",option)
}

/*生成实体表*/
export async function new_table_struct(obj) {
  const querystring = qs.stringify(obj);
  // const option = {
  //   method: "POST",
  //   body: JSON.stringify(obj)
  // }
  return request(API_BASE_METADATA + "/metadataTable/createEntityTable?" + querystring);
  // return request(API_BASE_METADATA + "/metadataTable/createtable",option)
}

/*搜索*/
export async function search_table_struct(obj,pager) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }
  console.log(9999)
  return request(API_BASE_METADATA + "/metadataTable/search?page="+(pager.current || 1)+"&rows="+(pager.pageSize || DEFAULT_PAGE_SIZE),option)
}

/*基本信息递交（生成新表）*/
export async function get_metatable_id(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }
  return request(API_BASE_METADATA + "/metadataTable/insert",option);
}

/*平台数据查询*/
export async function queryPlatformList(paper,obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj),
  };
  return request(API_BASE_METADATA + "/dataSource/search?page="+paper.current+"&rows="+paper.pageSize + `&renterId=${obj.renterId}&userId=${obj.id}`,option);
}

/*平台数据链接*/
export async function get_platform_url(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }
  return request(API_BASE_METADATA + "/platformServer/search",option);
}
/*平台HDFS树查询*/
export async function get_platform_HDFStree(renterId) {
  const option = {
    method: "POST",
    body: JSON.stringify({"renterId": renterId})
  }
  return request(API_BASE_METADATA + "/hdfs/search",option);
}
/*平台HDFS树新建*/
export async function insert_platform_HDFStree(obj) {
  console.log(obj)
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }
  return request(API_BASE_METADATA + "/hdfs/insert",option);
}
/*平台HDFS树修改*/
export async function update_platform_HDFStree(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }
  return request(API_BASE_METADATA + "/hdfs/update",option);
}
/*平台HDFS树删除*/
export async function delete_platform_HDFStree(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }
  return request(API_BASE_METADATA + "/hdfs/delete",option);
}

/*平台名字重复检测*/
export async function check_platform_name(obj) {
  // obj.renterId = account.renterId;
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }
  return request(API_BASE_METADATA + "/dataSource/getname",option);
}

/* 获取前置机资源平台侧列表 */
export async function getPlatformServerList(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_METADATA}/platformServer/list?${querystring}`);
}

// 导出元数据事实表接口
export const exportMetadata = `${API_BASE_METADATA}/metadataTable/exportMetadata`;

// 导出元数据文件目录接口
export const exportMetafile = `${API_BASE_METADATA}/metadataFile/exportFileByIds`;

// 数据表导入接口

export const importMetadataTable = `${API_BASE_METADATA}/metadataTable/importMetadata`;
