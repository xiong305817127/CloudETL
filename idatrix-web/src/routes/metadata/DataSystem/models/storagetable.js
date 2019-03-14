
export default {
  namespace: 'storagetable',
  state:{
    info:{},
    reloadList: false,
  },
  reducers: {
    "show"(state,action){
      console.log(action);
      return {
        ...state,
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
    },
    "reloadList"(state, action) {
      return {
        ...state,
        reloadList: action.reload,
      };
    }
  }
};
