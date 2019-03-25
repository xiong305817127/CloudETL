/*数据库链接*/
export default {
  namespace: "hadoopoutputmodel",
  state:{
    visible:false
  },
  reducers: {
    'show'(state,action){
      console.log(action);
      return {
        ...state,
        visible:action.visible
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

