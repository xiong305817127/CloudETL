import tools from './components/tools.config.js';
import { resetData } from './components/flowdata'
import { viewTask, getTaskNodeInfo } from '../../../services/analysisTask';
import { deepCopy } from 'utils/utils';

const fromatItems = (doms)=>{
  let args = [];
  for(let i=0;i<doms.length;i++){
    let obj = {};
    obj.dataId =  doms[i].id;
    obj.id = jsPlumbUtil.uuid();
    obj.panel = doms[i].type;
    obj.text = doms[i].name;
    obj.x = parseInt(doms[i].location.xloc);
    obj.y = parseInt(doms[i].location.yloc);
    obj.imgUrl = tools[doms[i].type].imgUrl;
    obj.config = tools[doms[i].type].config;
    obj.dragClass = "dragNormal";
    if(doms[i].jarContent){
      obj.config = doms[i].jarContent;
    }
    if(doms[i].scripts){
      obj.config = doms[i].scripts;
    }

    args.push(obj);
  }
  return args;
};

const getItem = (items,name)=>{
  // console.log(items);
  // console.log(name);
  for(var index of items){
    if(index.text === name){
      return index;
    }
  }
}

const fromatLines = (items,lines)=>{
  let args = [];
  for(var index of lines){
    let obj = {};
    obj.sourceId = getItem(items,index.from).id;
    obj.targetId = getItem(items,index.to).id;
    args.push(obj);
  }
  return args;
};


export default {
  namespace: 'flowspace',
  state: {
    name:"",
    items:[],
    lines:[],
    viewId:"",
    model:"newState",
    description:"",
    status: 'UNSAVED',
    trigger: {},
    nodes: [],
  },

  effects: {
    // 获取任务详情
    *getTaskView({ payload }, { put }) {
      const taskid = payload;
      const { data } = yield viewTask(taskid);
      const { data: data2 } = yield getTaskNodeInfo({ taskid });
      const newData = deepCopy(data && data.data || {});
      Object.assign(newData, {
        status: data2 && data2.data ? data2.data.status : 'UNEXECUTED',
        nodes: data2 && data2.data ? data2.data.nodes : [],
      });
      yield put({ type: 'newState', payload: newData });
    },
  },

  reducers: {
    'newState'(state,action){
      resetData();
      let items = fromatItems(action.payload.stepList || []);
      let lines = fromatLines(items,action.payload.hopList || []);
      return {
        ...state,
        viewId:action.payload.id,
        name:action.payload.name,
        description:action.payload.description,
        items:items,
        lines:lines,
        nodes:action.payload.nodes,
        status:action.payload.status,
        trigger: action.payload.trigger || {},
        model:"newState"
      };
    },
    'updateStatus'(state, action) {
      return { ...state, ...action.payload, model: 'updateStatus' };
    },
    'addItem'(state,action){

      let obj = {}
      obj.id = jsPlumbUtil.uuid();
      obj.panel = action.panel;
      obj.text = action.text;
      obj.x = action.x;
      obj.y = action.y;
      obj.imgUrl = action.imgUrl;
      obj.config = action.config;
      obj.dragClass = "dragNormal";

      return {
        ...state,
        model:"addItem",
        items:[
          ...state.items,
          obj
        ],
        status: 'UNSAVED',
      }
    },
    'changeItemName'(state,action){

      return {
        ...state,
        model:"changeItemName",
        items:state.items.map((index)=>{
          if(index.id === action.key){
            index.text = action.text
          }
          return index;
        }),
        status: 'UNSAVED',
      };
    },
    'deleteItem'(state,action){
      return {
        ...state,
        model:"deleteItem",
        deleteId:action.id,
        items:state.items.filter((index)=>{
          return index.id != action.id;
        }),
        lines:state.lines.filter(line => {
          return line.sourceId != action.id && line.targetId != action.id;
        }),
        status: 'UNSAVED',
      };
    },
    "initStep"(state,action){
      let args = state.items;
      if(args.length != 0){
        for(let index of args){
          index.dragClass = "dragNormal";
        }
        return {
          ...state,
          items:args,
          model:"initStep"
        };
      }
      return state;
    },
    "saveItem"(state,action){
      return {
        ...state,
        model:"saveItem",
        items:state.items.map((index)=>{
          if(index.id === action.obj.id){
            index.config = action.obj.config;
            index.text = action.obj.name;
          }
          return index;
        }),
        status: 'UNSAVED', // 对于整个任务来说，修改节点，相应于将任务状态置于未保存
      };
    }
  },
  subscriptions: {
    setup({ dispatch, history }) {
      history.listen(location => {
      });
    }
  }
};



