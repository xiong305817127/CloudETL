import { getTrans_exec_id, getTrans_status, getTransExecInfo } from 'services/quality';
import { runStatus, stopStatus, pauseStatus, errorStatus } from "../../../constant";

const Timer = (time)=>new Promise((resolve)=>{
    setTimeout(()=>{
        console.log(time,"延时1000");
        resolve();
    },time)
})

const initState = {
    //日志列表
    logList: [],
    //是否显示分析信息 true 显示  false 隐藏
    showInfo: false,
    //步骤执行信息
    StepMeasure: [],
    //展现弹框
    visible:false,
    //执行的名字
    name:"",
    //是否轮询
    isReload:true
}

export default {
    namespace: "analysisInfo",
    state: { ...initState },
    reducers: {
        save(state, action) {
            return { ...state, ...action.payload }
        },
        clear(state,action){
            state.logList.splice(0);
            state.StepMeasure.splice(0);
            return {...action.payload}
        }
    },
    subscriptions: {
        setup({ history, dispatch }) {
            history.listen(({ pathname }) => {
                if (pathname === "/gather/qualityAnalysis/designSpace") {
                    dispatch({ type: "save",payload:{isReload:true} })
                }else{
                    dispatch({ type: "save",payload:{isReload:false} })
                }
            })
        }
    },
    effects: {
        *getExecuteStatus({ payload,status }, { call, put, select }) {
            const { owner } = yield select(state=> state.transheader);
            const { logList,isReload } = yield select(state => state.analysisInfo);
            const { name,view } = yield select(state => state.designSpace);
            if(name && payload.name !== name) return;
            const { data } = yield call(getTrans_exec_id, { ...payload , owner});
            const { code } = data;
            const idData = data;
            if (code === "200") {
                const { executionId } = idData.data;
                if(!executionId){
                    yield put({ type:"save",payload:{ ...initState } });
                    yield put({ 
                        type:"designSpace/save",
                        payload:{ status:"Waiting" }
                    });
                    return ;
                }
                const { data } = yield call(getTransExecInfo, { executionId,logList , owner });
                if (code === "200") {
                    const { ExecLog, StepMeasure, StepStatus } = data.data;
                    const { log } = ExecLog;
                    if (log && log.trim()) {
                        logList.push({
                            key: jsPlumbUtil.uuid(),
                            log: decodeURIComponent(log.trim().replace(/[\r\n]/g, ''))
                        })
                    };
                    yield put({
                        type: "save",
                        payload: {logList, showInfo:true,StepMeasure:StepMeasure.map((index,key)=>{
                            index.key=key;
                            return index;
                        })}
                    });

                    //延时1000s
                    if(runStatus.has(status) && isReload && view){
                        yield call(Timer,1000);
                        yield put({ type:"getStatus",payload } );
                        yield put({ 
                            type:"designSpace/updateStepStatus",
                            payload:{ style:"dragNormal", StepStatus,status }
                        })
                    }else if(stopStatus.has(status)){
                        yield put({ 
                            type:"designSpace/updateStepStatus",
                            payload:{ style:"stopStyle", StepStatus,status }
                        })
                    }else if(errorStatus.has(status)){
                        yield put({ 
                            type:"designSpace/updateStepStatus",
                            payload:{ style:"errorStyle",StepStatus,status }
                        })
                    }else if(pauseStatus.has(status)){
                        yield put({ 
                            type:"designSpace/save",
                            payload:{ status }
                        })
                    }else{
                        yield put({ 
                            type:"designSpace/updateStepStatus",
                            payload:{ style:"success",StepStatus,status }
                        })
                    }
                }else{
                    yield put({ type: "clear", payload: { ...initState,...payload } });
                }
            }
        },
        *getStatus({ payload }, { put,select,call }){
            const {owner} = yield select(state=>state.transheader);
            const { data } = yield call(getTrans_status, { ...payload ,owner });
            const { code } = data;
            if(code === "200"){
                const { status } = data.data;
                yield put({ type:"getExecuteStatus", payload,status });
            }
        },
        *initInfo({ payload }, { put }) {
            yield put({ type: "clear", payload: { ...initState,...payload } });
            yield put({ type: "getStatus", payload });
        }
    }
}