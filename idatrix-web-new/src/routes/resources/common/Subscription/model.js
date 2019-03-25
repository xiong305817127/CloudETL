import { getConfigInit,getSubscriptionAdd } from "services/catalog";
import { message } from "antd";

export default{
	namespace:"subscriptionModal",

	state:{
		visible:false,
		name:"",
		config:{},

		//查询数据
		searchSource:[],

		//订阅数据
		subscriptionSource:[],

		//订阅不可编辑字段
		disabledKey:[]
	},

	reducers:{
		save(state,action){
			console.log("调用");

			return {...state,...action.payload}
		}
	},

	effects:{
		*getSubDetail({ payload,name },{ call,select,put }){
			const { data } = yield call(getConfigInit,{...payload});

			const { code } = data;
			if(code === "200"){
				let searchSource = [];
				let subscriptionSource = [];

				let disabledKey = [];
				if(data.data.outputDbioList){
					let key = 0;
					for(let index of data.data.outputDbioList){
						searchSource.push({
							...index,key:key++
						})
					}
				}

				if(data.data.inputDbioList){
					let key = 1;
					for(let index of data.data.inputDbioList){
						subscriptionSource.push({
							...index,key:key++
						})
						if(index.requiredFlag){
							disabledKey.push(index.id);
						}
					}
				}

				yield put({
					type:"save",
					payload:{
						config:data.data,
						visible:true,
						name,disabledKey,
						searchSource,subscriptionSource
					}
				})
			}
		},
		*getSubscriptionAdd({ payload },{ select,put,call }){
			console.log(payload,"数据处理");
			const { data } = yield call(getSubscriptionAdd,{deptName:payload.deptName,endDate:payload.endDate,
				                        inputDbioList:payload.inputDbioList,outputDbioList:payload.outputDbioList,
										resourceId:payload.resourceId,subscribeReason:payload.subscribeReason,shareMethod:payload.shareMethod});
			
			const { code } = data;
			if(code === "200"){
				yield put({
					type:"save",
					payload:{
						visible:false
					}
				})
				/** 按分页去刷新 */
				yield put({type:"indexModel/getList",payload:{ page:payload.page,pageSize:payload.pageSize, }})
			}
		}

	}
}