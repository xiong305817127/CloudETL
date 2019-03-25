import {
  getCluster_list,
  edit_stepConfigs,
  save_stepConfigs
} from "../../../services/gather";
import { message } from "antd";

/*节点模块配置*/
export default {
  namespace: "domconfig",
  state: {
    visible: false,
    transName: "",
    stepName: "",
    configs: {
      clusterSchema: "",
      distribute: ""
    },
    clusterList: []
  },
  reducers: {
    queryItem(state, action) {
      console.log(action);
      console.log({ ...state, ...action.domInfo, visible: true });
      return { ...state, ...action.domInfo, visible: true };
    },
    queryClusterList(state, action) {
      console.log({ ...state, ...action.domInfo });
      return { ...state, ...action.domInfo };
    },
    show(state, action) {
      return {
        ...state,
        visible: action.visible
      };
    },
    hide(state, action) {
      return {
        visible: false,
        transName: "",
        stepName: "",
        configs: {
          clusterSchema: "",
          distribute: ""
        },
        clusterList: []
      };
    }
  },
  effects: {
    *query({ obj }, { select, call, put }) {
      const { owner } = yield select(state => state.transheader);
      const { data } = yield edit_stepConfigs({ ...obj, owner });
      console.log(data);
      const { code } = data;
      if (code === "200") {
        yield put({
          type: "queryItem",
          domInfo: {
            transName: data.data.transName,
            stepName: data.data.stepName,
            configs: {
              clusterSchema: data.data.configs.clusterSchema,
              distribute: data.data.configs.distribute
            }
          }
        });
      }
    },
    *queryList({ domInfo }, { select, call, put }) {
      const { owner } = yield select(state => state.transheader);
      const { data } = yield call(getCluster_list, { owner });
      const { code } = data;
      if (code === "200") {
        yield put({
          type: "queryClusterList",
          domInfo: {
            clusterList: data.data
          }
        });
      }
    },
    *saveList({ obj }, { select, call, put }) {
      const { owner } = yield select(state => state.transheader);
      const { data } = yield save_stepConfigs({ ...obj, owner });
      console.log(data);
      if (data) {
        const { code } = data;
        if (code === "200") {
          message.success("保存成功！");
        }
      }
      yield put({
        type: "show",
        visible: false
      });
    }
  }
};
