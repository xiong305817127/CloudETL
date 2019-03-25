/**
 * Created by Administrator on 2018/4/26.
 */
/*
* 多维分析
* */
import qs from 'qs';
import { API_BASE_OLAP } from '../constants';
import request from '../utils/request';

// 获取选项列表
export async function getOlap() {
    return request(`${API_BASE_OLAP}/list`);
}


