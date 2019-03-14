import { getRoleList } from '../../../services/menuManage';

export default {

  namespace: 'menuManage',

  state: {
    list: [],
  },

  effects: {
    *getList({ payload }, { put }) {
      const res = yield getRoleList(payload);
      yield put({ type: 'save', payload: res.data });
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
