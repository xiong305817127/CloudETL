/*日志打印模块*/
export default {
  namespace: 'debugdetail',
  state: {
    visible:false
  },
  reducers: {
    "changeView"(state,action){
      return {
        ...state,
        visible:action.visible
      }
    }
  }
};


