import {
  getServer_list,
  getDefaultEngineList
} from "../../../../../services/gather";
import {
  Job_exec_configuration,
  execBatchJob,
  getEdit_job_attributes
} from "../../../../../services/gather1";
import { message } from "antd";

/*提示框消息模块*/
export default {
  namespace: "runjob",
  state: {
    visible: false,
    actionName: "",
    viewId: "",
    serverList: [],
    model: "execLocal",
    runModel: "default",
    selectedRows: [],
    params: {},
    dataSource: [],
    items: [],
    owner: ""
  },
  reducers: {
    show(state, action) {
      return {
        ...state,
        ...action.payload
      };
    },
    hide(state, action) {
      return {
        ...state,
        visible: action.visible,
        actionName: "",
        viewId: "",
        serverList: [],
        runModel: "default",
        selectedRows: [],
        dataSource: [],
        params: {}
      };
    },
    changeModel(state, action) {
      return {
        ...state,
        model: action.model
      };
    }
  },
  effects: {
    *queryServerList({ payload }, { select, call, put }) {
      const { items } = yield select(state => state.jobspace);
      //const { data } = yield call(getServer_list);
      const data1 = yield call(getDefaultEngineList);
      const { activeArgs } = yield select(state => state.jobheader);
      const data2 = yield getEdit_job_attributes({
        name: payload.actionName,
        owner: activeArgs.get(payload.actionName).owner
      });

      const { params } = data2.data.data;

      const { code } = data1.data;
      if (code === "200") {
        yield put({
          type: "show",
          payload: {
            ...payload,
            params: params,
            serverList: data1.data.data,
            items: items
          }
        });
      }
    },
    *queryBatchList({ payload }, { select, call, put }) {
      //const { data } = yield call(getServer_list);
      const { data } = yield call(getDefaultEngineList);

      const { code } = data;

      const filteredSelectedRows = payload.selectedRows.filter(val=>val.owner === payload.owner);
      
      if(filteredSelectedRows.length > 0){
        if (code === "200") {
          yield put({
            type: "show",
            payload: {
              ...payload,
              serverList: data.data,
            }
          });
        }
      }else{
        message.info("请选择至少一个当前用户的任务！");
      }
    },
    *batchRun({ payload }, { select, call, put }) {
      yield put({
        type: "hide"
      });

      let args = [];
      for (let index of payload.selectedRows) {
        args.push(index.name);
      }

      const {data} = yield execBatchJob({
        names: args,
        configuration: payload.configuration,
        owner: payload.owner
      });

      if(data.code === "200"){
        message.success("执行提交成功，请稍后刷新页面。");
      }
    }
  }
};
