import {
  getSubtreeAndDepth,
  getNodeInfo,
  addNode,
  deleteNode,
  fileImport
} from "services/catalog";
import { message } from "antd";

const getNewResouceList = (args, parentId, newData) => {
  let getChild = false;
  return args.map(index => {
    if (getChild) return index;
    if (parseInt(index.id) === parseInt(parentId)) {
      index.children = newData;
      getChild = true;
      return index;
    } else {
      if (index.children) {
        return getNewResouceList(index.children, parentId, newData);
      }
      return index;
    }
  });
};

export default {
  namespace: "databaseModel",

  state: {
    resourcesList: [], // 资源列表
    resourceView: {}, // 资源详情

    //控制SliderModal弹框
    visible: false, //显示
    actionType: "new", //操作类型
    config: {}
  },

  effects: {
    /**
     *  逐级查询资源目录
     *  根据传入的id,逐级加载数据
     */
    *getResourcesFolder(
      { payload, resolve, treeNode, resourcesList },
      { put, call, select }
    ) {
      const { data } = yield call(getSubtreeAndDepth, { ...payload });
      const { code } = data;
      if (code === "200") {
        if (resourcesList && resourcesList.length > 0) {
          treeNode.props.dataRef.children = data.data.map(index => {
            index.children = null;
            return index;
          });
          resolve();
          yield put({ type: "save", payload: { resourcesList } });
        } else {
          const databaseModel = yield select(state => state.databaseModel);
          for (let index of data.data) {
            index.children = null;
            databaseModel.resourcesList.push(index);
          }
          yield put({
            type: "save",
            payload: { resourcesList: databaseModel.resourcesList }
          });
        }
      }
    },
    *updateResourcesFolder({ payload }, { call, select, put }) {
      const { resourcesList } = yield select(state => state.databaseModel);
      const { data } = yield call(getSubtreeAndDepth, { ...payload });
      const { code } = data;
      if (code === "200") {
        let newData = data.data.map(index => {
          index.children = null;
          return index;
        });
        //递归更新resourceList
        let newResourceList = getNewResouceList(
          resourcesList,
          payload.id,
          newData
        );
        yield put({
          type: "save",
          payload: { resourcesList: newResourceList }
        });
      }
    },
    *deleteNodeInfo({ payload }, { put, call, select }) {
      const { data } = yield call(deleteNode, { ...payload });
      const { code } = data;
      if (code === "200") {
        const { config } = yield select(state => state.databaseModel);
        yield put({
          type: "updateResourcesFolder",
          payload: { id: config.parentId }
        });
      }
    },
    *showResource({ payload }, { put }) {
      yield put({ type: "save", payload: { resourceView: payload } });
    },
    *getNode({ payload }, { put, call }) {
      const { data } = yield call(getNodeInfo, { ...payload });
      const { code } = data;
      if (code === "200") {
        yield put({ type: "save", payload: { config: { ...data.data } } });
      }
    },
    *saveNode({ actionType, payload }, { put, call }) {
      const { data } = yield call(addNode, { ...payload });
      const { code } = data;
      if (code === "200") {
        if (actionType === "edit") {
          yield put({
            type: "save",
            payload: { visible: false, config: { ...payload } }
          });
        } else {
          yield put({ type: "save", payload: { visible: false } });
        }
        yield put({
          type: "updateResourcesFolder",
          payload: { id: payload.parentId }
        });
      }
    },
    *fileImport({ payload }, { put, call, select }) {
      const hide = message.loading("正在批量导入目录...", 0);
      const { data } = yield call(fileImport, { ...payload });
      const { code } = data;
      if (code === "200") {
        hide();
        const { config } = yield select(state => state.databaseModel);
        message.success("批量导入目录成功！");
        yield put({
          type: "updateResourcesFolder",
          payload: { id: config.id }
        });
      } else {
        hide();
        message.error("批量导入目录失败！");
      }
    }
  },
  reducers: {
    save(state, action) {
      return { ...state, ...action.payload };
    }
  }
};
