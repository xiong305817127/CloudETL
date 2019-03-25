import { getSubProcessedApprove } from 'services/catalog';
import { message } from 'antd';

export default{
	namespace:"subscriptionApprovedModel",
	state:{
		total:0,
		datasource:[],
		loading:false
	},
	subscriptions:{
		setup({history,dispatch}){
	      	return history.listen(({ pathname,query })=>{
		        if(pathname === "/resources/subscription/approved"){
		            dispatch({
		              type:"getList",payload:{
		                ...query,
		                page:query.page?query.page:1,
		                pageSize:query.pageSize?query.pageSize:10
		              }
		            })
		        }
	      	})
	    },
	},
	effects:{
		*getList({payload},{put,select,call}){
			console.log("调用");
			yield put({type:"save",payload:{loading:true}})
			const { data } = yield call(getSubProcessedApprove,{...payload});
			const { code } = data;
			if(code === "200"){
		        yield put({
		            type:'save',
		            payload:{
		              datasource:data.data?data.data.results:[],
		              total:data.data?data.data.total:0,
		              loading:false
		            }
		        })
		    }
		},
	},
	reducers:{
		save(state,action){
			return {...state,...action.payload}
		}
	}
}