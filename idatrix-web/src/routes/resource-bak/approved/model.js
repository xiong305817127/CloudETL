import { getList } from '../../../services/approved';
import { DEFAULT_PAGE_SIZE } from '../../../constants';

const Timer = (time)=>{
  return new Promise((resolve)=>{
    setTimeout(()=>{
      resolve();
    },time)
  })
}


export default {

  namespace: 'approvedModel',

  state: {
    rows: [],
    total: 0,
  },

  effects: {
    *getList({ payload }, { put,select }) {
      Timer(300);
      const { username } = yield select(state=>state.account);
      if(!username){ return false }
      const query = { ...payload };
      query.username = username;
      query.keyword = query.keyword || '';
      query.rows = query.pageSize || DEFAULT_PAGE_SIZE;
      const { data } = yield getList(query);
      const source = data && data.data || { rows: []};
      source.rows = source.rows.filter(row => row !== null);
      yield put({ type: 'save', payload: source });
    },
  },

  subscriptions: {
    setup({ history, dispatch }) {
      return history.listen(({ pathname, query }) => {
        if (pathname === '/resources/approved') {
          dispatch({
            type: 'getList',
            payload: query,
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
