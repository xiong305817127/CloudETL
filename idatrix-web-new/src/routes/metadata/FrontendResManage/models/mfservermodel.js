
export default {
  namespace: 'mfservermodel',
  state: {
    visible:false,
    model:"",
    info:{},
    id:"",
  },
  reducers: {
    "show"(state,action){
      console.log(action);
      return {
        ...state,
        ...action,
        visible:action.visible,
        model:action.model,
        info:action.info,
         id:action.id,
      };
    },
    "hide"(state,action){
      return {
        ...state,
        visible:action.visible,
      };
    }
  },
};
