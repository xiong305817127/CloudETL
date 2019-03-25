
import {getFileList } from  '../../../../services/gather';

const setList = (aras,data,key)=>{
  aras.map(index=>{
    if(index.children && index.children.length>0){
      setList(index.children,data,key);
    }else{
      if(index.path === key){
        return index.children = data;
      }
    }
  })
};

export default {
  namespace: 'filemodel',
  state: {
    visible:false,
    root:"",
    treeList:{},
    expandedKeys:[],
    setFolder:{},
    model:"",
    title:"",
    value:"",
    pathStr:""
  },
  reducers: {
    'showModel'(state,action){
      return {
        ...state,
        ...action.payload
      };
    },
    'addTree'(state,action){
      setList([state.treeList],action.treeData,action.evenKey);
      return {
        ...state,
        treeList:state.treeList
      };
    },
    'hideModel'(state,action){
      return {
        visible:false,
        filelist:{},
        ModalCole:{},
        value:"",
        stateFile:"",
        title:"",
        fields:""
      };
    }
  },
   effects: {
     *showTreeModel({payload}, {select, call, put}) {
       const { owner } = yield select(state=>state.transheader);
       const { data } = yield getFileList({...payload.obj,owner});
       const { code } = data;
       if(code === "200"){
         yield put({
           type: "showModel",
           payload: {
             visible:true,
             root:payload.obj.type,
             treeList:data.data,
             expandedKeys:[""+data.data.fileName],
             setFolder:payload.fuc,
             model:payload.model,
             title:payload.title,
             pathStr:payload.pathStr
           }
         });
       }
     },
     *getTreeModel({payload}, {select, call, put}) {
      const { owner } = yield select(state=>state.transheader);
       const { data } = yield getFileList({...payload.obj,owner});
        const {code} = data;
       if(code === "200"){
         yield put({
           type: "addTree",
           treeData:data.data.children,
           evenKey:payload.evenKey
         });
       }
     }
  }
};
