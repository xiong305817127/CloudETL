/*组件弹出框模块*/

export default {
  namespace: 'items',
  state:{
    visible:false,
    x:0,
    y:0,
    key:"",
    panel:"",
    config:{},
    transname:{},
    description:"",
    prevStepNames:[],
    nextStepNames:[]
  },
  reducers: {
    'show'(state,action){
      return {
        ...state,
        visible:action.visible,
        panel:action.panel,
        text:action.text,
        config:action.config,
        transname:action.transname,
        parallel:action.parallel,
        description:action.description,
        key:action.key,
        prevStepNames:action.prevStepNames,
        nextStepNames:action.nextStepNames
      };
    },
    'hide'(state,action){
      return {
        ...state,
        x:0,
        y:0,
        key:"",
        panel:"",
        config:{},
        transname:{},
        description:"",
        prevStepNames:[],
        nextStepNames:[],
        visible:action.visible
      };
    }
  }
};


