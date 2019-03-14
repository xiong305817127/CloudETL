/**
 * Created by Administrator on 2017/8/29.
 */
import { message } from "antd";
import { getNew_job, getDelete_job } from "../../../../../services/gather1";
import * as FuncsList from "../../../../../services/gather1";
import { CurryFunc } from "../../../../../utils/utils";

export default {
  namespace: "jobheader",
  state: {
    model: "welcome",
    activeArgs: new Map(),
    activeKey: "",
    //要删除的名字
    removeKey: "",

    //组件是否需要更新
    shouldUpdate: false,
    //是否有任务
    hasTask: false,
    //新建任务
    newFile: false,
    //弹窗展示
    modelVisible: false,

    // 方法集合
    // curry后第一个参数为owner
    methods: {}
  },
  reducers: {
    openFile(state, action) {
      window.location.href = "#/gather/designplatform";

      let args = state.activeArgs;
      if (!state.activeArgs.has(action.payload.activeKey)) {
        let viewId = jsPlumbUtil.uuid();
        args.set(action.payload.activeKey, {
          viewId,
          owner: action.payload.owner
        });
      }
      return {
        ...state,
        ...action.payload,
        model: "view",
        activeArgs: args,
        shouldUpdate: true,
        hasTask: true,
        owner: action.payload.owner,
        methods: CurryFunc(FuncsList, { owner: action.payload.owner })
      };
    },
    newFile(state, action) {
      window.location.href = "#/gather/designplatform";
      let args = state.activeArgs;
      args.set(action.payload.activeKey, {
        viewId: action.payload.viewId,
        owner: action.payload.owner
      });
      return {
        ...state,
        model: "view",
        activeKey: action.payload.activeKey,
        activeArgs: args,
        newFile: true,
        hasTask: true,
        owner: action.payload.owner,
        methods: CurryFunc(FuncsList, { owner: action.payload.owner })

      };
    },
    changeModel(state, action) {
      let activeKey = action.payload.activeKey;
      let args = state.activeArgs;

      return  activeKey ? {
        ...state,
        ...action.payload,
        model: "view",
        shouldUpdate: false,
        newFile: true,
        hasTask: true,
        owner: args.has(activeKey) ? args.get(activeKey).owner : "",
        methods: CurryFunc(FuncsList, {
          // 切换model自动刷新owner
          owner: args.has(activeKey) ? args.get(activeKey).owner : ""
        })
      }:{
        ...state,
        ...action.payload,
      };
    },
    changeName(state, action) {
      let args = state.activeArgs;
      args.set(action.payload.newname, args.get(action.payload.name));
      args.delete(action.payload.name);

      return {
        ...state,
        activeArgs: args,
        activeKey: action.payload.newname
      };
    },
    closeModel(state, action) {
      let args = state.activeArgs;
      let hasTask = state.hasTask;
      let shouldUpdate = state.shouldUpdate;
      let activeKey = state.activeKey;

      if (action.payload.removeKey === state.activeKey) {
        args.delete(action.payload.removeKey);
        let panels = [...args.keys()];
        if (args.size > 0) {
          activeKey = panels[panels.length - 1];
          shouldUpdate = true;
        } else {
          activeKey = "";
        }
      } else {
        args.delete(action.payload.removeKey);
      }

      if (args.size === 0) {
        hasTask = false;
      }

      return {
        ...state,
        activeArgs: args,
        hasTask: hasTask,
        activeKey: activeKey,
        shouldUpdate: shouldUpdate,
        modelVisible: false,
        owner: args.has(activeKey) ? args.get(activeKey).owner : "",
        methods: CurryFunc(FuncsList, {
          // 关闭model自动刷新owner
          owner: args.has(activeKey) ? args.get(activeKey).owner : ""
        })
      };
    },
    clearHeader(state, action) {
      state.activeArgs.clear();
      return {
        model: "welcome",
        activeArgs: new Map(),
        activeKey: "",
        //要删除的名字
        removeKey: "",
        owner: "",
        //组件是否需要更新
        shouldUpdate: false,
        //是否有任务
        hasTask: false,
        //新建任务
        newFile: false,
        //弹窗展示
        modelVisible: false,

        methods: {}
      };
    }
  },
  effects: {
    *deleteTrans({ payload }, { select, call, put }) {
      const { data } = yield getDelete_job(payload.removeKey);
      const { code } = data;
      if (code === "200") {
        yield put({
          type: "closeModel",
          payload: {
            removeKey: payload.removeKey
          }
        });
        message.success("删除成功");
      }
    },
    *saveNewTrans({ payload }, { select, call, put }) {
      const { username } = yield select(state => state.account);
      const { data } = yield getNew_job({
        ...payload,
        group: "default",
        owner: username
      });
      const { code } = data;
      if (code === "200") {
        message.success("新建调度任务成功");

        // 新建成功后，刷新下拉框
        yield put({
          type: "designplatform/queryJobList",
          payload: { isMap: true }
        });

        if (payload.copy_name) {
          yield put({
            type: "openFile",
            payload: {
              activeKey: payload.info_name,
              owner: username
            }
          });
        } else {
          yield put({
            type: "newFile",
            payload: {
              activeKey: payload.info_name,
              viewId: payload.viewId,
              owner: username
            }
          });
        }
      }
    }
  }
};
