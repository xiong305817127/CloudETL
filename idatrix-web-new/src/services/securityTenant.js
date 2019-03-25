/**
 * 安全子系统 - 租户管理
 */
import qs from "qs";
import base64 from "base64-utf8";
import {
  API_BASE_SECURITY,
  API_BASE_METADATA,
  API_BASE_MONITOR,
  API_BASE_METADATA_NEW
} from "../constants";
import request from "../utils/request";
import { CLUSTER_USER, CLUSTER_PWD } from "../config/cluster.config";

// 租户管理：model 获取列表http://localhost:8000/security/renter/list.shtml
export async function getList(data) {
  const querystring = qs.stringify(data);
  const query = querystring ? `?${querystring}` : "";
  return request(`${API_BASE_SECURITY}/renter/list.shtml${query}`);
}
// 租户管理：POST新建/renter/add,JSON格式拼接提交
export async function newTenant(obj) {
  const option = {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
    },
    body: qs.stringify(obj)
  };
  return request(`${API_BASE_SECURITY}/renter/add.shtml`, option);
}

// 禁用租户，也可以开启用户
// added by steven leo on 2018/12/17
export async function disalbeTenant(obj) {
  const option = {
    method: "PUT",
    headers: { "Content-Type": "application/json;charset=UTF-8" }
  };
  return request(
    `${API_BASE_SECURITY}/renter/updateStatus.shtml?${qs.stringify(obj)}`,
    option
  );
}

// 重置租户密码
// added by steven leo on 2018/12/17
export async function resetPassword(obj) {
  const option = {
    method: "PUT",
    headers: { "Content-Type": "application/json;charset=UTF-8" }
  };
  return request(
    `${API_BASE_SECURITY}/renter/restRenterPassword.shtml?${qs.stringify(obj)}`,
    option
  );
}

// 修改租户
export async function modifyTenant(obj) {
  const option = {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
    },
    body: qs.stringify(obj)
  };
  return request(`${API_BASE_SECURITY}/renter/update.shtml`, option);
}

// 删除租户
export async function deleteTenant(obj) {
  const option = {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
    },
    body: qs.stringify(obj)
  };
  return request(`${API_BASE_SECURITY}/renter/delete.shtml`, option);
}

// 获取服务列表
export async function getServicesList() {
  const basic = base64.encode(`${CLUSTER_USER}:${CLUSTER_PWD}`);
  const option = {
    headers: {
      Authorization: `Basic ${basic}`,
      "X-Requested-By": "ambari"
    }
  };

  /**
   * 由运维系统转为metacube的接口
   */
  return request(
    `${API_BASE_METADATA_NEW}/ambari/api/v1/service_config_versions`,
    option
  );
  // return request(`${API_BASE_MONITOR}/clusters/${CLUSTER_NAME}/configurations/service_config_versions?service_name.in(CLUSTERINFO)&is_current=true`, option);
}

// 获取资源列表
export async function getResourcesList() {
  return request(`${API_BASE_SECURITY}/permission/getSystem.shtml`);
}

// 验重，包括租户名、账户、邮箱、手机
export async function checkRepeated(formData) {
  const option = {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
    },
    body: qs.stringify(formData)
  };
  return request(`${API_BASE_SECURITY}/renter/isDuplicate`, option);
}
