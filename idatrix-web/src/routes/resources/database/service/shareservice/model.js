import { MLZYgetAllServicePages,downloadDetile } from  'services/DirectoryOverview';
import { message } from 'antd';

export default{
	namespace:"shareserviceModel",
	
	state:{
    options:[],
    id:"",
    info:"",
    data:[],
    loading:false,
    visible:false,
    pagination:{
      current:1,
      pageSize:10
    },
    dataTable:[],
    selectedKeys:"",
    serverModel:'',
    serverId:'',
    clickModel:'',
    //保存提供方代码
    deptCode:"",
    //保存提供方名字
    deptName:"",
    providerName:[],
    BaseText:[],
    pagination: {
      current:1,
      pageSize:10
    },
    total:0,
    visibleHide:true,
    dataDateilList:[]
  },

	reducers:{
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
          if(pathname === "/resources/database/service/shareservice"){
               dispatch({
                type:"getList",payload:{
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
        const { data } = yield call(MLZYgetAllServicePages,{...payload});

        const { code } = data;

        if(code === "200"){
           yield put({
              type:'save',
              payload:{
                BaseText:data.data.results,
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

      *downloadDetile({payload},{call,select,put}){
        yield put({type:"save",payload:{loading:true}})
        const { data } = yield call(downloadDetile,{...payload});

        const { code } = data;

        if(code === "200"){
              message.success("下载成功");
        }
      },
  }
}