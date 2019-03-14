/**
 * Created by Administrator on 2017/8/29.
 */
import { message } from 'antd';
import { newTrans,getDelete_trans } from  'services/quality';
import { routerRedux } from 'dva/router';

const initState = {
  //视图状态  false欢迎页  true 设计页
  view:false,

  activeArgs:new Map(),
  activeKey:"",
  //要删除的名字
  removeKey:"",
  //组件是否需要更新
  shouldUpdate:false,
  //是否有任务
  hasTask:false,
  //新建任务
  newFile:false,
  //弹窗展示
  modelVisible:false
};

export default {
  namespace: 'designSpace',
  state: {...initState},
  reducers: {

    openFile(state,action){
      window.location.href = '#/gather/designplatform';

      let args = state.activeArgs;
      if(!state.activeArgs.has(action.payload.activeKey)){
        let viewId = jsPlumbUtil.uuid();
        args.set(action.payload.activeKey,viewId);
      }
      return {
        ...state,
        ...action.payload,
        model:"view",
        activeArgs:args,
        shouldUpdate:true,
        hasTask:true
      };
    },
    newFile(state,action){
      window.location.href = '#/gather/designplatform';
      let args = state.activeArgs;
      args.set(action.payload.activeKey,action.payload.viewId);
      return {
        ...state,
        model:"view",
        activeKey:action.payload.activeKey,
        activeArgs:args,
        newFile:true,
        hasTask:true
      };
    },
    changeModel(state,action){
        return {
          ...state,
          ...action.payload
        }
    },
    changeName(state,action){
      let args = state.activeArgs;
      args.set(action.payload.newname,args.get(action.payload.name));
      args.delete(action.payload.name);

      return {
        ...state,
        activeArgs:args,
        activeKey:action.payload.newname
      };
    },
    closeModel(state,action){
        let args = state.activeArgs;
        let hasTask = state.hasTask;
        let shouldUpdate = state.shouldUpdate;
        let activeKey = state.activeKey;

        if(action.payload.removeKey === state.activeKey){
            args.delete(action.payload.removeKey);
            let panels =  [...args.keys()];
            if(args.size > 0){
              activeKey = panels[panels.length -1];
              shouldUpdate = true;
            }else{
              activeKey = "";
            }
        }else{
            args.delete(action.payload.removeKey);
        }

        if(args.size === 0){
            hasTask = false
        }

        return {
          ...state,
          activeArgs:args,
          hasTask:hasTask,
          activeKey:activeKey,
          shouldUpdate:shouldUpdate,
          modelVisible:false
        }
    },
    clearHeader(state,action){
        return{
          model:"welcome",
          activeArgs:new Map(),
          activeKey:"",
          //要删除的名字
          removeKey:"",

          //组件是否需要更新
          shouldUpdate:false,
          //是否有任务
          hasTask:false,
          //新建任务
          newFile:false,
          //弹窗展示
          modelVisible:false
        }
    }
  },
  effects: {
    //新建分析任务
    *newAnalysis({ payload }, { select,call,put } ){
      const { data } = yield newTrans({...payload,group:"default"});
      const { code } = data;
      if( code === "200"){
        message.success("新建转换任务成功");
        yield put({
          type:"save",
          payload:{
            activeKey:payload.name,
            viewId
          }
        })

        yield put(routerRedux.push("/gather/qualityAnalysis/designSpace"));
      }
    },
    *deleteTrans({payload}, {select, call, put}) {
        const {data} = yield getDelete_trans(payload.removeKey);
        const {code} = data;
        if(code === "200"){
            yield put ({
              type:"closeModel",
              payload:{
                removeKey:payload.removeKey
              }
            });
            message.success("删除成功");
        }
    },
    *saveNewTrans({payload}, {select, call, put}) {
      const { data } = yield newTrans({...payload,group:"default"});
      const { code } = data;
      console.log(payload,"路由跳转");

      if( code === "200"){
        message.success("新建转换任务成功");
        yield put(routerRedux.push("/gather/qualityAnalysis/designSpace"));

        // message.success("新建转换任务成功");
        // if(payload.copy_name){
        //   yield put ({
        //     type:"openFile",
        //     payload:{
        //       activeKey:payload.info_name
        //     }
        //   });
        // }else{
        //   yield put ({
        //     type:"newFile",
        //     payload:{
        //       activeKey:payload.info_name,
        //       viewId:payload.viewId
        //     }
        //   });
        // }
      }
    }
  }
};
