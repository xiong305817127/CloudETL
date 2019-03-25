
export default {
  namespace: 'tab1model',
  state:{
    info:{}
  },
  reducers: {
    "show"(state,action){
      console.log(action);
      return {
        ...state,
        visible:action.visible,
        model:action.model,
        info:action.info,
        dsType:action.dsType
      };
    },
    "hide"(state,action){
      return {
        ...state,
        visible:action.visible
      };
    },
    "type"(state,action){
      return {
        ...state,
        type:action.type
      };
    }
  }
};
