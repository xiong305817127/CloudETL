import { getConfig,handleSubmit } from 'services/catalog';
import { message } from 'antd';

export default{
	namespace:"systemparmModel",
	
	state:{
		config:{},
		//文件资源目录名
		fileRoot:"",
		//文件资源目录
		originFileRoot:"",

		fileId:"",
		filesId:"",
	},
	subscriptions:{
	    setup({history,dispatch}){
	      return history.listen(({ pathname,query })=>{
	        if(pathname === "/resources/database/systemparm"){
	        	dispatch({ type:"resourcesCommon/getHdfsTree"});
	      		dispatch({ type:"resourcesCommon/getRoles"});
	      		dispatch({ type:"getConfig"});
	        }
	      })
	    }
	},
	effects:{
		*getConfig({payload},{call,put}){
			const {data} = yield call(getConfig);
			const { code } = data;
			if(code === "200"){
				const { fileRootIds,originFileRootIds,fileRoot,originFileRoot } = data.data;

				const args1 = fileRoot.split("/").filter(index=> index.trim());
				const args2 = originFileRoot.split("/").filter(index=> index.trim());

				yield put({type:"save",payload:{
					config:{...data.data,fileRootIds:fileRootIds?fileRootIds.split(","):"",originFileRootIds:originFileRootIds?originFileRootIds.split(","):""},
					fileRoot:args1.join("/"),originFileRoot:args2.join("/")
				}})
			}

		},
		*handleSubmit({payload},{call,put}){
			const {data} = yield call(handleSubmit,{...payload});
			const { code } = data;
			if(code === "200"){
				message.success("保存成功！")
			}
		}
	},
	reducers:{
		save(state,action){
			return {...state,...action.payload}
		}
	}
}