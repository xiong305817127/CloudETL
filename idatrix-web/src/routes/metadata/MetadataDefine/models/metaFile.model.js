/**
 * 元数据定义 - 文件目录类model
 */

import { deepCopy } from 'utils/utils';
import { DEFAULT_PAGE_SIZE } from 'constants';
import { getDirectoryList } from 'services/metadataDefine';

const immutableState = {
  source: { // 基本列表
    list: [],
    total: 0,
  },
  view: {},
  viewMode: 'read', // 查看详情模式  read, new, edit
  editorVisible: false, // 编辑器显示开关
  viewVisible: false, // 预览窗口显示开关
}

export default {

  namespace: 'metaFileDefine',

  state: {
    ...deepCopy(immutableState),
  },

  effects: {
    *getList({ payload }, { put, select }) {
      const { account } = yield select(state => state);
      const query = {
        page: payload.page || 1,
        rows: payload.pageSize || DEFAULT_PAGE_SIZE,
      }
      const formData = {
        // dept: payload.dept,
        dept: Array.isArray(payload.dept) ? payload.dept[payload.dept.length - 1] : payload.dept,
        keyword: payload.keyword,
        metaNameCn: '',
        metaType: payload.metaType || 1,
        renterId: account.renterId,
      }
      const { data } = yield getDirectoryList(query, formData);
      const list = data && data.data && data.data.rows || [];
      const total = data && data.data && data.data.total || 0;
      yield put({ type: 'save', payload: { source: { list, total } } });
    }
  },

  subscriptions: {
    setup({ history, dispatch }) {
      return history.listen(({ pathname, query }) => {
        if (pathname === '/MetadataDefine' && query.model === 'file') {
          dispatch({ type: 'getList', payload: query });
        }
      });
    }
  },

  reducers: {
    // 查询文件详情
    view(state, action) {
      const { id } = action;
      if (id) { // 修改
        const view = state.source.list.find(row => row.fileid === id);
        return { ...state, view, viewFields: [] }; // 设viewFields为空是为了刷新数据
      } else { // 新增
        const view = deepCopy(immutableState.view);
        return { ...state, view };
      }
    },
    showEditor(state, action) {
      return { ...state, editorVisible: true };
    },
    hideEditor(state, action) {
      return { ...state, editorVisible: false };
    },
    showView(state, action) {
      return { ...state, viewVisible: true };
    },
    hideView(state, action) {
      return { ...state, viewVisible: false };
    },
    save(state, action) {
      return { ...state, ...action.payload };
    },
  },

};
