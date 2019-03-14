/**
 * 元数据公共信息model
 * 注：该model维护的是持久数据，故，可公用的数据才能在此维护
 *
 * 使用说明：
 * 以调用部门数据为例
 * >
 * >   // 第一步：dispatch数据相关的查询方法
 * >   componentWillMount() {
 * >     const { dispatch } = this.props;
 * >     dispatch({ type: 'metadataCommon/getDepartments' });
 * >   }
 * >
 * >   // 第二步：引入需要的数据
 * >   render() {
 * >     const { departmentsOptions, departmentsTree } = this.props.metadataCommon;
 * >     console.log(departmentsOptions, departmentsTree);
 * >   }
 * >
 */

import {
  getSourceTable,
  getDepartments,
  getStoreDatabase,
  getStoreDatabaseAcquition,
  getUserByRenterId,
  getAllResource,
  getHdfsTree,
  getfindOrgnazation
} from 'services/metadataCommon';
import { deepCopy, convertArrayToTree } from 'utils/utils';

/**
 * 不可变原始state
 * 使用时注意先用deepCopy拷贝一份，防止污染
 */
const immutableState = {
 
  sourceTableOptions: [],   // 数据来源表选项列表
  departmentsOptions: [],   // 部门选项列表
  departmentsTree: [],      // 部门树，可供Tree、TreeSelect、Cascader组件使用
  storeDatabaseOptions: [], // 存储的数据库选项列表（联动子选项）
  storeAcquisitionDatabaseOptions: [], // 存储的数据库选项列表（联动子选项） 采集
  usersOptions: [],         // 部门用户选项列表（联动子选项）
  industryOptions: [],      // 行业选项列表
  themeOptions: [],         // 主题选项列表
  tagsOptions: [],          // 标签选项列表
  hdfsTree: [],             // HDFS树形
  hdfsPlanList: [],         // HDFS树展开成一维数组
  clearSelect: false, // 是否需要清空选中的select框
};

export default {

  namespace: 'metadataCommon',

  state: {
    ...deepCopy(immutableState),
  },
  subscriptions:{
		setup({history,dispatch}){
			history.listen(({ pathname,query })=>{
				if(pathname === "/DataSystemSegistration"){
					if(query.tabType === "6"){
            console.log("检查路由是否为6")
						dispatch({ type:"getHdfsTree",force:true});
					}
				}
			})
		}
  },
  effects: {
    /**
     * 查询数据来源表（Cascader组件使用）
     * 无dsId时，查库，有dsId时，查表
     * @param  {boolean} options.force  是否强制刷新
     * @param  {string}  options.dsId   数据库id
     */
    *getSourceTable({ force, dsId }, { put, select }) {
      const { account, metadataCommon } = yield select(state => state);
      if (metadataCommon.sourceTableOptions.length > 0 && !dsId && !force) return;
      if (!dsId) { // 无dsId，查库
        const { data } = yield getSourceTable({ renterId: account.renterId });
        if (data.data && Array.isArray(data.data.rows)) {
          const list = data.data.rows.map(item => ({
            label: `${item.frontEndServer.serverName} - ${item.dbDatabasename}`,
            value: String(item.dsId),
            isLeaf: false,
          }));
          yield put({ type: 'save', payload: { sourceTableOptions: list } });
        }
      } else { // 有dsId，查表
        const { sourceTableOptions } = metadataCommon;
        const found = sourceTableOptions.find(db => db.value == dsId);
        // 未找到父节点，或已经查询过一次但未要求强制刷新，则结束请求
        if (!found || (found.children || found.children === null) && !force) return;
        const { data } = yield getSourceTable({ dsId });
        if (data.data && Array.isArray(data.data.rows)) {
          found.children = data.data.rows.length > 0 ? data.data.rows.map(item => ({
            label: `${item.metaNameCn}(${item.metaNameEn})`,
            value: String(item.metaid),
          })) : null;
          found.loading = false;
          yield put({ type: 'save', payload: { sourceTableOptions } });
        }
      }
    },

    /**
     * 查询部门选项数据
     * @param  {boolean} options.force  是否强制刷新
     */
    *getDepartments({ force }, { put, select }) {
      const { account, metadataCommon } = yield select(state => state);
      if (metadataCommon.departmentsOptions.length > 0 && !force) return;
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
     * 查询存储的数据库
     * !! 联动子选项
     * @param  {boolean} options.force   是否强制刷新
     * @param  {string}  options.dstype  数据库类型
     */
    *getStoreDatabase({ force , dstype = 3 }, { put, select }) {
      const { account, metadataCommon } = yield select(state => state);
     console.log(dstype,"getStoreDatabase========================");
      // 联动子选项，需要第一时间清空
      yield put({ type: 'save', payload: { storeDatabaseOptions: [] } });
    
      const { data } = yield getStoreDatabase({
        renterId: account.renterId,
        userId: account.id,
        dstype,
        /*sourceId: 2,*/
        status: 2,
      });

      if (data.data && Array.isArray(data.data.rows)) {
        const list = data.data.rows.map(item => ({
          label: item.dsName,
          value: String(item.dsId),
        }));
        const storeDatabaseOptions = list;
        storeDatabaseOptions.dstype = dstype;
        yield put({ type: 'save', payload: { storeDatabaseOptions } });
      }
    },


/**
     * 查询存储的数据库   采集
     * !! 联动子选项
     * @param  {boolean} options.force   是否强制刷新
     * @param  {string}  options.dstype  数据库类型
     */
    *getStoreDatabaseAcquition({ force ,dstype = 4},{ put, select }) {
      console.log(dstype,"getStoreDatabaseAcquition===================");
      const { account, metadataCommon,acquisition } = yield select(state => state);
      // 联动子选项，需要第一时间清空
      yield put({ type: 'save', payload: { storeAcquisitionDatabaseOptions: [] } });
      
      const { data } = yield getStoreDatabaseAcquition({
        renterId: account.renterId,
        userId: account.id,
        dstype,
        sourceId: 2,
        status: 2,
      });
      if (data.data && Array.isArray(data.data.rows)) {
        const list = data.data.rows.map(item => ({
          label: item.dbDatabasename,
          value: String(item.dsId),
        }));
        const storeAcquisitionDatabaseOptions = list;
        storeAcquisitionDatabaseOptions.dstype = dstype;
        yield put({ type: 'save', payload: { storeAcquisitionDatabaseOptions } });
      }  
    },
    /**
     * 查询用户选项数据
     * @param  {boolean} options.forc    是否强制刷新
     */
    *getUsers({ force }, { put, select }) {
      const { account, metadataCommon } = yield select(state => state);
      if (metadataCommon.usersOptions.length > 0 && !force) return;
      const { data } = yield getUserByRenterId({ renterId: account.renterId });
      if (Array.isArray(data.data)) {
        const list = data.data.map(item => ({
          label: item.username,
          value: String(item.id),
        }));
        const usersOptions = list;
        yield put({ type: 'save', payload: { usersOptions } });
      }
    },

    /**
     * 查询行业、标签选项数据
     * @param  {boolean} options.force  是否强制刷新
     */
    *getAllResource({ force }, { put, select }) {
      const { account, metadataCommon } = yield select(state => state);
      const { industryOptions, themeOptions, tagsOptions } = metadataCommon;
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
      const { account, metadataCommon } = yield select(state => state);
      if (metadataCommon.hdfsTree.length > 0 && !force) return;
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
          yield put({ type: 'save', payload: { hdfsTree: tree, hdfsPlanList: plan(tree),clearSelect: true } });
        } catch (err) {}
      }
    },

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
