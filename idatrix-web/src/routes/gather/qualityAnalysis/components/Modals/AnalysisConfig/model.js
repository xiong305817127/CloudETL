import {
  getCluster_list,
  edit_stepConfigs,
  save_stepConfigs
} from "services/quality";
import { message } from "antd";

const initState = {
  //modal是否显示
  visible: false,
  //转换名称
  transName: "",
  //节点名称
  stepName: "",
  //配置
  configs: {
    clusterSchema: "",
    distribute: ""
  },
  //集群列表
  clusterList: []
};

/*节点模块配置*/
export default {
  namespace: "analysisConfig",
  state: { ...initState },
  reducers: {
    save(state, action) {
      return { ...state, ...action.payload };
    },
    reset() {
      return { ...initState };
    }
  },
  effects: {
    *editConfig({ payload }, { call, put }) {
      const { owner } = yield select(state => state.transheader);
      const { data } = yield call(edit_stepConfigs, { ...payload, owner });
      const { code } = data;
      if (code === "200") {
        yield put({
          type: "save",
          payload: { ...data.data, visible: true }
        });
        yield put({ type: "queryClusterList" });
      }
    },
    *queryClusterList({}, { call, put }) {
      const { data } = yield call(getCluster_list);
      const { code } = data;
      if (code === "200") {
        yield put({
          type: "save",
          payload: {
            clusterList: data.data
          }
        });
      }
    },
    *saveConfig({ payload }, { select, call, put }) {
      const { transName, stepName } = yield select(
        state => state.analysisConfig
      );
      const { owner } = yield select(state=>state.transheader);
      const { data } = yield call(save_stepConfigs, {
        transName,
        stepName,
        owner,
        configs: { ...payload }
      });
      if (data) {
        const { code } = data;
        if (code === "200") {
          message.success("保存成功！");
          yield put({ type: "reset" });
        }
      }
    }
  }
};
