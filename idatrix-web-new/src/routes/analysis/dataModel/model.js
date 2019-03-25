import {
  getModelList,
  addNewFolder,
  isExistFolder,
  isExistName,
  deleteModel,
  deleteFolder,
  importToSaiku,
  getDatabaseList,
  getTableList,
  getFieldsList
} from "services/bi";
import { message } from "antd";
//import { databaseType } from "config/jsplumb.config.js";

// let options = databaseType.map(index => {
// 	return {
// 		value: index.value,
// 		label: index.name,
// 		isLeaf: false
// 	};
// })

//延时1秒
const Timer = time =>
  new Promise(resolve => {
    setTimeout(() => {
      resolve();
    }, time);
  });

const initState = {
  //数据源数组
  options: [
    { label: "MySql", value: 1, isLeaf: false },
    { label: "Oracle", value: 2, isLeaf: false }
  ],
  //目录树数组
  folderTreeList: [],
  //新建文件夹
  newFolder: false,
  //新建模型
  newModal: false,
  //视图模式 false 欢迎页  true  详情页
  view: false,
  //选择的类型 true 文件夹  false 文件
  selectType: true,
  //选中的id
  menuId: "",
  //展开项
  expandedKeys: []
};

export default {
  namespace: "biDatamodel",

  state: {
    ...initState
  },
  subscriptions: {
    setup({ history, dispatch }) {
      history.listen(({ pathname }) => {
        if (pathname === "/analysis/DataModel") {
          dispatch({ type: "getModelList", force: true });
        } else {
          dispatch({ type: "clear" });
        }
      });
    }
  },
  effects: {
    //获取文件夹列表
    *getModelList({ force }, { put, call, select }) {
      yield call(Timer, 300);
      const { folderTreeList } = yield select(state => state.biDatamodel);
      const { renterId } = yield select(state => state.account);
      if (folderTreeList.length > 0 && !force) {
        return false;
      }
      const { data } = yield call(getModelList, { renterId });
      const { code } = data;
      if (code === "200") {
        yield put({ type: "save", payload: { folderTreeList: data.data } });
      }
    },
    //删除目录
    *deleteFolder({ payload }, { put, call, select }) {
      const { menuId } = yield select(state => state.biDatamodel);
      const { data } = yield call(deleteFolder, [menuId]);
      if (data.msg === "success") {
        message.success("删除目录成功！");
        yield put({ type: "getModelList", force: true });
      }
    },
    //删除模型
    *deleteModel({ payload }, { put, call, select }) {
      const { menuId } = yield select(state => state.biDatamodel);
      const { id } = yield select(state => state.biModelId);
      const { data } = yield call(deleteModel, [menuId]);
      if (data.msg === "success") {
        message.success("删除模型成功！");
        yield put({ type: "getModelList", force: true });
        if (menuId === id) {
          yield put({ type: "save", payload: { view: false } });
          yield put({ type: "biModelId/clearAll" });
        }
      }
    },
    //加载文件夹下数据
    *loadFolderData({ payload, resolve, reject }, { put, call, select }) {
      console.log(payload, "加载完成");

      resolve();
    },
    //新建文件夹
    *addNewFolder({ payload }, { call, put, select }) {
      const { renterId } = yield select(state => state.account);
      const { data } = yield call(addNewFolder, { ...payload, renterId });
      const { code } = data;
      console.log(code, "请求代码");
      if (code === "200") {
        yield put({ type: "getModelList", force: true });
      }
    },
    //验证文件夹是否存在
    *isExistFolder({ callback, action, payload }, { select, call }) {
      console.log("调用");
      const { renterId } = yield select(state => state.account);
      let method = isExistFolder;
      if (action && action === "name") {
        method = isExistName;
      }
      console.log(method, "方法");
      const { data } = yield call(method, { ...payload, renterId });
      const { code } = data;
      if (code === "200") {
        if (data.data) {
          if (action && action === "name") {
            callback("数据模型名称已存在！");
          } else {
            callback("文件夹名称已存在！");
          }
          return false;
        }
        callback();
        return false;
      } else {
        callback();
      }
    },
    //获取数据库表
    *getDataSource({ payload, resolve, reject }, { select, put, call }) {
      const { options } = yield select(state => state.biDatamodel);
      let data = null;
      if (payload.length === 1) {
        data = yield call(getDatabaseList, payload.id);
      } else if (payload.length === 2) {
        data = yield call(getTableList, payload.id);
      } else if (payload.length === 3) {
        data = yield call(getFieldsList, payload.id);
      }
      const { code } = data.data;
      if (code === "200") {
        payload.targetOption.loading = false;
        if (payload.length === 1) {
          payload.targetOption.children = data.data.data.map(index => ({
            label: `${index.schemaName}(${index.ip})`,
            value: index.schemaId,
            isLeaf: false
          }));
        } else if (payload.length === 2) {
          payload.targetOption.children = data.data.data.map(index => ({
            label: index.name,
            value: index.id,
            isLeaf: false
          }));
        } else if (payload.length === 3) {
          payload.targetOption.children = data.data.data.map(index => ({
            label: `${index.columnName}(${index.dataType})`,
            value: index.columnName,
            disabled: true
          }));
        }
        resolve();
      } else {
        payload.targetOption.children = [];
        reject();
      }
      yield put({
        type: "save",
        payload: {
          options: [...options]
        }
      });
    },
    //一键导入
    *importToSaiku({}, { call }) {
      const hide = message.loading("正在导入...", 0);
      const { data } = yield call(importToSaiku);
      if (data && data.code === "200") {
        hide();
        message.success(data && data.msg ? data.msg : "导入成功！", 2);
      } else {
        hide();
      }
    }
  },
  reducers: {
    save(state, action) {
      return { ...state, ...action.payload };
    },
    hide(state, action) {
      for (let index of state.options) {
        index.children && index.children.splice(0);
      }
      return {
        ...state,
        ...action.payload,
        options: [{ label: "MySql", value: 1, isLeaf: false },
				{ label: "Oracle", value: 2, isLeaf: false }]
      };
    },
    clear(state, action) {
      state.folderTreeList.splice(0);
      state.expandedKeys.splice(0);
      for (let index of state.options) {
        index.children && index.children.splice(0);
      }
      return {
        ...initState,
        options: [{ label: "MySql", value: 1, isLeaf: false },
				{ label: "Oracle", value: 2, isLeaf: false }]
      };
    }
  }
};
