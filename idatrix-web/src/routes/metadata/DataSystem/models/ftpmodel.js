
export default {
  namespace: 'ftpmodel',
  state: {
    visible:false,
    model:"",
    info:{},
    modelDle:"",
  },
  reducers: {
    "show"(state,action){
      console.log(action,"a");
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
  },
};
