import { getList } from '../../../services/myApplication';
import { DEFAULT_PAGE_SIZE } from '../../../constants';

const Timer = (time)=>{
    return new Promise((resolve)=>{
      setTimeout(()=>{
        resolve();
      },time)
    })
}

export default {

  namespace: 'myApplication',

  state: {
    rows: [],
    total: 0,
  },

  effects: {
    *getList({ payload }, { put,select }) {
      Timer(300);
      const query = { ...payload };
      const { id } = yield select(state=>state.account);
      if(!id){  return false }
      query.userId = id;
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
        if (pathname === '/resources/myApplication') {
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
