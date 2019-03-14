/**
 * 元数据定义 - 数据表类草稿箱model
 */

import { DEFAULT_PAGE_SIZE } from 'constants';
import { deepCopy } from 'utils/utils';
import { getDraftsList } from 'services/metadataDefine';

const immutableState = {
  list: [],
  total: 0,
};

export default {

  namespace: 'metaDataDrafts',

  state: {
    ...deepCopy(immutableState),
  },

  effects: {
    // 查询数据表列表
    *getList({ payload }, { put, select }) {
      const { account } = yield select(state => state);
      const query = {
        page: payload.page || 1,
        rows: payload.pageSize || DEFAULT_PAGE_SIZE,
      };
      const formData = {
        creator: account.username,
        keyword: payload.keyword,
        dept: payload.dept,
      };
      const { data } = yield getDraftsList(query, formData);
      const list = data && data.data ? data.data : [];
      const total = data && data.data  ? data.data.length : 0;

      console.log("显示！！！",list,total);
      yield put({ type: 'save', payload: { list, total } });
    },
  },

  subscriptions: {
    setup({ history, dispatch }) {
      return history.listen(({ pathname, query }) => {
        if (pathname === '/MetadataDefine/drafts') {
          dispatch({ type: 'getList', payload: query });
        }
      });
    }
  },

  reducers: {
    save(state, action) {
      return { ...state, ...action.payload };
    },
  },

};
