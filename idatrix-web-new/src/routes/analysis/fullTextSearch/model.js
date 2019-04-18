/**
 * 元数据定义 - ES索引类model
 */

import Immutable from "immutable";
import { getSolrOptions } from "services/analysisFullTextSearch";

const immutableState = Immutable.fromJS({
  options: []
});

export default {
  namespace: "FullTextSearch",

  state: immutableState,

  effects: {
    // 获取索引选项列表
    *getOptions({}, { put }) {
      const { data } = yield getSolrOptions();
      const { code } = data;
      const options = code === "200" && data.data ? data.data : [];
      yield put({ type: "save", payload: { options } });
    }
  },

  subscriptions: {
    setup({ history, dispatch }) {
      return history.listen(({ pathname }) => {
        if (
          pathname === "/analysis/FullTextSearch" ||
          pathname === "/analysis/FullTextSearch/custom"
        ) {
          dispatch({ type: "getOptions" });
        }
      });
    }
  },

  reducers: {
    save(state, action) {
      return state.merge(action.payload);
      // return { ...state, ...action.payload };
    }
  }
};
