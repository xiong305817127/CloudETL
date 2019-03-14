/**
 * 元数据定义 - ES索引类model
 */

import { deepCopy } from 'utils/utils';
import { DEFAULT_PAGE_SIZE } from 'constants';
import { getESList, getESDetail } from 'services/metadataDefine';

const immutableState = {
  source: { // 基本列表
    list: [],
    total: 0,
  },
  view: {
    viewTabs: [],
  },
  viewTabs: [], // 表字段
  viewMode: 'read', // 查看详情模式  read, new, edit
  editorVisible: false, // 编辑器显示开关
}

export default {

  namespace: 'metaES',

  state: {
    ...deepCopy(immutableState),
  },

  effects: {
    // 获取ES列表
    *getList({ payload }, { put, select }) {
      const query = {
        page: payload.page || 1,
        rows: payload.pageSize || DEFAULT_PAGE_SIZE,
      }
      const formData = {
        dept: payload.dept,
        keyword: payload.keyword,
      }
      const { data } = yield getESList(query, formData);
      const list = data && data.data && data.data.rows || [];
      const total = data && data.data && data.data.total || 0;
      yield put({ type: 'save', payload: { source: { list, total } } });
    },
    // 查询ES详情
    *view({ id }, { put, select }) {
      if (id) { // 修改/查看
        const { metaES } = yield select(state => state);
        const view = metaES.source.list.find(row => row.indexId === id);
        view.viewTabs = [];
        yield put({ type: 'save', payload: { view, viewTabs: [] } });
        const { data } = yield getESDetail({ indexId: id }); // 获取字段列表
        const viewTabs = data && data.data && data.data.tabs || [];
        view.viewTabs = viewTabs;
        yield put({ type: 'save', payload: { view, viewTabs } });
      } else { // 新增
        const view = deepCopy(immutableState.view);
        yield put({ type: 'save', payload: { view, viewTabs: [] } });
      }
    },
  },

  subscriptions: {
    setup({ history, dispatch }) {
      return history.listen(({ pathname, query }) => {
        if (pathname === '/MetadataDefine' && query.model === 'es') {
          dispatch({ type: 'getList', payload: query });
        }
      });
    }
  },

  reducers: {
    showEditor(state, action) {
      return { ...state, editorVisible: true };
    },
    hideEditor(state, action) {
      return { ...state, editorVisible: false };
    },
    save(state, action) {
      return { ...state, ...action.payload };
    },
  },

};
