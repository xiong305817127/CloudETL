export default {
  namespace: 'dsregistermodel',
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
        info:action.info
      };
    },
    "editmodel"(state,action){
      console.log(action);
      return {
        ...state,
        model:action.model
      };
    },
    "hide"(state,action){
      return {
        ...state,
        visible:action.visible
      };
    }
  }
};
