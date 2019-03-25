
import { getWebservice } from  'services/DirectoryOverview';
import { message } from 'antd';

export default{
	namespace:"serveroptian",
	state:{
		total:0,
		loading:false,
		datasource:[],
		id:"",
		name:"",
		subNo:"",
		datasource1:[{
			name:"inputParams",
			type:"List<ParamDTO>",
			file:"否",
			dateli:"表示查询条件，ParamDTO包含以下内容：	paramCode数据库字段名称	paramName资源中信息项名称paramValue数值;"
		},{
			name:"subscribeKey",
			type:"String",
			file:"是",
			dateli:"公共参数展现的服务参数。"
		},{
			name:"pageNum",
			type:"Int",
			file:"否",
			dateli:"查询起始页序号，默认0"
		},{
			name:"pageSize",
			type:"Int",
			file:"否",
			dateli:"查询页面大小，默认20"
		}],
		datasource2:[{
			name:"statusCode",
			type:"int",
			size:"200",
			dateli:"返回状态码，200表示正确状态",
		},{
			name:"errorMsg",
			type:"String",
			size:"如：输入参数校验失败",
			dateli:"异常信息描述，正常时候为null",
		},{
			name:"totalSize",
			type:"int",
			size:"",
			dateli:"查询记录总条数",
		},{
			name:"pageNum",
			type:"int",
			size:"200",
			dateli:"当前页数",
		},{
			name:"sql",
			type:"String",
			size:"",
			dateli:"查询时执行的sql",
		},{
			name:"data",
			type:"List<Map<String, Object>>",
			size:"",
			dateli:"查询返回数据，其中每一个Map记录一条数据，其中Key为列名，Value为列数值。",
		},{
			name:"columns",
			type:"List<String>",
			size:"",
			dateli:"所有的列名称",
		}],
	},
	subscriptions:{
		setup({history,dispatch}){
			  /*dispatch({
		              type:"getList",
			              payload:{
							
			              	id:str,
			                ...query,
			                page:query.page?query.page:1,
			                pageSize:query.pageSize?query.pageSize:10
			              }
			            })*/
			
	      	return history.listen(({ pathname,query })=>{
	      		let index = pathname .lastIndexOf("\/");  
               let str  = pathname .substring(index + 1, pathname.length);
	      		
		        if(pathname === "/resources/subscription/mysubscriptions/dataserver/"+str){
		        	console.log(query,"...query,");
		            dispatch({
		              type:"getList",
			              payload:{
							
			              	id:str,
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
			const { data } = yield call(getWebservice,{id:payload.id});
			yield put({
		              type:"save",
			              payload:{
							name:payload.name,
							subNo:payload.subNo,
			              }
			            })
			const { code } = data;
			if(code === "200"){
				console.log([data.data],"数据payload",payload);
		        yield put({
		            type:'save',
		            payload:{
		            	id:payload.id,
						name:payload.name,
						subNo:payload.subNo,
						datasource:[data.data]?[data.data]:[],
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