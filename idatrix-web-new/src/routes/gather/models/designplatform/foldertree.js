/**
 * Created by Administrator on 2017/8/7.
 */
import { getFileList } from '../../../../services/gather';


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

export default {
  namespace: 'foldertree',
  state: {
    visible: false,
    treeList: {},
    setFolder: {},
    root: "",
    expandedKeys: []
  },
  reducers: {
    'showTree'(state, action) {
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
    'hideTree'(state, action) {
      state.expandedKeys.splice(0);
      return {
        visible: false,
        treeList: {},
        setFolder: {},
        root: "",
        expandedKeys: []
      };
    }
  },
  effects: {
    *showTreeModel({ payload }, { select, call, put }) {
      const { owner } = yield select(state=>state.transheader);
      const { data } = yield getFileList({...payload.obj,owner});

      if (data) {
        const { code } = data;
        if (code === "200") {
          yield put({
            type: "showTree",
            payload: {
              visible: true,
              root: payload.obj.type,
              treeList: data.data,
              expandedKeys: ["" + data.data.fileName],
              setFolder: payload.fuc,
              pathStr: payload.str
            }
          });
        }
      }
    },
    *getTreeModel({ payload }, { select, call, put }) {
      const { owner } = yield select(state=>state.transheader);
      const { data } = yield getFileList({...payload.obj,owner});
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
};

