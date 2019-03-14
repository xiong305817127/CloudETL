/**
 * Created by Administrator on 2017/4/10.
 * log日志
 */
import { message } from "antd";
import { Job_exec_configuration } from "../../../../../services/gather1";

export default {
  namespace: "jobdebug",
  state: {
    visible: "none",
    viewId: "",
    transName: "",
    model: ""
  },
  reducers: {
    save(state, action) {
      return {
        ...state,
        ...action.payload
      };
    },
    openDebug(state, action) {
      return {
        ...state,
        ...action.payload,
        model: "openDebug"
      };
    },
    cleanDebug(state, action) {
      return {
        visible: "none",
        viewId: "",
        transName: "",
        model: "cleanDebug"
      };
    },
    changeTabs(state, action) {
      return {
        ...state,
        visible: "none",
        viewId: action.viewId,
        transName: action.transName,
        model: "cleanDebug"
      };
    }
  },
  effects: {
    *executeJob({ payload }, { select, call, put }) {
      const { activeArgs } = yield select(state => state.jobheader);
      const { data } = yield Job_exec_configuration({
        ...payload.obj,
        owner: activeArgs.get(payload.obj.name).owner
      });
      const { code } = data;
      if (code === "200") {
        yield put({
          type: "openDebug",
          payload: {
            viewId: payload.viewId,
            transName: payload.actionName,
            visible: "block"
          }
        });
        message.success("开始执行");
      }
    }
  }
};
