
/*提示框消息模块*/
export default {
  namespace: 'tip',
  state: {
    visible:false,
    text:"",
    tipText:"",
    status:"trans",
    transname:"",
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
        transname:action.transname,
        type:"deleteItem",
        status:action.status
      };
    },
    'hide'(state, action){
      return {
        ...state,
        visible: action.visible,
      }
    }
  }
};
