/**
 * Created by Administrator on 2017/4/10.
 * log日志
 */
import {Trans_exec_configuration } from  '../../../../../services/gather';
import { message } from  'antd';

export default {
  namespace: 'transdebug',
  state: {
    visible:"none",
    viewId:"",
    transName:"",
    model:"",
    shouldRequest:true
  },
  reducers: {
    save(state,action){
      return {
        ...state,
        ...action.payload
      };
    },
    openDebug(state,action){

      return {
        ...state,
        ...action.payload,
        model:"openDebug"
      };
    },
    pauseDebug(state,action){
      return {
        ...state,
        ...action.payload,
        model:"pauseDebug"
      };
    },
    cleanDebug(state,action){
      return {
        visible:"none",
        viewId:"",
        transName:"",
        model:"cleanDebug"
      };
    },
    changeTabs(state,action){
      return {
        ...state,
        visible:"none",
        viewId:action.viewId,
        transName:action.transName,
        model:"cleanDebug"
      };
    }
  },
  effects:{
      *executeTrans({payload}, {select, call, put}) {
        const { owner } = yield select(state=>state.transheader);
        const { data } = yield Trans_exec_configuration({...payload.obj,owner});
        const { code } = data;
        if( code === "200"){
          yield put ({
            type:"openDebug",
            payload:{
              viewId:payload.viewId,
              transName:payload.actionName,
              visible:"block"
            }
          });
          message.success("开始执行");
        }
      }
  }
};



