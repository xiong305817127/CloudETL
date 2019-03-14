/**
 * Created by Administrator on 2017/9/11.
 */
/**
 * 云化数据集成系统，持久层
 * @param dispatch
 */
import { getDeployMode,getLoginUser } from  '../../../../services/gather';

export default {
  namespace: 'cloudetlCommon',
  state: {
    metaStore: "",
    metaCube: "",
    isMetacube:false, 
    transEngine:""  
  },
  reducers: {
    updateData(state,action){
        return {...state,...action.payload,isMetacube:action.payload.metaCube !== "Pentaho"};
    }
  },
  subscriptions: {
    setup({ dispatch, history }) {
      history.listen(location => {
        let args = location.pathname.split("/");
        if (args[1] === "gather") {
          dispatch({
            type:"getAllResource",
            payload:{}
          })
        }
      })
    }
  },
  effects: {
    *getAllResource({}, { put,call, select }) {
      const {cloudetlCommon} = yield select(state => state);
      const {metaCube, metaStore} = cloudetlCommon;
      if(metaCube && metaStore){
          return;
      }else{
          const { data } = yield call(getDeployMode);
          const {code} = data;
          if(code === "200"){
              yield put({
                 type:"updateData",
                  payload:{
                    ...data.data
                  }
              })
          }
      }
    }
  }
};