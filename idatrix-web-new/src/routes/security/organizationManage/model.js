import { getList, getUsersByOrgId,batchDeleteOrganization } from '../../../services/securityOrganization';
import { deepCopy } from '../../../utils/utils';
import { message } from "antd";

// 缺省根节点
const defaultDept = {
  id: null,
  deptName: '组织和机构',
  parentId: '',
};

export default {

  namespace: 'organizationManage',

  state: {
    list: [],
    allList: [defaultDept],
    userList: [],
    parentId: null,
  },

  effects: {
    // 获取所有组织机构
    *getAllList({ payload }, { call, put,select }) {
      const { parentId } = yield select(state=>state.organizationManage);
      const { data } = yield getList(payload);
      const allList = data ? data.data.list : [];
      allList.unshift(defaultDept);
      yield put({ type: 'save', payload: { allList } });
      yield put({ type: 'getList', payload: { parentId: parentId || 'null' } });
      yield put({ type: "securityCommon/getOrgList",payload:{force: true, dataReady: data}})
    },
    // 根据组织id查询用户列表
    *getUsersByOrgId({ payload }, { put }) {
      const { data } = yield getUsersByOrgId(payload);
      yield put({ type: 'save', payload: { userList: data && data.data || [] } });
    },
    //批量删除组织
    *batchDelete({payload},{call,put,select}){
      const { data } = yield call(batchDeleteOrganization,payload);
      const { renterId } = yield select(state=>state.account);
      const { code } = data;
      if(code === "200"){
          message.success("批量删除成功");
          yield put({ type:"getAllList",payload:{renterId,pageSize:10000000}})
      }
    }
  },

  subscriptions: {
    setup({ history, dispatch }) {
      // 监听 history 变化，当进入 `/` 时触发 `load` action
      return history.listen(({ pathname, query }) => {
        if (pathname === '/OrganizationManagementTable') {
          // dispatch({
          //   type: 'getList',
          //   payload: {
          //     pageNo: query.page,
          //     pageSize: query.pageSize,
          //   },
          // });
          // dispatch({ type: 'getAllList' });
        }
      });
    },
  },

  reducers: {
    getList(state, action) {
      const { payload } = action;
      const allList = deepCopy(state.allList);
      allList.shift(); // 移除缺省根节点
      state.list = allList.filter(item => {
        // 根据上级组织过滤
        if (payload.parentId) {
          return item.parentId == (payload.parentId === 'null' ? null : payload.parentId);
        }
        // 根据组织名称、代码、备注过滤
        if (payload.keyword) {
          return item.deptName.indexOf(payload.keyword) > -1
            // || item.remark.indexOf(payload.keyword) > -1;
            || item.deptCode.indexOf(payload.keyword) > -1;
        }
        return true;
      });
      return { ...state, parentId: payload.parentId };
    },
    save(state, action) {
      return { ...state, ...action.payload };
    },
  },

};
