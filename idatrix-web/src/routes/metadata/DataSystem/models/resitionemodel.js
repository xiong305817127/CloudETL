
export default {
  namespace: 'resitionemodel',
  state:{
    info:{}
  },
  reducers: {
    "show"(state,action){
      return {
        ...state,
        visible:action.visible,
        model:action.model,
        info:action.info
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
