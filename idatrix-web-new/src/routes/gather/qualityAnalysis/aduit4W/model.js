import {
	get4WByExecId,
	get4WListByRenter
} from "services/quality";

const initState = {
	//异常数据报告
	detailInfo:{},
	//稽核数据统计
	aduitDataSource:[],
	//total
	total:0
};

/*节点模块配置*/
export default {
  namespace: "aduit4WModel",
  state: { ...initState },
  reducers: {
    save(state, action) {
      return { ...state, ...action.payload };
    }
	},
	subscriptions:{
		setup({ history,dispatch }){
			return history.listen(({ pathname,query })=>{
				if(pathname === "/gather/qualityAnalysis/aduit4W"){
					dispatch({ type:"get4WListByRenter",payload:{ ...query }})
				}
			})
		}
	},
  effects: {
    // *get4WByExecId({ payload }, { call, put }) {
    //   const { data } = yield call(get4WByExecId, { ...payload });
    //   const { code } = data;
    //   if (code === "200") {
    //     yield put({
    //       type: "save",
    //       payload: { detailInfo:data.data }
    //     });
    //   }
		// },
		*get4WListByRenter({ payload }, { call, put }) {
      const { data } = yield call(get4WListByRenter,{pageSize:12,...payload});
			const { code } = data;
			let detailInfo = {};
      if (code === "200") {
				if(data.data.rows && data.data.rows[0]){
					detailInfo = data.data.rows[0];
					data.data.rows[0].rowClassName = true;
				}
        yield put({
          type: "save",
          payload: { aduitDataSource:data.data.rows?data.data.rows:[],total:data.data.total,detailInfo }
        });
      }
		}
  }
};
