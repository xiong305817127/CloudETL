/*云化数据设计平台*/
import { getTrans_list } from "../../../../services/gather";
import { getJob_list } from "../../../../services/gather1";

export default {
  namespace: "designplatform",
  state: {
    status: "trans",
    transList: {},
    jobList: {},
    copyTransItme: null,
    copyJobItme: null
  },
  reducers: {
    changeList(state, action) {
      console.log(action);

      return { ...state, ...action.payload };
    },
    changeStatus(state, action) {
      return { ...state, ...action.payload };
    },
    changeTransView(state, action) {
      return { ...state, transview: action.transview };
    },
    changeJobView(state, action) {
      return { ...state, jobview: action.jobview };
    }
  },
  effects: {
    *copyCache({ payload }, { select, put }) {
      let owner = "";
      if (payload.copyTransItme) {
        const data = yield select(state => state.transheader);
        owner = data.owner;
        yield put({
          type: "changeList",
          payload: { copyTransItme: { ...payload.copyTransItme, fromOwner: owner } }
        });
      } else {
        const data = yield select(state => state.jobheader);
        owner = data.owner;
        yield put({
          type: "changeList",
          payload: { copyJobItme: { ...payload.copyJobItme, fromOwner: owner } }
        });
      }
    },
    *queryJobList({ payload }, { select, call, put }) {
      const { data } = yield getJob_list({
        isOnlyName: true,
        isMap: payload.isMap
      });
      const { username } = yield select(state => state.account);
      const { code } = data;
      if (code === "200") {
        yield put({
          type: "changeList",
          payload: {
            jobList: data.data
          }
        });
      }
    },
    *queryTransList({ payload }, { select, call, put }) {
      const { data } = yield getTrans_list({
        isOnlyName: true,
        isMap: payload.isMap
      });
      const { username } = yield select(state => state.account);
      const { code } = data;
      if (code === "200") {
        yield put({
          type: "changeList",
          payload: {
            transList: data.data
          }
        });
      }
    }
  }
};
