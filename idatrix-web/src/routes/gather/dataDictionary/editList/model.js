import { GetdictAllList,GetdictData,Getupdate,deletedictData} from 'services/quality';
import { message } from 'antd';
import { hashHistory } from 'react-router';
export default {
	namespace:"DataDictionaryEditModel",

	state:{
		total:0,
		datasource:[],
		loading:false,
		stdVal1:"",  //参考标准值参数
        valueArr:"",   //参考近似值项目
        name:"",  //   标准值名称
        newid:"",    //    标准值id
        splIndexVal:"",   //  保存地址
				arrlist:[],
				errorMessage:"",
				successMessage:"",
				dictdata:"",
   	},
	subscriptions:{
		setup({history,dispatch}){
	      	return history.listen(({ pathname,query })=>{
	      		let index = pathname .lastIndexOf("\/");  
                let str  = pathname .substring(index + 1, pathname.length);
            
		        if(pathname === "/gather/dataDictionary/edit/"+str){
		            dispatch({
		              type:"getList",payload:{
		                ...query,
		                name:str,
		                page:query.page?query.page:1,
		                size:query.pageSize?query.pageSize:10
		              }
		            })
		        }
	      	})
	    },
	},
	effects:{
		*getList({payload},{put,select,call}){
			yield put({type:"save",payload:{loading:true}})
			const { data } = yield call(GetdictAllList,{...payload});
		   
			const { code } = data;
			let references = data.data?data.data.other:null;  //根据获取的数组转换成字符
			let dictdatalist = data.data?data.data.other.dictdata:null; 
			if(code === "200"){
		        yield put({
		            type:'save',
		            payload:{
		              datasource:data.data?data.data.rows:[],
									id:references.dictId,
									dictdata:parseInt(dictdatalist.renterId),
		              loading:false,
		              total:data.data?data.data.total:0,
		            }
		        })
		    }
		},

		 *GetdictData({payload,str},{call,select,put}){
		      const { data } = yield call(GetdictData,{...payload});
		      const { code } = data;
		      if(code === "200"){
		      		message.success("保存成功");
							yield put({type:"save",payload:{dataSource:payload}});
		      }
	    },
		 *Getupdate({payload,str},{call,select,put}){
		      const { data } = yield call(Getupdate,{...payload});
		      const { code } = data;
        
		      if(code === "200"){
		      		message.success("保存成功");
		      		yield put({type:"save",payload:{arrlist:payload}});
		      		hashHistory.push(`/gather/dataDictionary`);
		      }
			},

			*deletedictData({payload,str},{call,select,put}){
				const { data } = yield call(deletedictData,{...payload});
				const { code } = data;
				console.log(payload,"payload=====");
				let idj = payload.dictId;
				if(code === "200"){
						message.success("删除成功");
						yield put({type:"save",payload:{dataSource:payload}});
						hashHistory.push(`/gather/dataDictionary/edit/`+payload.dictId);
					//	yield put({type:"getList",payload:{idj}});
				}
		},
			
			
	},
	reducers:{
		save(state,action){
			return {...state,...action.payload}
		},
		 setMetaId(state, action) {
		      return {
		        ...state,
		        ...action.payload
		      };
		    },
	}
}