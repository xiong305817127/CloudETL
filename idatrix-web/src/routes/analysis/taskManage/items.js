/*组件弹出框模块*/

export default {
  namespace: 'items',
  state:{
    visible:false,
    text:"",
    id:"",
    panel:"",
    config:""
  },
  reducers: {
    'show'(state,action){
      return {
        ...state,
        ...action
      };
    },
    'hide'(state,action){
      return {
        ...state,
        visible:action.visible,
      };
    }
  }
};


