import { getSubDetail,getSubProcess } from "services/catalog";
import { normalList,dbList,serviceList,shareMethodArgs } from "../../../../constants";
import { message } from "antd";
import { hashHistory } from 'dva/router';

export default{
	namespace:"subscriptionDetailModel",

	state:{
		dataSource:[],

		searchSource:[],
		subSource:[],
		shareMethod:0,
		dbShareMethod:0,

		status:"new"
	},

	reducers:{
		save(state,action){
			return {...state,...action.payload}
		}
	},

	effects:{
		*getSubDetail({ payload,status },{ call,select,put }){
			const { data } = yield call(getSubDetail,{...payload});

			const { code } = data;
			if(code === "200"){
				let args = [];
				let dataSource = [];
				let key = 0;
				let subSource = [];
				let searchSource = [];
				if(data.data.shareMethod === 1 && data.data.dbShareMethod === 1){
					args = dbList;
				}else if(data.data.shareMethod === 3 || data.data.dbShareMethod === 2){
					args = serviceList;
				}else{
					args = normalList;
				}
				if(data.data.inputDbioList){
					let key = 1;
					for(let index of data.data.inputDbioList){
						subSource.push({
							...index,
							key:key++,
						})
					}
				}
				if(data.data.outputDbioList){
					let key = 1;
					for(let index of data.data.outputDbioList){
						searchSource.push({
							...index,
							key:key++,
						})
					}
				}
				for(let index of args.keys()){
					if(index === "shareMethod"){
						dataSource.push({
							fieldname:args.get(index),
							value:shareMethodArgs[data.data[index]].title,
							key:key++
						})
					}else{
						dataSource.push({
							fieldname:args.get(index),
							value:data.data[index],
							key:key++
						})
					}
				}
				yield put({
					type:"save",
					payload:{
						dataSource,subSource,searchSource,status,
						shareMethod:data.data.shareMethod,
						dbShareMethod:data.data.dbShareMethod,
					}
				})
			}
		},
		*getSubProcess({payload},{call,select,put}){
			const { status } = yield select(state => state.subscriptionDetailModel);
			const { data } = yield call(getSubProcess,{...payload});
			const { code } = data;
			if(code === "200"){
				message.success("审批成功！");
				if(status === "approval"){
					hashHistory.push("/resources/subscription/approval");
				}
			}
		}
	}
}