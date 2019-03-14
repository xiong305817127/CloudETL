import { getDefaultEngineList,getEdit_trans_attributes,execBatchTrans } from  '../../../../../services/gather';

/*提示框消息模块*/
export default {
  namespace: 'rundebugger',
  state: {
    visible:false,
    actionName:"",
    viewId:"",
    executeList:[],
    model:"default",
    runModel:"default",
    selectedRows:[],
    params:{},
    dataSource:[],
    items:[]
  },
  reducers: {
    'show'(state, action){
      return {
        ...state,
        ...action.payload
      }
    },
    'hide'(state, action){
      return {
        ...state,
        visible: action.visible,

        model:"default",
        runModel:"default",
        actionName:"",
        viewId:"",
        executeList:[],
        selectedRows:[],
        dataSource:[]
      }
    },
    'changeModel'(state, action){
      return {
        ...state,
        model:action.model
      }
    }
  },
  effects: {
    *queryExecuteList({payload}, { select, call, put }) {
      const{ items }  = yield select(state=>state.transspace);
      const { owner} = yield select(state=>state.transheader);
      yield put({
        type: 'show',
        payload:{
          ...payload
        }
      });

      const data1 = yield call(getDefaultEngineList, {owner});
      const data2 = yield call(getEdit_trans_attributes, {name: payload.actionName,owner});

      const { params } = data2.data.data;

      const {code} = data1.data;
      if(code === "200"){
        yield put({
          type: 'show',
          payload:{
            params:params,
            items:items,
            executeList:data1.data.data
          }
        });
      }
    },
    *queryBatchList({payload}, { select, call, put }) {
      yield put({
        type: 'show',
        payload:{
          ...payload
        }
      });
      const { data } = yield call(getDefaultEngineList);
      const {code} = data;
      if(code === "200"){
        yield put({
          type: 'show',
          payload:{
            executeList:data.data
          }
        });
      }
    },
    *batchRun({payload}, { select, call, put }) {
      yield put({
        type: 'hide'
      });
      let args = [];

      for(let index of payload.selectedRows){
        args.push(index.name);
      }

      yield execBatchTrans({
          names:args,
          configuration:payload.configuration
      });

    }
  }
};

