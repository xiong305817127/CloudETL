import { getOverviewTask,getHistory } from  'services/DirectoryOverview';
export default {
  namespace: 'reportModel',
  state:{
  	pagination:{
      current:1,
      pageSize:10
    },
    paginations:{
      current:1,
      pageSize:10
    },
    options:[],
    text:"",
    data:[],
    loading:false,
     dataList:[],
    datalistTo:[],
    exCount:"",
    visibles: false,
    taskName:"",
    code:"",
    provideDept:"",
    taskStatus:"",
    subscribeDept:"",
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
        visibles:true
     
      };
    },
    hideModel(state,action){
      return {
         ...state,
        ...action.payload,
        visibles:false,
      };
    }
  },
   subscriptions:{
      setup({history,dispatch}){
        return history.listen(({ pathname,query })=>{
          console.log(pathname,query ,"pathname,query ");
          if(pathname === "/resources/exchange/report/index"){
               dispatch({
                type:"getList",
                payload:{
                  ...query,
                  pageNum:query.page?query.page:1,
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
        const { data } = yield call(getOverviewTask,{...payload});
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
