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
 * >     dispatch({ type: 'resourcesCommon/getDepartments' });
 * >   }
 * >
 * >   // 第二步：引入需要的数据
 * >   render() {
 * >     const { departmentsOptions, departmentsTree } = this.props.resourcesCommon;
 * >     console.log(departmentsOptions, departmentsTree);
 * >   }
 * >
 */

import {
  getDepartments,
  getHdfsTree,
  getPermitsList,
} from 'services/resourcesCommon';
import { getAllOrgs,getAllServices,getRoles,getSubtreeAndDepth } from 'services/catalog';
import { deepCopy, convertArrayToTree } from '../../utils/utils';

let Timer = null;

/**
 * 不可变原始state
 * 使用时注意先用deepCopy拷贝一份，防止污染
 */
const immutableState = {
  departmentsOptions: [],   // 组织选项列表
  departmentsTree: [],      // 组织树，可供Tree、TreeSelect、Cascader组件使用

  resourcesList:[],         // 资源目录树
  allDepartments:[],        // 所有部门列表
  servicesList:[],          //所有服务名
  rolesList:[],          //所有租户下角色

  hdfsTree: [],             // HDFS树形
  hdfsPlanList: [],         // HDFS树展开成一维数组
  permits: {                // 权限选项
    tableOptions: [],       // 数据表类权限选项列表
    fileOptions: [],        // 文件目录类权限选项列表
  }
};

export default {

  namespace: 'resourcesCommon',

  state: {
    ...deepCopy(immutableState),
  },

  subscriptions:{
    setup({ history,dispatch }){
      history.listen(({ pathname })=>{
        if(pathname.indexOf("resources") !== -1){
          if(Timer){
            clearTimeout(Timer);
          }
          Timer = setTimeout(()=>{
            dispatch({ type:"getRoles" });
            dispatch({ type:"getFolderList" });
            dispatch({ type:"getServicesList" });
            dispatch({ type:"getResourcesList" });
            dispatch({ type:"getAllDepartments" });
            dispatch({ type:"getDepartments" });
            dispatch({ type:"getHdfsTree" });
            // dispatch({ type:"getPermitsOptions" });
          },300);
        }
      })
    }
  },
  effects: {
      /**
     *  查询租户下的角色
     *  @param  {boolean} options.force  是否强制刷新
     */
    *getRoles({ force }, { put,call,select }) {
      const { resourcesCommon } = yield select(state => state);
      const { rolesList } = resourcesCommon;
      if(rolesList.length>0 && !force) return;
      const { data } = yield call(getRoles,{pageSize:10000});
      yield put({ type: 'save', payload: { rolesList: data && data.data.list || [] } });
    },
    /**
     *  查询所有服务名
     *  @param  {boolean} options.force  是否强制刷新
     */
    *getServicesList({ force }, { put,call,select }) {
      const { resourcesCommon } = yield select(state => state);
      const { servicesList } = resourcesCommon;
      if(servicesList.length>0 && !force) return;
      const { data } = yield call(getAllServices);
      yield put({ type: 'save', payload: { servicesList: data && data.data ? data.data : [] } });
    },
    /**
     *  查询所有资源目录
     *  @param  {boolean} options.force  是否强制刷新
     */
    // *getResourcesList({ force }, { put,call,select }) {
    //   const { resourcesCommon } = yield select(state => state);
    //   const { resourcesList } = resourcesCommon;
    //   if(resourcesList.length>0 && !force) return;
    //   const { data } = yield call(getAllNode);
    //   yield put({ type: 'save', payload: { resourcesList: data && data.data || [] } });
		// },

		/**
     *  逐级查询资源目录
     *  @param  {boolean} options.force  是否强制刷新
     */
    *getResourcesFolder({ payload,resolve,treeNode,resourcesList }, { put,call,select }) {
			const { data } = yield call(getSubtreeAndDepth,{...payload});
			const { code } = data;
			if(code === "200"){
				if(resourcesList && resourcesList.length > 0){
					treeNode.props.dataRef.children = data.data.map(index=> {
						index.children = null;
						return index;
					});
					resolve();
					yield put({ type: 'save', payload: { resourcesList } });
				}else{
					const resourcesCommon = yield select(state=>state.resourcesCommon);
					for(let index of data.data){
						index.children = null;
						resourcesCommon.resourcesList.push(index);
					}
					yield put({ type: 'save', payload: { resourcesList:resourcesCommon.resourcesList } });
				}				
			}
    },

    /**
     *  查询所有部门信息
     *  @param  {boolean} options.force  是否强制刷新
     */
    *getAllDepartments({ force }, { put,call,select }) {
      const { account,resourcesCommon } = yield select(state => state);
      const { allDepartments } = resourcesCommon;
      if(allDepartments.length>0 && !force) return;
      const { data } = yield call(getAllOrgs,{userId: account.id});
      const { code } = data;
      if(code === "200"){
         yield put({ type: 'save', payload: { allDepartments: data.data || [] } });
      }
    },
    /**
     * 查询组织选项数据
     * @param  {boolean} options.force  是否强制刷新
     */
    *getDepartments({ force }, { put, select }) {
      const { account, resourcesCommon } = yield select(state => state);
      if (resourcesCommon.departmentsOptions.length > 0 && !force) return;
      console.log("发起请求");
      const { data } = yield getDepartments({
        userid: account.id,
      });
      if (Array.isArray(data.data)) {
        const list = data.data.map(item => ({
          label: item.deptName,
          code: item.deptCode,
          value: String(item.id),
        }));
        const rootId = list[0] && list[0].id;
        const tree = convertArrayToTree(data.data, rootId, 'id', 'parentId', 'children', child => ({
          value: String(child.id),
          label: child.deptName,
           code: child.deptCode,
           unifiedCreditCode:child.unifiedCreditCode
        }));
        yield put({ type: 'save', payload: { departmentsOptions: list, departmentsTree: tree } });
      }
    },
    /**
     * 查询hdfs树形
     * @param  {[type]} options.force  是否强制刷新
     */
    *getHdfsTree({ force }, { put, select }) {
      const { account, resourcesCommon } = yield select(state => state);
      if (resourcesCommon.hdfsTree.length > 0 && !force) return;
      const { data } = yield getHdfsTree({
        renterId: account.renterId,
        hasRealHdfsDir: true,
      });
      if (data.data) {
        try {
          const tree = data.data;
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
      const { account, resourcesCommon } = yield select(state => state);
      const permits = { ...resourcesCommon.permits };
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
