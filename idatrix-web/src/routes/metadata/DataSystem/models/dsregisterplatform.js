
export default {
  namespace: 'dsregisterplatform',
  state: {},
  reducers: {
    "editmodel"(state,action){
      return {
        ...state,
        model:action.model
      };
    },
    "changeView"(state,action){
      return {
        ...state,
        view:action.view,
        model:"changeView"
      };
    },
    "reload"(state,action){
      return {
        ...state,
        reload:action.reload,
      };
    }
  }
};
