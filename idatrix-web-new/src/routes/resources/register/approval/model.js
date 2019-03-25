import { getWaitRegInfo,getBatchProcess } from 'services/catalog';
import { message } from "antd";

export default{
	namespace:"approvalModel",

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
	        if(pathname === "/resources/register/approval"){
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
	      const { data } = yield call(getWaitRegInfo,{...payload});

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
	    	const { approvalModel } = yield select(state => state);
	      	const { data } = yield call(getBatchProcess,{...payload});
	      	const { code } = data;

	      if(code === "200"){
	        message.success("批量注册成功！");

	        approvalModel.selectedRowKeys.splice(0);
	        approvalModel.selectedRows.splice(0);

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