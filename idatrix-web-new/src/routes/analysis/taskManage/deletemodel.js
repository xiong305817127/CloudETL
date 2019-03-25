
/*提示框消息模块*/
export default {
  namespace: 'deletemodel',
  state: {
    visible:false,
    text:"",
    tipText:"",
    type:"deleteItem"
  },
  reducers: {
    'deleteItem'(state, action){

      action.tip = "确定删除 "+action.text;
      return {
        ...state,
        id:action.id,
        visible:action.visible,
        text:action.text,
        tipText:action.tip,
        type:"deleteItem"
      };
    },
    'hide'(state, action){
      return {
        ...state,
        visible: action.visible,
      }
    }
  },
  subscriptions: {
    setup({ dispatch, history }) {
      history.listen(location => {
        if(location.pathname === "/analysis/TaskManage/EditTaskManage/38"){
            console.log("0000000000000");
        }
      });
    }
  }
};
