import {
  getAllResource,
  searchTable,
  searchFile,
  getMeta,
  getDepartment,
  getPermitsList,
  getPermitsResults,
  queryFieldRelation
} from '../../../services/dataResource';
import { deepCopy } from 'utils/utils';

const Timer = (time)=>{
  return new Promise((resolve)=>{
    setTimeout(()=>{
      resolve();
    },time)
  })
}

export default {

  namespace: 'dataResource',

  state: {
    resource: {},
    originResource: [],
    result: {},
    resultType: 'table',
    departments: [],
    viewTable: [],
    permitsList: [],
    permitsResults: [],
    fieldRelations: [],
  },

  effects: {
    // 获取权限列表
    *getPermitsList({ payload }, { call, put }) {
      const { data } = yield getPermitsList(payload);
      yield put({ type: 'save', payload: { permitsList: data.data || [] } });
    },

    // 获取权限结果
    *getPermitsResults({ payload }, { call, put }) {
      const { data } = yield getPermitsResults(payload);
      yield put({ type: 'save', payload: { permitsResults: data.data || [] } });
    },

    // 高级搜索页
    *getAllResource({ payload }, { call, put }) {
      const { data } = yield getAllResource(payload);
      const list = data && data.data || [];
      const resource = {};
      let currentType = '';
      let currentCapital = '';
      list.sort((a, b) => a.prefix > b.prefix); // 按字母排序
      list.sort((a, b) => a.type < b.type); // 按分类排序
      list.forEach((item) => {
        if (currentType !== item.type) {
          currentType = item.type;
          currentCapital = item.prefix;
          if (!resource[currentType]) {
            resource[currentType] = {};
          }
        }
        if (currentCapital !== item.prefix) {
          currentCapital = item.prefix;
        }
        if (!resource[currentType][currentCapital]) {
          resource[currentType][currentCapital] = [];
        }
        resource[currentType][currentCapital].push(item);
      });
      yield put({ type: 'save', payload: { resource, originResource: list } });
    },

    // 查询
    *search({ payload }, { put,select }) {
      Timer(300);
      const { username } = yield select(state=>state.account); 
      if(!username){  return false }
      const type = payload.type || 'table';
      const query = deepCopy(payload.query);
      const formData = Object.assign({}, query, {
        industrys: Array.isArray(query.industrys) ? query.industrys : query.industrys && query.industrys.split(',') || [],
        tags: Array.isArray(query.tags) ?  query.tags : query.tags && query.tags.split(',') || [],
        themes: Array.isArray(query.themes) ? query.themes : query.themes && query.themes.split(',') || [],
        owner: username,
      });
      delete formData.page;
      delete formData.pageSize;
      const query2 = { page: query.page, rows: query.pageSize };
      yield put({ type: 'save', payload: { result: { row: [], total: 0 }, resultType: type } }); // 快速切换页签
      const { data } = yield type === 'table' ? searchTable(formData, query2) : searchFile(formData, query2);
      const result = data && data.data || { row: [] };
      yield put({ type: 'save', payload: { result, resultType: type } });
    },

    // 获取详情
    *getMeta({ payload }, { put }) {
      const { data } = yield getMeta(payload);
      yield put({ type: 'save', payload: { viewTable: data.data } });
    },

    // 获取组织列表
    *getDepartment({ payload }, { put }) {
      const { data } = yield getDepartment(payload);
      yield put({ type: 'save', payload: { departments: data && data.data || '[]' } });
    },

    // 获取字段关系
    *getFieldRelation({ payload }, { put }) {
      const { data } = yield queryFieldRelation(payload);
      yield put({ type: 'save', payload: { fieldRelations: data && data.data && data.data.dataFiledRelation || [] } });
    },

  },

  subscriptions: {
    setup({ history, dispatch }) {
      return history.listen(({ pathname, query }) => {
        if (pathname === '/resources/directory') {
          dispatch({
            type: 'getAllResource',
          });
        } else if (pathname === '/resources/directory/result') {
          const query2 = deepCopy(query);
          delete query2.type;
          dispatch({
            type: 'search',
            payload: { query: query2, type: query.type },
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
