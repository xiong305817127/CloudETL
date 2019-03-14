/**
 * Created by Administrator on 2018/2/6.
 */
import Tools from "../../../components/config/Tools";
import { message } from "antd";
import {
  add_step,
  move_step,
  delete_step,
  copyStep,
  add_hop,
  save_stepConfigs,
  delete_hop
} from "../../../../../services/gather";
/**
 * 格式化节点，并返回数组
 * @param doms 节点数组
 * @returns {Array}
 */
const fromatItems = doms => {
  let args = [];
  for (let index of doms) {
    if (index) {
      let type = index.type;
      if (!Tools[type] || Tools[type].typeFeil != "trans") {
        type = "UNKNOWN";
      }
      args.push({
        id: jsPlumbUtil.uuid(),
        panel: type,
        text: index.name,
        imgUrl: Tools[type].imgUrl,
        config: Tools[type].config,
        dragClass: "dragNormal",
        x: parseInt(index.gui.xloc),
        y: parseInt(index.gui.yloc),
        distributes: index.distributes,
        supportsErrorHandling: index.supportsErrorHandling
      });
    }
  }
  return args;
};

/**
 * 根据名字返回节点数组中的对象
 * @param items  节点数组
 * @param name   名字
 * @returns {*}
 */
const getItem = (items, name) => {
  for (var index of items) {
    if (index.text === name) {
      return index;
    }
  }
};

/**
 * 根据id，返回节点的名字
 * @param items  节点数组
 * @param id      节点ID
 */
const getItemName = (items, id) => {
  for (let index of items) {
    if (index.id === id) {
      return index.text;
    }
  }
};

/**
 * 格式化线条
 * @param items
 * @param lines
 * @returns {Array}
 */
const fromatLines = (items, lines) => {
  let args = [];
  for (var index of lines) {
    let obj = {};
    obj.sourceId = getItem(items, index.from).id;
    obj.targetId = getItem(items, index.to).id;
    obj.enabled = index.enabled;
    obj.evaluation = index.evaluation;
    obj.unconditional = index.unconditional;
    args.push(obj);
  }
  return args;
};

/**
 * 根据已存在名字，生成新名字
 * @param text
 * @returns {*}
 */
const getNewName = (text, elems) => {
  let nameArgs = [];
  let newName = text;
  let i = 1;
  for (let index of elems) {
    nameArgs.push(index.text);
  }

  while (nameArgs.includes(newName)) {
    newName = text + " " + i++;
  }
  return newName;
};

const initState = {
  name: "",
  items: [],
  lines: [],
  //元素id的集合防止重复渲染
  itemsId: [],
  viewId: "",
  btnStatus: "Waiting",
  shouldUpdate: false,

  //切换主题风格
  spaceStyle: "workspace_contain2"
};

/*
 *     transspace/changeStyle
 * */

export default {
  namespace: "transspace",
  state: { ...initState },
  reducers: {
    clear(state) {
      state.items.splice(0);
      state.lines.splice(0);
      state.itemsId.splice(0);

      return { ...initState };
    },
    updateData(state, action) {
      return {
        ...state,
        ...action.payload
      };
    },
    //打开转换
    openFile(state, action) {
      let items = action.itemsList ? fromatItems(action.itemsList) : [];
      let lines = action.linesList ? fromatLines(items, action.linesList) : [];

      return {
        ...state,
        viewId: action.viewId,
        name: action.name,
        items: items,
        lines: lines,
        shouldUpdate: true,
        btnStatus: action.btnStatus
      };
    },
    //添加节点
    addItem(state, action) {
      return {
        ...state,
        shouldUpdate: true,
        items: [
          ...state.items,
          {
            ...action.payload,
            dragClass: "dragNormal",
            id: jsPlumbUtil.uuid()
          }
        ]
      };
    },
    //改变节点名字
    changeItemName(state, action) {
      return {
        ...state,
        shouldUpdate: true,
        items: state.items.map(index => {
          if (index.id === action.key) {
            index.text = action.text;
          }
          return index;
        })
      };
    },
    //删除节点
    deleteItem(state, action) {
      return {
        ...state,
        shouldUpdate: true,
        items: state.items.filter(index => {
          return index.id != action.payload.id;
        }),
        lines: state.lines.filter(index => {
          return (
            index.sourceId !== action.payload.id &&
            index.targetId !== action.payload.id
          );
        })
      };
    },
    //执行过程中更新step状态
    updateStep(state, action) {
      let args = state.items;
      if (args.length > 0) {
        for (let index of action.payload.stepInfo) {
          if (index && index.errCount != 0) {
            if (getItem(args, index.stepName)) {
              getItem(args, index.stepName).dragClass = "error";
            }
          }
        }
        return {
          ...state,
          items: args
        };
      }
      return state;
    },
    //初始化步骤状态
    initStep(state, action) {
      return {
        ...state,
        items: state.items.map(index => {
          index.dragClass = "dragNormal";
          return index;
        })
      };
    },
    //更新结束后的step状态
    updateResultStep(state, action) {
      const { status, stepInfo } = action.payload;

      let args = state.items;
      for (let index of args) {
        index.dragClass = status;
      }
      for (let index of stepInfo) {
        if (index && index.errCount > 0) {
          if (getItem(args, index.stepName)) {
            getItem(args, index.stepName).dragClass = "error";
          }
        }
      }
      return {
        ...state,
        items: args
      };
    }
  },
  effects: {
    *deleteStep({ payload }, { select, call, put }) {
      const { owner } = yield select(state => state.transheader);
      const { data } = yield delete_step({ ...payload.obj, owner });
      const { code } = data;
      console.log(payload);
      if (code === "200") {
        yield put({
          type: "deleteItem",
          payload: {
            id: payload.id
          }
        });
        message.success("删除成功");
      } else {
        message.success("删除失败，请重试");
      }
    },
    *addNewItem({ payload }, { select, call, put }) {
      const { items, name } = yield select(state => state.transspace);
      const { activeArgs } = yield select(state => state.transheader);

      payload.text = getNewName(payload.text, items);

      let obj1 = {};
      obj1.transName = name;
      obj1.stepName = payload.text;
      obj1.stepType = payload.panel;
      obj1.owner = activeArgs.get(name).owner;
      const data1 = yield add_step(obj1);
      if (data1.data.code === "200") {
        const { distributes, supportsErrorHandling } = data1.data.data;

        yield put({
          type: "addItem",
          payload: {
            ...payload,
            distributes,
            supportsErrorHandling
          }
        });
        obj1.xloc = payload.x;
        obj1.yloc = payload.y;
        yield move_step(obj1);
      }
    },
    *copyItem({ payload }, { select, call, put }) {
      const { items } = yield select(state => state.transspace);
      const { owner } = yield select(state=> state.transheader);
      payload.obj.toStepName = getNewName(
        Tools[payload.item.panel].text,
        items
      );

      const { data } = yield copyStep({ ...payload.obj, toOwner: owner  });

      const { code } = data;

      if (code === "200") {
        message.success("粘贴成功！");
        payload.item.text = payload.obj.toStepName;
        payload.item.x = data.data.x;
        payload.item.y = data.data.y;

        if (code === "200") {
          yield put({
            type: "addItem",
            payload: {
              ...payload.item
            }
          });
        }
      }
    },
    *addHop({ payload }, { select, call, put }) {
      const { items, lines } = yield select(state => state.transspace);
      const { owner } = yield select(state => state.transheader);
      lines.push({
        sourceId: payload.start,
        targetId: payload.target,
        enabled: true,
        evaluation: true,
        unconditional: true
      });

      payload.start = getItemName(items, payload.start);
      payload.target = getItemName(items, payload.target);

      let obj = {
        name: payload.transname,
        from: payload.start,
        to: payload.target,
        enabled: true,
        evaluation: true,
        unconditional: true
      };

      const { data } = yield call(add_hop, { ...obj, isTrans: true, owner });
      const { code } = data;
      if (code === "200") {
        yield put({
          type: "updateData",
          payload: {
            lines
          }
        });
      }
    },
    *moveStep({ payload }, { select, call, put }) {
      const { name, items } = yield select(state => state.transspace);

      // 添加owner
      const { owner } = yield select(state => state.transheader);

      const { data } = yield call(move_step, {
        xloc: payload.x,
        yloc: payload.y,
        transName: name,
        owner: owner,
        stepName: getItemName(items, payload.id)
      });
      const { code } = data;
      items.map(index => {
        if (index.id === payload.id) {
          index.x = payload.x;
          index.y = payload.y;
        }
      });
      if (code === "200") {
        yield put({
          type: "updateData",
          payload: {
            items
          }
        });
      }
    },
    *saveLine({ payload }, { select, call, put }) {
      const { items, lines, name } = yield select(state => state.transspace);
      const { sendType, errorInfo } = payload;
      const { owner } = yield select(state => state.transheader);

      if (sendType === "error") {
        let obj = {
          name: name,
          from: getItemName(items, errorInfo.sourceId),
          to: getItemName(items, errorInfo.targetId),
          isTrans: true,
          enabled: true,
          evaluation: true,
          unconditional: false
        };
        const { data } = yield call(add_hop, { ...obj, isTrans: true, owner });

        const { code } = data;

        if (code === "200") {
          for (let index of lines) {
            if (
              index.sourceId === errorInfo.sourceId &&
              index.targetId === errorInfo.targetId
            ) {
              index.unconditional = false;
            } else {
              index.unconditional = true;
            }
          }
        }
      } else {
        let obj = {
          transName: name,
          stepName: getItemName(items, errorInfo.sourceId),
          configs: {
            clusterSchema: "##ignore##", // 集群配置
            distribute: sendType != "copy"
          }
        };
        const { data } = yield call(save_stepConfigs, { ...obj, owner });
        const { code } = data;

        if (code === "200") {
          for (let index of items) {
            if (index.id === errorInfo.sourceId) {
              index.distributes = sendType !== "copy";
            }
          }
        }
      }
      yield put({
        type: "updateData",
        payload: {
          lines,
          items,
          shouldUpdate: true
        }
      });
    },
    *deleteHop({ payload }, { select, call, put }) {
      const { name, items, lines } = yield select(state => state.transspace);
      const { owner } = yield select(state => state.transheader);
      let obj = {
        name: name,
        from: getItemName(items, payload.sourceId),
        to: getItemName(items, payload.targetId),
        isTrans: true
      };
      const { data } = yield call(delete_hop, { ...obj, owner });
      const { code } = data;

      if (code === "200") {
        yield put({
          type: "updateData",
          payload: {
            lines: lines.filter(index => {
              return !(
                index.targetId === payload.targetId &&
                index.sourceId === payload.sourceId
              );
            }),
            shouldUpdate: true
          }
        });
      }
    },
    *updateStepStatus({ payload }, { select, call, put }) {
      const { name } = yield select(state => state.transspace);

      const { transName, actionType } = payload;

      if (transName != name) {
        return false;
      }
      if (actionType === "updateStep") {
        yield put({
          type: "updateStep",
          payload: {
            stepInfo: payload.stepInfo
          }
        });
      } else if (actionType === "updateResult") {
        yield put({
          type: "updateResultStep",
          payload: {
            stepInfo: payload.stepInfo,
            status: payload.status
          }
        });
      } else {
        yield put({
          type: "updateData",
          payload: {
            btnStatus: payload.transStatus
          }
        });
      }
    }
  }
};
