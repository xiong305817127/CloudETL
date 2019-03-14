import { getProcessedPub,getHistoryInfo } from 'services/catalog';
import { message } from 'antd';

export default{
	namespace:"releaseApprovedModel",

	state:{
		//审批历史数据
		datasource2:[],
		CheckHistoryShow:false,

		total:0,
		datasource:[],
		loading:false,

		//选择审批历史的名字
		selectName:""
	},
	subscriptions:{
		setup({history,dispatch}){
	      	return history.listen(({ pathname,query })=>{
		        if(pathname === "/resources/release/approved"){
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
			const { data } = yield call(getProcessedPub,{...payload});
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
		*getCheck({payload,selectName},{call,select,put}){
	      const { data } = yield call(getHistoryInfo,{...payload});
	      const { code } = data;

	      if(code === "200"){
	      	let args = [];
            let num = 1;
            if(data.data){
              for(let index of data.data){
                args.push({key:num++,...index})
              }
            }
	        yield put({
	            type:'save',
	            payload:{
	              datasource2:args,
	              CheckHistoryShow:true,
	              selectName
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