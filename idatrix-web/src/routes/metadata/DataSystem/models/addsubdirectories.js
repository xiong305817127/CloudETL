
export default {
  namespace: 'addsubdirectories',
  state:{},
  reducers: {
    "show"(state,action){
      return {
        ...state,
        visible:action.visible,
        doWhat:action.doWhat,
        selectedKeys:action.selectedKeys,
        selectedTitle: action.selectedTitle,
        treeData: action.treeData
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
