import { getTerminalManageRecordsByCondition } from 'services/DirectoryOverview';
export default {
  namespace: 'frontModel',
  state: {
    pagination: {
      current: 1,
      pageSize: 10
    },
    options: [],
    text: "",
    data: [],
    loading: false,
    visible: false,

    dataServer: [],
    database: [],
    tmDBPort: "",
    tmDBType: "",
    model: "",
    serverip: "",
    dbUser: "",
    //保存提供方代码
    deptCode: "",
    newidTy: "",
    //保存提供方名字
    deptName: "",
    deptId: 0,
    deptList: [],
    total: 0,
    sFTPbase: [],
    Sid: "",
    tmDBId: "",
    tmDBName: "",
    tmName: "",
    schemaList:[], //模式名称
  },
  reducers: {
    setMetaId(state, action) {
      return {
        ...state,
        ...action.payload
      };
    },
    showModel(state, action) {
      return {
        ...state,
        ...action.payload,
        visible: true

      };
    },
    hideModel(state, action) {
      return {
        ...state,
        ...action.payload,
        dataServer: [],
        database: [],
        deptList: [],
        sFTPbase: [],
        text: "",
        visible: false,
      };
    }
  },
  subscriptions: {
    setup({ history, dispatch }) {
      return history.listen(({ pathname, query }) => {
        if (pathname === "/resources/exchange/front") {
          dispatch({
            type: "getList",
            payload: {
              ...query,
              pageNum: query.page ? query.page : 1,
              pageSize: query.pageSize ? query.pageSize : 10
            }
          })
        }
      })
    }
  },

  effects: {
    *getList({ payload }, { call, put }) {
      yield put({ type: "save", payload: { loading: true } })
      const { data } = yield call(getTerminalManageRecordsByCondition, { ...payload });
      const { code } = data;
      if (code === "200") {
        yield put({
          type: 'setMetaId',
          payload: {
            data: data.data.results,
            pagination: {
              current: 1,
              pageSize: 10
            },
            total: data.data.total,
            loading: false
          }
        })
      }
    },

  }
};
