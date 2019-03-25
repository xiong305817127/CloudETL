
export default {
  namespace: 'datasystemsegistration',
  state: {
    view:"1",
    dbDatabasename:""
  },
  reducers: {
    "editmodel"(state,action){
      console.log(action);

      return {
        ...state,
        model:action.model
      };
    },
    "changeView"(state,action){

      return {
        ...state,
        view:action.view,
        model:"changeView",
        dbDatabasename:action.metaNameCn
      };
    },
    "search"(state,action){
      console.log(action);
      return {
        ...state,
        model:"search",
        dbDatabasename:action.metaNameCn
      };
    }
  }
};
