import { getOverview } from "services/DirectoryOverview";
export default {
  namespace: "exchangDataModel",
  state: {
    pagination: {
      current: 1,
      pageSize: 10
    },
    options: [],
    text: "",
    data: [],
    loading: false,
    dataList: [],
    datalistTo: [],
    exCount: "",
    taskName: "",
    deptName: "",
    taskType: "",
    status: ""
  },
  reducers: {
    setMetaId(state, action) {
      return {
        ...state,
        ...action.payload
      };
    },
   // 打开弹框数据
    showModel(state, action) {
      return {
        ...state,
        ...action.payload,
        visibles: true
      };
    },
    //关闭弹框数据
    hideModel(state, action) {
      return {
        ...state,
        ...action.payload,
        visibles: false
      };
    }
  },
  //根据路由查询数据
  subscriptions: {
    setup({ history, dispatch }) {
      return history.listen(({ pathname, query }) => {
        if (pathname === "/resources/exchange/report/exchangeData") {
          dispatch({
            type: "getList",
            payload: {
              ...query,
              pageNum: query.page ? query.page : 1,
              pageSize: query.pageSize ? query.pageSize : 10
            }
          });
        }
      });
    }
  },

  effects: {
    *getList({ payload }, { call, select, put }) {
      // yield put({ type: "save", payload: { loading: true } });      
      const { data } = yield call(getOverview, { ...payload});
      const { code } = data;
      
      if (code === "200") {
        yield put({
          type: "setMetaId",
          payload: {
            data: data.data.results,
            pagination: {
              current: 1,
              pageSize: 10,
              total: data.data.total
            },
            loading: false
          }
        });
      }
    }
  }
};