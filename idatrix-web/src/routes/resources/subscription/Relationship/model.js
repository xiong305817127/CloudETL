/*注释
alisa
2018-09-26
我的订阅  中的订阅关系管理  
model  RelationshipModel
*/
import { getOwnManage,stop,resume } from 'services/DirectoryOverview';
import { message } from 'antd';

export default {
	namespace:"RelationshipModel",

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
		        if(pathname === "/resources/subscription/Relationship"){
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
			const { data } = yield call(getOwnManage,{...payload});
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
		*getstop({payload},{call,put,select}){
	      	const { data } = yield call(stop,{...payload});
	      	const { code } = data;
	      if(code === "200"){
	        message.success("取消订阅成功！");
	        yield put({type:"getList"});
	      }
	    },
	    *getresume({payload},{call,put,select}){
	      	const { data } = yield call(resume,{...payload});
	      	const { code } = data;
             console.log(data,"data++getresume");
	      if(code === "200"){
	        message.success("订阅成功！");
	        yield put({type:"getList"});
	      }
	    }
	},
	reducers:{
		save(state,action){
			return {...state,...action.payload}
		}
	}
}