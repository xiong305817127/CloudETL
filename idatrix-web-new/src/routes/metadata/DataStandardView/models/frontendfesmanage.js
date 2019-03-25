import { DEFAULT_PAGE_SIZE } from '../../../../constants';
import { getPlatformServerList } from '../../../../services/metadata';

export default {
  namespace: 'frontendfesmanage',
  state: {
    model:"1",
    actionKey:"",
    platformServerInfo: {
      rows: [],
      total: 0,
    }
  },
  effects: {
    *getPlatformServerList({ payload }, { put }) {
      const query = {
        page: payload.page || 1,
        rows: payload.pageSize || DEFAULT_PAGE_SIZE,
      }
      const { data } = yield getPlatformServerList(query);
      yield put({ type: 'save', payload: { platformServerInfo: {
        rows: data.data && data.data.rows || [],
        total: data.data && data.data.total || 0,
      }}});
    }
  },
  reducers: {
    changeView(state,action){
      return {
        ...state,
        ...action.payload
      };
    },
    save(state, action) {
      return { ...state, ...action.payload };
    },
  },
  subscriptions: {
    setup({ dispatch, history }) {
      history.listen(location=>{
        if(location.pathname === "/DataStandardView"){
          const model = location.query.model ? location.query.model : '1';
          dispatch({
            type:"changeView",
            payload: { model },
          });
          if (model == 2) {
            // 获取平台侧服务列表
            dispatch({
              type: 'getPlatformServerList',
              payload: location.query,
            });
          }
        }
      });
    }
  }
};
