import { getResourcesList } from '../../../services/securityResources';

export default {

  namespace: 'resourcesManage',

  state: {
    resourcesList: [], // 资源列表
    resourceView: {}, // 资源详情
  },

  effects: {
    *getResourcesList({ payload }, { put }) {
      const { data } = yield getResourcesList(payload);
      yield put({ type: 'save', payload: { resourcesList: data && data.data && data.data.list ? data.data.list : [] } });
    },
    *showResource({ payload }, { put }) {
      yield put({ type: 'save', payload: { resourceView: payload } });
    },
  },

  subscriptions: {
    setup({ history, dispatch }) {
      return history.listen(({ pathname, query }) => {
        if (pathname === '/ResourcesManagingTable') {
          dispatch({
            type: 'getResourcesList',
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
