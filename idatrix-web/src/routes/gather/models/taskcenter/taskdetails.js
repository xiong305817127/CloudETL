import { get_TransRecords, get_TransLog } from "../../../../services/gather";
import { get_JobRecords, get_JobLog } from "../../../../services/gather1";

export default {
  namespace: "taskdetails",
  state: {
    visible: false,
    visible1: false,
    visible2: false,

    //选择时间查看日志
    visible3: false,
    record: {},

    excute: {},
    records: [],
    logs: "",
    status: "",
    name: ""
  },
  reducers: {
    showExcute(state, action) {
      return {
        ...state,
        ...action.payload
      };
    },
    setRecords(state, action) {
      return {
        ...state,
        ...action.payload,
        visible: true
      };
    },
    setLogs(state, action) {
      return {
        ...state,
        ...action.payload,
        visible: false,
        visible1: true
      };
    },
    closeRecord(state, action) {
      return {
        ...state,
        visible: false
      };
    },
    closeLog(state, action) {
      return {
        ...state,
        visible1: false,
        visible: true
      };
    }
  },
  effects: {
    *queryJobHistory({ payload }, { select, call, put }) {
      const { data } = yield get_JobRecords(payload);
      const { code } = data;
      if (code === "200") {
        yield put({
          type: "setRecords",
          payload: {
            records: data.data.records,
            status: "job",
            name: payload.name
          }
        });
      }
    },
    *queryJobLog({ payload }, { select, call, put }) {
      const { data } = yield get_JobLog(payload);
      console.log(data);
      const { code, logs } = data;
      if (code === "200") {
        yield put({
          type: "setLogs",
          payload: {
            logs: decodeURIComponent(data.data.logs),
            visible3: false
          }
        });
      }
    },
    *queryTransHistory({ payload }, { select, call, put }) {
      const { data } = yield get_TransRecords(payload);
      console.log(data);
      const { code } = data;
      if (code === "200") {
        yield put({
          type: "setRecords",
          payload: {
            records: data.data.records,
            status: "trans",
            name: payload.name
          }
        });
      }
    },
    *queryTransLog({ payload }, { select, call, put }) {
      const { data } = yield get_TransLog(payload);
      console.log(data);
      const { code } = data;
      if (code === "200") {
        yield put({
          type: "setLogs",
          payload: {
            logs: decodeURIComponent(data.data.logs),
            visible3: false
          }
        });
      }
    }
  }
};
