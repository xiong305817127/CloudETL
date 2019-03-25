import {
	getNodeTypes,
	getNodeTotal,
	getTaskByNodeType,
	getStatisticsReferenceByNodeType
} from "services/quality";

const initState = {
	//稽核总览数据
	aduitsList:[],
	//稽核数据统计
	aduitsTotal:{},
	//稽核总览节点数据
	nodeDataSource:[],
	//标识
	flag:"year",
	//节点类型
	nodeType:"CHARACTER",
	//类型数据
	tabType:"CHARACTER",
	typeList:[],
	loading:false
};

/*节点模块配置*/
export default {
  namespace: "qualityIndexModel",
  state: { ...initState },
  reducers: {
    save(state, action) {
      return { ...state, ...action.payload };
    },
    reset() {
      return { ...initState };
    }
	},
	subscriptions:{
		setup({ history,dispatch }){
			return history.listen(({ pathname,query })=>{
				if(pathname === "/gather/qualityAnalysis/indexPage"){
					dispatch({ type:"getNodeTypes",payload:{ ...query }  });
					dispatch({ type:"getTaskByNodeType",payload:{ ...query }  });
					dispatch({ type:"getNodeTotal"});
					dispatch({ type:"getStatisticsReferenceByNodeType",payload:{...query}});
				}
			})
		}
	},
  effects: {
    *getNodeTypes({ payload }, { call, put,select }) {
			const { flag,aduitsList } = yield select(state=>state.qualityIndexModel);
			//如果已经是最新数据，则不请求
			if((( payload.flag === flag) || !payload.flag) && aduitsList.length > 0) return;
      const { data } = yield call(getNodeTypes, { flag,...payload });
      const { code } = data;
      if (code === "200") {
        yield put({
          type: "save",
          payload: { aduitsList:data.data,...payload }
        });
      }
		},
		*getNodeTotal({  }, { call, put,select }) {
			const { aduitsTotal } = yield select(state =>state.qualityIndexModel);
			console.log(aduitsTotal,"测试");
			if(aduitsTotal.total) return;
      const { data } = yield call(getNodeTotal);
      const { code } = data;
      if (code === "200") {
        yield put({
          type: "save",
          payload: { aduitsTotal:data.data }
        });
      }
		},
		*getTaskByNodeType({ payload },{ select,call,put }){
			const { flag,nodeType,nodeDataSource } = yield select(state=>state.qualityIndexModel);
			//如果已经是最新数据，则不请求
			if( (!nodeType ||  nodeType ===  payload.nodeType) && nodeDataSource.length > 0 &&
			(payload.flag === flag  || !payload.flag)) return;
      const { data } = yield call(getTaskByNodeType, { flag,nodeType,...payload });
      const { code } = data;
      if (code === "200") {
        yield put({
          type: "save",
          payload: { nodeDataSource:data.data,...payload }
        });
      }
		},
		*getStatisticsReferenceByNodeType({ payload },{ select,call,put }){
			const { tabType,typeList } = yield select(state=>state.qualityIndexModel);
			//如果已经是最新数据，则不请求
			if((!tabType || (tabType === payload.tabType)) && typeList.length > 0 ) return;
			yield put({ type: "save",payload: { loading:false } });
			const nodeType = payload.tabType?payload.tabType:tabType;
      const { data } = yield call(getStatisticsReferenceByNodeType, { nodeType });
      const { code } = data;
      if (code === "200") {
        yield put({
          type: "save",
          payload: { typeList:data.data,tabType:nodeType,loading:true }
        });
      }
		},
  }
};
