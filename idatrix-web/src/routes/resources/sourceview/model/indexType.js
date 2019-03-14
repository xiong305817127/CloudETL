import { convertArrayToTree } from '../../../../utils/utils';
import { MLZYgetLib } from  'services/DirectoryOverview';

export default {
  namespace: 'indexType',
  state:{
    options:[],
    data:[],
    loading:false,
    pagniation:{
      pageNum: 0,
      pageSize: 0,
      total: 0
    },
    TetleName:"",
    BaseText:[],
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
    }
  },

 subscriptions:{
      setup({history,dispatch}){
        return history.listen(({ pathname,query })=>{
        	let index = pathname .lastIndexOf("\/");  
          let str  = pathname .substring(index + 1, pathname.length);
          let paths = ["/resources/sourceview/viwe/TypeText/base/base",
                      "/resources/sourceview/viwe/TypeText/department/department",
                      "/resources/sourceview/viwe/TypeText/topic/topic"];

          if(paths.indexOf(pathname) !== -1){
               dispatch({
                type:"getList",
                payload:{
                  ...query,
                  libName:str,
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
        const { data } = yield call(MLZYgetLib,{...payload});
        const { code } = data;
        if(code === "200"){
           yield put({
              type:'setMetaId',
              payload:{
                BaseText:data.data.results,
                pagination: {
                  current: 1,
                  pageSize: 10,
                  total: data.data.total
                },
                loading:false
              }
           })
        }else{
           yield put({
              type:'setMetaId',
              payload:{
                BaseText:[],
                pagination: {
                  current: 1,
                  pageSize: 10,
                  total: []
                },
                loading:false
              }
           })
        }

      
      },
  
  }
};
 
