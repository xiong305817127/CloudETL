import { dataDictList,getdataDict,getdictNewlist,GetSibmitdictNew,GetdictDatastatus} from 'services/quality';
import { message } from 'antd';

export default {
	namespace:"dataDictionModel",

	state:{
		total:0,
		text:"",
		datasource:[],
		visibleShow:false,
		loading:false,
		dictNewlist:[],  //获取新建下拉名称
		doceNewlist:[],  //获取新建下拉描述
		datalist:[],
        chenckTrue:false,
		diceName:"",   //字典名称保存
        listType:"",
	},
	subscriptions:{
		setup({history,dispatch}){
	      	return history.listen(({ pathname,query })=>{
				
		        if(pathname === "/gather/dataDictionary"){
		            dispatch({
		              type:"getList",payload:{
		                ...query,
		                page:query.page?query.page:1,
		                size:query.pageSize?query.pageSize:10
		              }
		            })
		        }
	      	})
	    },
	},
	effects:{
		//查询接口
		*getList({payload},{put,select,call}){
			yield put({type:"save",payload:{loading:true}})
			const { data } = yield call(dataDictList,{...payload});
			const { code } = data;
			let datalist =data.data?data.data.rows:[];
			if(code === "200"){
				
		        yield put({
		            type:'save',
		            payload:{
					  listType:parseInt(datalist[0].renterId),
		              datasource:datalist,
		              total:data.data?data.data.total:0,
		              loading:false
		            }
				})
		    }
		},
        //获取新建下拉列表
		*getdictNewlist({payload},{put,select,call}){
			yield put({type:"save",payload:{loading:true}})
			const { data } = yield call(getdictNewlist,{...payload});
			const { code } = data;
   			if(code === "200"){
		        yield put({
		            type:'save',
		            payload:{
		              dictNewlist:data.data.data.dictName,
		              doceNewlist:data.data.data.dictDesc,
		              total:data.data?data.data.count:0,
		              loading:false
		            }
		        })
		    }
		},
		 *GetSibmitdictNew({payload,str},{call,select,put}){
		      const { data } = yield call(GetSibmitdictNew,{...payload});
		      if(data.code === "200"){
		      		message.success("保存成功");
		      		yield put({type:"save",payload:{dataSource:payload,visibleShow:false}});
		      		yield put({type:"getList",payload:{datasource:payload}});
		      } 
	    },
        *GetdictDatastatus({payload,str},{call,select,put}){
		      const { data } = yield call(GetdictDatastatus,{...payload});
		      console.log(data,"====");
		      if(data.code === "200"){
		      		message.success("操作成功");
		      		yield put({type:"getList",payload:{datasource:payload}});
		      }
	    },
	},
	reducers:{
		save(state,action){
			return {...state,...action.payload}
		},
		getConfigId(state,action){
			return {...state,...action.payload}
		}
	}
}