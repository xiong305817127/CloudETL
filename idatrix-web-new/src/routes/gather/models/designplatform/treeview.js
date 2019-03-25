/**
 * Created by Administrator on 2017/8/7.
 */
import { getFileList, getParentPath } from 'services/gather';
import { getQuaFileList, getQuaParentPath } from 'services/quality';


const setList = (aras, data, key) => {
  aras.map(index => {
    if (index.children && index.children.length > 0) {
      setList(index.children, data, key);
    } else {
      if (index.path === key) {
        return index.children = data;
      }
    }
  })
};

const initState = {
  visible: false,
  treeList: {},
  expandedKeys: [],

  value: "",
  //更新modal方法
  updateModel: {},
  //传入的根类型
  rootType: "",
  //是否需要全名
  needFileName: false,
  //能选的类型
  needType: "all",
  //请求附加前缀
  prefixStr: "",
  filterType: "",
  //是否需要返回上一级目录
  needUpFolder: true,
  viewPath: "",

  //加载完成的节点
  loadedKeys: [],

  action:"cloudetl"
}

export default {
  namespace: 'treeview',
  state: { ...initState },
  reducers: {
    showTree(state, action) {
      return {
        ...state,
        ...action.payload
      };
    },
    'addTree'(state, action) {
      setList([state.treeList], action.treeData, action.evenKey);
      return {
        ...state,
        treeList: state.treeList
      };
    },
    hideTree(state, action) {
      state.expandedKeys.splice(0);
      state.loadedKeys.splice(0);
      return { ...initState };
    }
  },
  effects: {
    *showTreeModel({ payload }, { select, call, put }) {
      let action = payload.action?payload.action:"cloudetl";
      yield put({ type:"showTree",payload:{ action }})
      const { owner } = yield select(state=>state.transheader);

      //const { visible } = yield select(state => state.treeview);
      // if (!visible && !payload.viewPath) {
      //   yield put({ type: "getParentFolder",payload: { path: "" } })
      // }
      let res = null;
      if(action === "quality" ){
        res = yield getQuaFileList({...payload.obj,owner});
      }else{
        res = yield getFileList({...payload.obj,owner});
      }

      if (res && res.data) {
        const data = res.data;
        const { code } = data;

        if (code === "200") {
          yield put({
            type: "showTree",
            payload: {
              visible: true,
              action,
              rootType: payload.obj.type,
              treeList: data.data,
              expandedKeys: ["" + data.data.path?data.data.path:data.data.fileName],
              updateModel: payload.updateModel,
              prefixStr: payload.prefixStr ? payload.prefixStr : "",
              needFileName: payload.needFileName,
              needType: payload.needType,
              needUpFolder: payload.needUpFolder,
              filterType: payload.obj.filterType,
              viewPath: payload.viewPath
            }
          });
        }
      }
    },
    *getParentFolder({ payload }, { select, call, put }) {
      const { action } = yield select(state => state.treeview);
      let res = null;
      if(action === "quality" ){
        res = yield call(getQuaParentPath, payload);
      }else{
        res = yield call(getParentPath, payload);
      }
      if(res && res.data){
        const data = res.data;
        const { code } = data;
        if (code === "200") {
          yield put({
            type: "showTree",
            payload: {
              upFolder: data.data
            }
          })
        }
      }
    },
    *reloadTreeModel({ payload }, { select, call, put }) {
      const { action } = yield select(state => state.treeview);
      const { owner } = yield select(state=>state.transheader);

      let res = null;
      if(action === "quality" ){
        res = yield getQuaFileList({...payload.obj,owner});
      }else{
        res = yield getFileList({...payload.obj,owner});
      }
      if(res && res.data){
        const data = res.data;
        const { code } = data;
        if (code === "200") {
          yield put({
            type: "showTree",
            payload: {
              treeList: data.data,
              expandedKeys: ["" + data.data.path]
            }
          });
        }
      }
    },
    *getTreeModel({ payload }, { select, call, put }) {
      const { action } = yield select(state => state.treeview);
      const { owner } = yield select(state=>state.transheader);

      let res = null;
      if(action === "quality" ){
        res = yield getQuaFileList({...payload.obj,owner});
      }else{
        res = yield getFileList({...payload.obj,owner});
      }
      if(res && res.data){
        const data = res.data;
        const { code } = data;
        if (code === "200") {
          yield put({
            type: "addTree",
            treeData: data.data.children,
            evenKey: payload.evenKey
          });
        }
      }
    }
  }
};

