import { MLZYgetAllSourceServicePages } from  'services/DirectoryOverview';
import { message } from 'antd';

export default {
  namespace: 'sourceserviceModel',
  state:{
    options:[],
    text:"",
    info:"",
    data:[],
    loading:false,
    visible:false,
    pagination:0,
    dataTable:'',
    selectedKeys:"",
    serverModel:'',
    serverId:'',
    clickModel:'',
    //保存提供方代码
    deptCode:"",
    //保存提供方名字
    deptName:"",

    BaseText:[],
    pagination: {
      current:1,
      pageSize:10
    },
    total:0,
    visibleHide:true,
  },
  reducers: {
    setMetaId(state,action){
       return {
        ...state,
        ...action.payload,
      };
    },
     save(state,action){
       return {
        ...state,
        ...action.payload,
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
          if(pathname === "/resources/database/service/sourceservice"){
            
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
        const { data } = yield call(MLZYgetAllSourceServicePages,{...payload});
        const { code } = data;
        if(code === "200"){
           yield put({
              type:'save',
              payload:{
                BaseText:data.data?data.data.results:[],
                pagination: {
                  current:1,
                  pageSize:10
                },
                 total:data.data?data.data.total:"",
                loading:false
              }
           })
        }
      },
  }
  
};
