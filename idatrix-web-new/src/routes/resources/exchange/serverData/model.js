import { getAllServiceLog } from  'services/DirectoryOverview';
export default {
  namespace: 'serverDataModel',
  state:{
  	pagination:{
      current:1,
      pageSize:10
    },
    options:[],
    text:"",
    data:[],
    loading:false,
    logData:[],
  },
  reducers: {
    setMetaId(state,action){
       return {
        ...state,
        ...action.payload
      };
    },
    showModel(state,action){
      return {
        ...state,
        ...action.payload,
        visible:true
     
      };
    },
    hideModel(state,action){
      return {
         ...state,
        ...action.payload,
        visible:false,
      };
    },
    save(state,action){
      return {
        ...state,
        ...action.payload
      }
    }
  },
 subscriptions:{
      setup({history,dispatch}){
        return history.listen(({ pathname,query })=>{
          if(pathname === "/resources/exchange/serverData/index"){
               dispatch({
                type:"getList",
                payload:{
                  ...query,
                  page:query.page?query.page:1,
                  pageSize:query.pageSize?query.pageSize:10
                }
              })
          }
        })
      }
  },

  effects:{
    *getList({payload},{call,select,put}){
        yield put({type:"save",payload:{loading:true}})
        const { data } = yield call(getAllServiceLog,{...payload});
        const { code } = data;
        if(code === "200"){
           yield put({
              type:'setMetaId',
              payload:{
                data:data.data.results,
                pagination: {
                  current:1,
                  pageSize:10
                },
                total:data.data.total,
                loading:false
              }
           })
        }
      
      },
  }
};
