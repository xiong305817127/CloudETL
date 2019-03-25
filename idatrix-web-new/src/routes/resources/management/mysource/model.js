import { getResultList, getHistoryList, getCheckList, deleteInfo, getSubmitInfo, fileResourceImport } from 'services/catalog';
import { routerRedux } from 'dva/router';
import { message } from 'antd';

export default {
  namespace: "MySourceModel",

  state: {
    datasource: [],
    total: 0,
    selectedRowKeys: [],
    selectedRows: [],

    //点击名称
    resourceName: "",
    //历史变更
    HistoryChangeShow: false,
    datasource1: [],
    //检查历史
    CheckHistoryShow: false,
    datasource2: [],

    InsterShow: false,
    loading: false,

    //提交注册是否可用
    canSubmit: false
  },

  reducers: {
    save(state, action) {
      return { ...state, ...action.payload }
    }
  },
  subscriptions: {
    setup({ history, dispatch }) {
      return history.listen(({ pathname, query }) => {
        if (pathname === "/resources/management/mysource") {
          dispatch({
            type: "getList", payload: {
              ...query,
              page: query.page ? query.page : 1,
              pageSize: query.pageSize ? query.pageSize : 10
            }
          })
        }
      })
    }
  },
  effects: {
    *deleteInfo({ payload }, { call, select, put }) {

      const { MySourceModel } = yield select(state => state);

      const { data } = yield call(deleteInfo, { ...payload });

      const { code } = data;

      if (code === "200") {
        message.success("删除成功！");

        MySourceModel.selectedRowKeys.splice(0);
        MySourceModel.selectedRows.splice(0);

        yield put(routerRedux.push("/resources/management/mysource"));
        yield put({ type: 'save', payload: { selectedRowKeys: [], selectedRows: [], canSubmit: false } });
      }
    },
    *getSubmitInfo({ payload }, { call, select, put }) {

      const { MySourceModel } = yield select(state => state);
      const { data } = yield call(getSubmitInfo, { ...payload });

      const { code } = data;

      if (code === "200") {
        message.success("批量提交审核成功！");

        MySourceModel.selectedRowKeys.splice(0);
        MySourceModel.selectedRows.splice(0);

        yield put(routerRedux.push("/resources/management/mysource"));
        yield put({ type: 'save', payload: { selectedRowKeys: [], selectedRows: [], canSubmit: false } });
      }
    },
    *getList({ payload }, { call, select, put }) {
      yield put({ type: "save", payload: { loading: true } });
      const { data } = yield call(getResultList, { ...payload });

      const { code } = data;

      if (code === "200") {
        yield put({
          type: 'save',
          payload: {
            datasource: data.data ? data.data.results : [],
            total: data.data ? data.data.total : 0,
            loading: false
          }
        })
      }
    },
    *getHistory({ payload, resourceName }, { call, select, put }) {
      const { data } = yield call(getHistoryList, { ...payload });
      const { code } = data;

      if (code === "200") {
        let args = [];
        let num = 1;
        if (data.data) {
          for (let index of data.data) {
            args.push({ key: num++, ...index })
          }
        }
        yield put({
          type: 'save',
          payload: {
            datasource1: args,
            HistoryChangeShow: true,
            resourceName
          }
        })
      }
    },
    *getCheck({ payload, resourceName }, { call, select, put }) {
      const { data } = yield call(getCheckList, { ...payload });
      const { code } = data;

      if (code === "200") {
        let args = [];
        let num = 1;
        if (data.data) {
          for (let index of data.data) {
            args.push({ key: num++, ...index })
          }
        }
        yield put({
          type: 'save',
          payload: {
            datasource2: args,
            CheckHistoryShow: true,
            resourceName
          }
        })
      }
    },
    *fileResourceImport({ payload }, { put, call }) {
      const hide = message.loading('正在导入新增资源..', 0);
      const { data } = yield call(fileResourceImport, { ...payload });
      const { code } = data;
      if (code === "200") {
        message.success("新增资源导入成功！");
        hide();
        yield put(routerRedux.push("/resources/management/mysource"));
      } else {
        message.error("新增资源导入失败！");
        hide();
      }
    }
  }
}