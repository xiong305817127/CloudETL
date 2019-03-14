/**
 * 数据系统注册相关接口
 * ! 由于历史原因，部分接口尚在metadata.js文件上，将逐步迁移到此文件
 */
import qs from 'qs';
import { API_BASE_METADATA } from '../constants';
import request from '../utils/request';

// 检测db用户名是否已存在
export async function dbUsernameIsExists(formData) {
  const option = {
    method: "POST",
    body: JSON.stringify(formData),
  };
  return request(`${API_BASE_METADATA}/dataSource/dbUsernameIsExists`, option);
}
