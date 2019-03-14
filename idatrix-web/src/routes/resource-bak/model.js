/**
 * 数据资源目录公共信息model
 * 注：该model维护的是持久数据，故，可公用的数据才能在此维护
 *
 * 使用说明：
 * 以调用组织数据为例
 * >
 * >   // 第一步：dispatch数据相关的查询方法
 * >   componentWillMount() {
 * >     const { dispatch } = this.props;
 * >     dispatch({ type: 'resourcesCommonChange/getDepartments' });
 * >   }
 * >
 * >   // 第二步：引入需要的数据
 * >   render() {
 * >     const { departmentsOptions, departmentsTree } = this.props.resourcesCommonChange;
 * >     console.log(departmentsOptions, departmentsTree);
 * >   }
 * >
 */

import {
  getDepartments,
  getAllResource,
  getHdfsTree,
  getPermitsList,
  findOrgnazation
} from '../../services/resourcesCommon';
import { deepCopy, convertArrayToTree } from '../../utils/utils';

/**
 * 不可变原始state
 * 使用时注意先用deepCopy拷贝一份，防止污染
 */
const immutableState = {
  findorgnazaTree: [],      // 目录资源目录查询所有单独使用 alisa   2018-9-26
  departmentsOptions: [],   // 组织选项列表
  departmentsTree: [],      // 组织树，可供Tree、TreeSelect、Cascader组件使用
  industryOptions: [],      // 行业选项列表
  themeOptions: [],         // 主题选项列表
  tagsOptions: [],          // 标签选项列表
  hdfsTree: [],             // HDFS树形
  hdfsPlanList: [],         // HDFS树展开成一维数组
  allDepartmentsOptions:[], // 跨租户组织选项列表
  allDepartmentsTree:[],    // 跨租户组织树，可供Tree、TreeSelect、Cascader组件使用
  permits: {                // 权限选项
    tableOptions: [],       // 数据表类权限选项列表
    fileOptions: [],        // 文件目录类权限选项列表
  }
};

export default {

  namespace: 'resourcesCommonChange',

  state: {
    ...deepCopy(immutableState),
  },

  effects: {
    /**
     * 查询组织选项数据
     * @param  {boolean} options.force  是否强制刷新
     */
    *getDepartments({ force }, { put, select }) {
      const { account, resourcesCommonChange } = yield select(state => state);
      if (resourcesCommonChange.departmentsOptions.length > 0 && !force) return;
      const { data } = yield getDepartments({
        userid: account.id,
      });
      if (Array.isArray(data.data)) {
        const list = data.data.map(item => ({
          label: item.deptName,
          value: String(item.id),
        }));
        const rootId = list[0] && list[0].id;
        const tree = convertArrayToTree(data.data, rootId, 'id', 'parentId', 'children', child => ({
          value: String(child.id),
          label: child.deptName,
        }));
        yield put({ type: 'save', payload: { departmentsOptions: list, departmentsTree: tree } });
      }
    },
    /**
     * 查询行业、标签选项数据
     * @param  {boolean} options.force  是否强制刷新
     */
    *getAllResource({ force }, { put, select }) {
      const { account, resourcesCommonChange } = yield select(state => state);
      const { industryOptions, themeOptions, tagsOptions } = resourcesCommonChange;
      // 已经查询过一次（行业、标签有任意列表有值），但未要求强制刷新，则结束请求
      if ((industryOptions.length > 0 || themeOptions.length > 0 || tagsOptions > 0) && !force) return;
      const { data } = yield getAllResource();
      if (Array.isArray(data.data)) {
        industryOptions.splice(0);
        themeOptions.splice(0);
        tagsOptions.splice(0);
        const list = data.data.map(item => {
          switch (item.type) {
            case '行业':
              industryOptions.push({label: item.keyword, value: String(item.id)});
            break;
            case '主题':
              themeOptions.push({label: item.keyword, value: String(item.id)});
            break;
            case '标签':
              tagsOptions.push({label: item.keyword, value: String(item.id)});
            break;
          }
        });
        yield put({ type: 'save', payload: { industryOptions, themeOptions, tagsOptions } });
      }
    },

    /**
     * 查询hdfs树形
     * @param  {[type]} options.force  是否强制刷新
     */
    *getHdfsTree({ force }, { put, select }) {
      const { account, resourcesCommonChange } = yield select(state => state);
      if (resourcesCommonChange.hdfsTree.length > 0 && !force) return;
      const { data } = yield getHdfsTree({
        renterId: account.renterId,
      });
      if (data.data) {
        try {
          const tree = JSON.parse(data.data);
          const plan = (arr) => {
            const result = [];
            arr.forEach(item => {
              result.push({
                label: item.label,
                value: item.value,
              });
              if (Array.isArray(item.children)) {
                plan(item.children).forEach(p => result.push({
                  label: p.label,
                  value: p.value,
                }));
              }
            });
            return result;
          }
          yield put({ type: 'save', payload: { hdfsTree: tree } });
          yield put({ type: 'save', payload: { hdfsPlanList: plan(tree) } });
        } catch (err) {}
      }
    },

    // 获取权限列表
    *getPermitsOptions({ force }, { put, select }) {
      const { account, resourcesCommonChange } = yield select(state => state);
      const permits = { ...resourcesCommonChange.permits };
      if ((permits.tableOptions.length > 0 || permits.fileOptions.length > 0) && !force) return;
      // 获取数据表类权限
      const { data } = yield getPermitsList({busstype: 1});
      if (Array.isArray(data.data)) {
        permits.tableOptions.splice(0);
        data.data.forEach(item => {
          permits.tableOptions.push({
            label: item.chinesename,
            value: item.authId,
          });
        });
        yield put({ type: 'save', payload: { permits } });
      }
      // 获取文件目录类权限
      const { data: data2 } = yield getPermitsList({busstype: 2});
      if (Array.isArray(data2.data)) {
        permits.fileOptions.splice(0);
        data2.data.forEach(item => {
          permits.fileOptions.push({
            label: item.chinesename,
            value: item.authId,
          });
        });
        yield put({ type: 'save', payload: { permits } });
      }
    },

    //获取所有租户的组织机构
    *findOrgnazation({force},{put,call,select}){
      const { allDepartmentsOptions } = yield select(state => state.resourcesCommonChange);
      if (allDepartmentsOptions.length > 0 && !force) return;
      const { data } = yield call(findOrgnazation);

      if (Array.isArray(data.data)) {
        const list = data.data.map(item => ({
          label: item.deptName,
          value: String(item.id),
        }));
        const rootId = list[0] && list[0].id;
        const tree = convertArrayToTree(data.data, rootId, 'id', 'parentId', 'children', child => ({
          value: String(child.id),
          label: child.deptName,
        }));
        yield put({ type: 'save', payload: { allDepartmentsOptions: list, allDepartmentsTree: tree } });
      }
    }
  },

  reducers: {
    // 合并状态
    save(state, action) {
      return { ...state, ...action.payload };
    },
    // 清理状态
    clear() {
      return {
        ...deepCopy(immutableState),
      };
    },
  },
};
