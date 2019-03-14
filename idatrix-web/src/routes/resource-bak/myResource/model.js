import { searchTable, searchFile, getMeta, getDir } from '../../../services/myResource';

const Timer = (time)=>{
  return new Promise((resolve)=>{
    setTimeout(()=>{
      resolve();
    },time)
  })
}


export default {

  namespace: 'myResource',

  state: {
    list: [],
    result: {},
    resultType: 'table',
    ownerType: '1',
    viewTable: [],
  },

  effects: {

    // 查询
    *search({ payload }, { put,select }) {
      Timer(300);
      const { username } = yield select(state=>state.account);
      if(!username){ return }
      const type = payload.type || 'table';
      const query = payload.query;
      query.userId = '';
      query.owner = username;
      query.renterId = '';
      yield put({ type: 'save', payload: { result: { rows: [], total: 0 }, resultType: type, ownerType: query.ownerType } }); // 快速切换页签
      const { data } = yield type === 'table' ? searchTable(query) : searchFile(query);
      const result = data && data.data || { rows: [] };
      if (result.rows) {
        result.rows = result.rows.filter(row => row !== null);
        result.rows.forEach(row => row.rowId = row.metaid ? row.metaid : row.fileid);
      }
      yield put({ type: 'save', payload: { result, resultType: type, ownerType: query.ownerType || '1' } });
    },

    // 获取表格详情
    *getMeta({ payload }, { put }) {
      const { data } = yield getMeta(payload);
      yield put({ type: 'save', payload: { viewTable: data.data } });
    },

    // 获取目录详情
    *getDir({ payload }, { put }) {
      const { data } = yield getDir(payload);
      yield put({ type: 'save', payload: { viewTable: data.data } });
    },
  },

  subscriptions: {
    setup({ history, dispatch }) {
      return history.listen(({ pathname, query }) => {
        if (pathname === '/resources/myResource') {
          dispatch({
            type: 'search',
            payload: {
              type: query.type || 'table',
              query: {
                dept: '',
                ownerType: query.ownerType || '1',
                owner: '',
                keyword: query.keyword || '',
                page: query.page,
                rows: query.pageSize,
              },
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
