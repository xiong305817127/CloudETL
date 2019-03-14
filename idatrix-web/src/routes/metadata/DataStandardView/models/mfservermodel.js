
export default {
  namespace: 'mfservermodel',
  state: {
    visible:false,
    model:"",
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
    "hide"(state,action){
      return {
        ...state,
        visible:action.visible
      };
    }
  },
};
