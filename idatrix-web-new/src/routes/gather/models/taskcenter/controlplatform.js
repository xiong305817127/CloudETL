/**
 * Created by Administrator on 2017/12/22.
 */
import {
  getOpen_trans,
  Trans_exec_configuration,
  getTrans_exec_id,
  get_exec_stop
} from "../../../../services/gather";
import {
  getOpen_job,
  Job_exec_configuration,
  getJob_exec_id,
  getJob_exec_stop
} from "../../../../services/gather1";
import Tools from "../../components/config/Tools";
import { message } from "antd";

/**
 *
 * @param 步骤名称
 * @param 步骤数组集合
 * @returns 返回对应的[x,y]坐标
 */
const getItems = (name, args) => {
  for (let index of args) {
    if (index.name === name) {
      return index;
    }
  }
};

/**
 *
 * @param 线段数组
 * @param 步骤数组
 * @returns 返回渲染eChart线条的数据
 */
const getHopDataList = (hops, steps, yMax) => {
  let data = [];
  for (let index of hops) {
    var fromArgs = getItems(index.from, steps);
    var toArgs = getItems(index.to, steps);

    data.push({
      name: fromArgs.name + "->" + toArgs.name,
      coords: [
        [fromArgs.gui.xloc, yMax - fromArgs.gui.yloc],
        [toArgs.gui.xloc, yMax - toArgs.gui.yloc]
      ]
    });
  }
  return data;
};

/**
 *
 * @param 步骤数组
 * @returns 返回步骤数组的坐标信息
 */

const getStepDataList = (steps, yMax) => {
  let data = [];
  for (let index of steps) {
    let type = index.type;
    if (!Tools[index.type]) {
      type = "UNKNOWN";
    }
    data.push({
      name: index.name,
      value: [index.gui.xloc, yMax - index.gui.yloc],
      type: type
    });
  }

  return data;
};

const getMax = steps => {
  let xArgs = [1];
  let yArgs = [1];
  let xMax = 0;
  let yMax = 0;

  for (let index of steps) {
    xArgs.push(index.gui.xloc);
    yArgs.push(index.gui.yloc);
  }
  xArgs.sort(function(a, b) {
    return b - a;
  });
  yArgs.sort(function(a, b) {
    return b - a;
  });

  xMax = xArgs[0] + 80;
  yMax = yArgs[0] + 80;

  return [xMax, yMax];
};

export default {
  namespace: "controlplatform",
  state: {
    visible: false,
    /*打开的转换名*/
    transName: "",

    viewType: "",

    /*操作类型*/
    actionType: "openView",

    /*线坐标名称*/
    hopList: [],
    hopData: [],

    /*步骤坐标名称*/
    stepList: [],
    stepData: [],

    xyMax: [1200, 600]
  },
  reducers: {
    updateModel(state, action) {
      return {
        ...state,
        ...action.payload
      };
    },
    hideModel(state, action) {
      return {
        visible: false,
        transName: "",
        actionType: "closeView",
        hopData: [],
        stepData: []
      };
    }
  },
  effects: {
    *openTrans({ payload }, { select, call, put }) {
      const { data } = yield getOpen_trans(payload);
      const { code } = data;

      if (code === "200") {
        const { hopList, stepList } = data.data;

        let hopData = [];
        let stepData = [];

        let xyMax = getMax(stepList);

        hopData = getHopDataList(hopList, stepList, xyMax[1]);
        stepData = getStepDataList(stepList, xyMax[1]);

        yield put({
          type: "updateModel",
          payload: {
            hopData: hopData,
            stepData: stepData,
            xyMax: xyMax,
            actionType: "openView",
            ...payload
          }
        });
      }
    },
    *openJobs({ payload }, { select, call, put }) {
      const { data } = yield getOpen_job(payload.transName);
      console.log(data);

      const { code } = data;

      if (code === "200") {
        const { entryList, hopList } = data.data;

        let hopData = [];
        let stepData = [];

        let xyMax = getMax(entryList);

        hopData = getHopDataList(hopList, entryList, xyMax[1]);
        stepData = getStepDataList(entryList, xyMax[1]);

        console.log(hopData);
        console.log(stepData);

        yield put({
          type: "updateModel",
          payload: {
            hopData: hopData,
            stepData: stepData,
            xyMax: xyMax,
            actionType: "openView",
            ...payload
          }
        });
      }
    },
    *stopTrans({ payload }, { select, call, put }) {
      const data1 = yield getTrans_exec_id(payload);
      if (data1.data.code === "200") {
        const data2 = yield get_exec_stop(data1.data.executionId);
        const { code } = data2.data;
        if (code === "200") {
          message.success("执行已终止");
        }
      }
    },
    *stopJob({ payload }, { select, call, put }) {
      const data1 = yield getJob_exec_id(payload);
      if (data1.data.code === "200") {
        const data2 = yield getJob_exec_stop(data1.data.executionId);
        const { code } = data2.data;
        if (code === "200") {
          message.success("执行已终止");
        }
      }
    },
    *executeTrans({ payload }, { select, call, put }) {
      const { owner } = yield select(state => state.transheader);
      const { data } = yield Trans_exec_configuration({
        ...payload.obj,
        owner
      });
      const { code } = data;
      if (code === "200") {
        yield put({
          type: "updateModel",
          payload: {
            actionType: "runView"
          }
        });
        message.success("开始执行");
      }
    },
    *executeJob({ payload }, { select, call, put }) {
      const { data } = yield Job_exec_configuration(payload.obj);
      const { code } = data;
      if (code === "200") {
        yield put({
          type: "updateModel",
          payload: {
            actionType: "runView"
          }
        });
        message.success("开始执行");
      }
    }
  }
};
