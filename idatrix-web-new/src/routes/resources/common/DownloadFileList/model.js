import { getResourceFile } from 'services/catalog';
export default{
	namespace:"downloadfileListModel",

	state:{
		//文件列表
		fileList:[],
		loading:false,
		pagination:{
	      current:1,
	      pageSize:10
	    },
	},

	reducers:{
		 setMetaId(state,action){
	       return {
	        ...state,
	        ...action.payload
	      };
	    },
		save(state,action){
			return {...state,...action.payload}
		}
	},

	effects:{
		*getList({payload},{call,select,put}){
	        yield put({type:"save",payload:{loading:true}})
	        const { data } = yield call(getResourceFile,{...payload});
	        const { code } = data;

	        if(code === "200"){
	        	if(data.data === null){
	        		  yield put({
			              type:'setMetaId',
			              payload:{
			                data:[],
			              
			                loading:false
			              }
			           })
	        	}else{
	        		  yield put({
			              type:'setMetaId',
			              payload:{
			                data:data.data?data.data.results:null,
			                pagination: {
			                  current:1,
			                  pageSize:10
			                },
			                total:data.data.total,
			                loading:false
			              }
			           })
	        	}
	         
	        }
	      },
	},

	subscriptions:{
		 setup({history,dispatch}){
        return history.listen(({ pathname,query,id })=>{
        	let index = pathname .lastIndexOf("\/");  
           let str  = pathname .substring(index + 1, pathname.length);
          if(pathname === "/resources/subscription/mysubscriptions/downloadfile/"+str){
               dispatch({
                type:"getList",
                payload:{
                  ...query,
                  id:str,
                  pageNum:query.page?query.page:1,
                  pageSize:query.pageSize?query.pageSize:10
                }
              })
          }
        })
      }
	}
}