import { getList, getAllUserList, getRolesByUserId } from '../../../services/usersManage';
import { getList as getAllOrganization } from '../../../services/securityOrganization';
import { getList as getAllRoleList } from '../../../services/roleManage';

// 缺省组织根节点
const defaultDept = {
  id: null,
  deptName: '组织和机构',
  parentId: '',
};

export default {

  namespace: 'usersManage',

  state: {
    list: [],
    allUserList: [], // 所有用户
    allRoleList: [], // 所有角色
    allOrganizations: [], // 所有组织机构
    belongRoles: [],
  },

  effects: {
    *getList({ payload }, { call, put }) {  // eslint-disable-line
      const res = yield getList(payload);
      yield put({ type: 'save', payload: res.data.data });
    },
    // 获取所有用户
    *getAllUserList({}, { call, put }) {  // eslint-disable-line
      const { data } = yield getAllUserList();
      yield put({ type: 'save', payload: { allUserList:data && data.data.list?data.data.list:[] } });
    },
    // 获取所有角色
    *getAllRoleList({}, { call, put }) {  // eslint-disable-line
      const { data } = yield getAllRoleList({ pageSize: 100000 });
      yield put({ type: 'save', payload: { allRoleList: data.data.list } });
    },
    // 根据用户id获取用户所属角色列表
    *getRolesByUserId({ payload }, { put }) {
      const { data } = yield getRolesByUserId(payload);
      yield put({ type: 'save', payload: { belongRoles: data && data.data && data.data ? data.data : [] } });
    },
  },

  subscriptions: {
    setup({ history, dispatch }) {
      // 监听 history 变化，当进入 `/` 时触发 `load` action
      return history.listen(({ pathname, query }) => {
        if (pathname === '/UserManagementTable') {
          dispatch({
            type: 'getList',
            payload: {
              pageNo: query.page,
              pageSize: query.pageSize,
            },
          });
        }
      });
    },
  },

  reducers: {
    save(state, action) {
      return { ...state, ...action.payload };
    },
  },

};
