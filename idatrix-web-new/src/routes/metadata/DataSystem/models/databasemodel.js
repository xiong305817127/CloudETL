
export default {
  namespace: 'databasemodel',
  state:{
    info:{}
  },
  reducers: {
    "show"(state,action){
      return {
        ...state,
        visible:action.visible,
        model:action.model,
        info:action.info,
        modelDle:action.modelDle,
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
