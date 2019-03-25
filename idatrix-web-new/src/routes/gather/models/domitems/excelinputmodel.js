/*数据库链接*/
export default {
  namespace: "excelinputmodel",
  state:{
     visible:false,
    tableNames:[],
  },
  reducers: {
    'show'(state,action){
      console.log(action);
      return {
        ...state,
        visible:action.visible,
        tableNames:action.tableNames,
        handleSheetUpdate:action.handleSheetUpdate
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

