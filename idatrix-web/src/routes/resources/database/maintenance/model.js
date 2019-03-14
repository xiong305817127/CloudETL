import { getMaintenList,getRecall,getBack,getPub } from 'services/catalog';
import { message } from "antd";

export default{
	namespace:"maintenanceModel",

	state:{
		total:0,
		datasource:[],
		selectedRowKeys:[],
		loading:false,
		selectedRows:[],

		//运允许的状态
		canStatus:"none"
	},
	subscriptions:{
	    setup({history,dispatch}){
	      return history.listen(({ pathname,query })=>{
	        if(pathname === "/resources/database/maintenance"){
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
	      const { data } = yield call(getMaintenList,{...payload});

	      const { code } = data;

	      if(code === "200"){
	         yield put({
	            type:'save',
	            payload:{
	              datasource:data.data.results,
	              total:data.data.total,
	              loading:false
	            }
	         })
	      }
	    },
	    *getRecall({payload},{call,put,select}){
	    	const { maintenanceModel } = yield select(state => state);
	      	const { data } = yield call(getRecall,{...payload});
	      	const { code } = data;

	      if(code === "200"){
	        message.success("批量下架成功！");

	        maintenanceModel.selectedRowKeys.splice(0);
	        maintenanceModel.selectedRows.splice(0);

	        yield put({type:"getList"});
	        yield put({type:'save',payload:{selectedRowKeys:[],selectedRows:[],canStatus:"none"} });
	      }
	    },
	     *getBack({payload},{call,put,select}){
	    	const { maintenanceModel } = yield select(state => state);
	      	const { data } = yield call(getBack,{...payload});
	      	const { code } = data;

	      if(code === "200"){
	        message.success("批量退回修改成功！");

	        maintenanceModel.selectedRowKeys.splice(0);
	        maintenanceModel.selectedRows.splice(0);

	        yield put({type:"getList"});
	        yield put({type:'save',payload:{selectedRowKeys:[],selectedRows:[],canStatus:"none"} });
	      }
	    },
	     *getPub({payload},{call,put,select}){
	    	const { maintenanceModel } = yield select(state => state);
	      	const { data } = yield call(getPub,{...payload});
	      	const { code } = data;

	      if(code === "200"){
	        message.success("批量发布成功！");

	        maintenanceModel.selectedRowKeys.splice(0);
	        maintenanceModel.selectedRows.splice(0);

	        yield put({type:"getList"});
	        yield put({type:'save',payload:{selectedRowKeys:[],selectedRows:[],canStatus:"none"} });
	      }
	    }
	},
	reducers:{

		save(state,action){
			return {...state,...action.payload}
		}
	}
}