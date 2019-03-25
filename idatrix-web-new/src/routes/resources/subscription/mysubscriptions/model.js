import { getSubscriptionList,getBatchProcess } from 'services/catalog';
import { getHistory } from  'services/DirectoryOverview';
import { message } from 'antd';

export default{
	namespace:"mysubscriptionsModel",
	state:{
		total:0,
		datasource:[],
		selectedRowKeys:[],
		loading:false,
		selectedRows:[],

		changeHistory:false,
		selectName:"",
		datasource1:[],
		id:"",
		approveStartTime:'',
        approveEndTime:'',
	},
	subscriptions:{
		setup({history,dispatch}){
	      	return history.listen(({ pathname,query })=>{
		        if(pathname === "/resources/subscription/mysubscriptions"){
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
			const { data } = yield call(getSubscriptionList,{...payload});
			const { code } = data;
			if(code === "200"){
				console.log(data.data.results,"数据");
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
	    },
	    *getChangeHistory({payload,selectName},{call,put,select}){
           console.log(payload,selectName,"payload,selectName");
	    	yield put({
	    		type:"save",
	    		payload:{
	    			selectName,
	    			id:payload.id,
	    			changeHistory:true,
	    			datasource1:[]
	    		}
	    	})
	    },
	    *getHistory({payload,selectName},{put,select,call}){
			yield put({type:"save",payload:{loading:true}})
			const { data } = yield call(getHistory,{...payload});
			const { code } = data;
			if(code === "200"){
				console.log(data.data,"数据");
		        yield put({
		            type:'save',
		            payload:{
		              selectName,
		              changeHistory:true,
		              id:payload.id,
		              datasource1:data.data?data.data:null,
		              total:data.data?data.data.total:0,
		              loading:false
		            }
		        })
		    }
		}
	},
	reducers:{
		save(state,action){
			return {...state,...action.payload}
		}
	}
}