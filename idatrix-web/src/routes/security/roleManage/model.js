import { getList, getPermissionById, getUsersByRoleId } from '../../../services/roleManage';
import { getResourcesList } from '../../../services/securityResources';

export default {

  namespace: 'roleManage',

  state: {
    list: [],
    resourcesList: [],
    permissionIds: [],
    userList: [],
  },

  effects: {
    *getList({ payload }, { put }) {
      const res = yield getList(payload);
      yield put({ type: 'save', payload: res.data });
    },
    // 获取权限资源
    *getResourcesList({ payload }, { put }) {
      const res = yield getResourcesList(payload);
      yield put({ type: 'save', payload: { resourcesList: res.data.data.list || [] } });
    },
    // 获取相关用户
    *getPermissionById({ payload }, { put }) {
      const { data } = yield getPermissionById(payload);
      const permissionIds = [];
      if ( data.data ) {
        data.data.forEach(item => permissionIds.push(item.id))
      }
      yield put({ type: 'save', payload: { permissionIds } });
    },
    // 获取角色用户列表
    *getUsersByRoleId({ payload }, { put }) {
      const { data } = yield getUsersByRoleId(payload);
      yield put({ type: 'save', payload: { userList: data && data.data || [] } });
    },
  },

  subscriptions: {
    setup({ history, dispatch }) {
      return history.listen(({ pathname, query }) => {
        if (pathname === '/RoleManagementTable') {
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
