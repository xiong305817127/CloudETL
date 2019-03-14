import { getWaitPub,getBatchProcess } from 'services/catalog';
import { message } from 'antd';

export default {
	namespace:"releaseApproval",

	state:{
		total:0,
		datasource:[],
		selectedRowKeys:[],
		loading:false,
		selectedRows:[]
	},
	subscriptions:{
		setup({history,dispatch}){
	      	return history.listen(({ pathname,query })=>{
		        if(pathname === "/resources/release/approval"){
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
			yield put({type:"save",payload:{loading:true}})
			const { data } = yield call(getWaitPub,{...payload});
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
		*getBatchProcess({payload},{call,put,select}){
	    	const { releaseApproval } = yield select(state => state);
	      	const { data } = yield call(getBatchProcess,{...payload});
	      	const { code } = data;

	      if(code === "200"){
	        message.success("批量发布成功！");

	        releaseApproval.selectedRowKeys.splice(0);
	        releaseApproval.selectedRows.splice(0);

	        yield put({type:"getList"});
	        yield put({type:'save',payload:{selectedRowKeys:[],selectedRows:[]} });
	      }
	    }
	},
	reducers:{
		save(state,action){
			return {...state,...action.payload}
		}
	}
}