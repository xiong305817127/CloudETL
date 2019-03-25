import { API_BASE } from '../constants';
import request from '../utils/request';

const requestUrl = {
  //服务开放&应用中心：
  standarListRequest: API_BASE + '/dataStandard/search',
  
}
// 获取元素据列表
 export async function getStandList(obj) {
  return request(requestUrl.standarListRequest+"?pageNum="+obj.pageNum+"&pageSize="+obj.pageSize);
}
