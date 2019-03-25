/**
 * 执行分析model (有单个执行与批量执行两张形势)
 * @author pwj 2018/9/28
 */
import {
  getDefaultEngineList,
  editTransAttributes,
  execBatchTrans,
  Trans_exec_configuration
} from "services/quality";
import { message } from "antd";
import { routerRedux } from "dva/router";

const initState = {
  //是否显示弹框
  visible: false,
  //执行引擎列表
  executeList: [],
  //执行引擎方式
  model: "default",
  //执行方式 default  普通执行  batch批量执行
  runType: "default",
  //附带的执行参数
  params: {},
  //执行转换的名称
  actionName: "",

  dataSource: [],
  items: [],
  viewId: ""
};

/*提示框消息模块*/
export default {
  namespace: "runAnalysis",
  state: { ...initState },
  reducers: {
    save(state, action) {
      return { ...state, ...action.payload };
    },
    cancel(state, action) {
      state.executeList.splice(0);
      state.dataSource.splice(0);
      state.items.splice(0);
      state.params = {};

      return { ...initState };
    },
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

        model: "default",
        runModel: "default",
        actionName: "",
        viewId: "",
        executeList: [],
        selectedRows: [],
        dataSource: []
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
    /**
     * 默认非批量执行时，打开转换
     */
    *openRunAnalysis({ payload }, { select, call, put }) {
      yield put({ type: "save", payload });
      let itemsArgs = [];
      let newParams = {};
      if (payload.runType !== "batch") {
        const { items } = yield select(state => state.transspace);
        itemsArgs = items;
        const { data } = yield call(editTransAttributes, {
          name: payload.actionName
        });
        const { code } = data;
        if (code === "200") {
          newParams = data.data.params ? data.data.params : {};
        }
      }
      const { data } = yield call(getDefaultEngineList);
      const { code } = data;
      if (code === "200") {
        yield put({
          type: "save",
          payload: {
            params: newParams,
            items: itemsArgs,
            executeList: data.data
          }
        });
      }
    },
    *batchRun({ payload }, { call, put, select }) {
      const { selectedRows } = yield select(state => state.qualityAnalysis);
      let args = [];
      for (let index of selectedRows) {
        args.push(index.name);
      }
      const { data } = yield call(execBatchTrans, {
        names: args,
        configuration: payload
      });
      const { code } = data;
      if (code === "200") {
        message.success("批量执行成功！");
        yield put(routerRedux.push("/gather/qualityAnalysis/taskList"));
        yield put({ type: "cancel" });
      }
    },
    *runAnalysis({ payload }, { call,select, put }) {
      const { owner } = yield select(state => state.transheader);
      const { data } = yield call(Trans_exec_configuration, {
        ...payload,
        owner
      });
      const { code } = data;
      if (code === "200") {
        yield put({
          type: "analysisInfo/initInfo",
          payload: { name: payload.name }
        });
        yield put({ type: "cancel" });
      }
    }
  }
};
