import { queryPlatformList,get_front_pos,insert_database_table_fields,update_database_table_fields,check_get_name,search_ftp_table_fields } from "services/metadata";
import { message } from "antd";

export default{
	namespace:"datasource",

	state:{
		total:0,
		data:[],
		loading:false,
		info:{},

		//前置机列表
		frontList:[],
		//显示新建注册
		visible:false,
		//触发弹框动作
		action:'new',
		//类型
		type:"",

	},
	subscriptions:{
		steup({history,dispatch}){
			return history.listen(({ pathname,query })=>{
				if(pathname === "/analysis/DataSource"){
					dispatch({
						type:"queryPlatformList",
						payload:{
							current: query.page || 1,
          					pageSize: query.pageSize || 10
						}
					})
				}
			})
		}
	},
	effects:{
		*getFrontList({payload},{call,put,select}){
			let type = payload.action === "new"?"create":"";
		

			/**
			 * 获取state
			 * edited by steven leo 2018/09/20
			 */
			const account = yield select(state=>state.account);
			// const { renterId } = yield select(state=>state.account);

			const {data:{msg,data}} = yield call(get_front_pos,{renterId: account.renterId,type});
			if(msg === "Success"){
				yield put({type:"save",payload:{...payload,frontList:data.rows,visible:true}})
			}
			// else{
			// 	message.error(msg);
			// }
 		},
		*queryPlatformList({payload},{call,put,select}){
			yield put({type:"showLoading"})
			const account = yield select(state=>(state.account))

			const { data } = yield call(queryPlatformList,{...payload},{sourceId:1,renterId:account.renterId});
			const { msg } = data;

			if(msg === "Success"){
				const {total,rows} = data.data;
		        rows.map( (row, index) => {
		            row.key = row.dsId;
		            if(row.frontEndServer){
		              row.serverName =  row.frontEndServer.serverName;
		              row.serverIp =  row.frontEndServer.serverIp;
		              row.organization =  row.frontEndServer.organization;
		            }
		            return row;
		        });

		        yield put({
		        	type:"save",
		        	payload:{
		        		total:total,
		        		data:rows,
		        		loading:false
		        	}
		        })
			}
			
			// else{
			// 	message.error(msg);
			// }
 		},
 		*handleSubmit({payload},{call,put,select}){
 			const { renterId } = yield select(state=>state.account);
 			const { data } = yield call(insert_database_table_fields,{renterId,...payload});
 			if (data && data.code === '200') {
 			  yield put({
 			  	 type:"queryPlatformList",
 			  	 payload:{
 			  	 	current:1,
          			pageSize:10
 			  	 }
 			  })
              message.success('新建成功');
			}
			// else if(data && data.code === '601'){
            //     message.error("密码强度不符合");
            // }else if(data && data.code === '602'){
            //       message.error("连接失败，请检查服务器配置");
            // }else if(data && data.code === '606'){
            //       message.error("请求失败");
            // } else {
            //   message.error("新增失败,请查看您所请求的前置机的IP是否存在");
            // }
 		},
 		*handleUpdata({ payload },{ call,put,select }){
			const account = yield select(state=>state.account);
 			const { data } = yield call(update_database_table_fields,{...payload,renterId:account.renterId});
 			if (data && data.code === '200') {
              message.success('修改成功');
			}
			//  else {
            //   message.error(data && data.msg || '修改失败');
            // }
 		}
	},
	reducers:{
		save(state,action){
			return {...state,...action.payload}
		},
		showLoading(state,action){
			return {...state,loading:true}
		}
	}
}