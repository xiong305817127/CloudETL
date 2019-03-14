import { edit_step } from "services/quality";

const initState = {
    //是否显示
    visible:false,
    //组件id
    key:"",
    //组件类型
    panel:"",
    //组件内部参数
    config:{},
    //分析名称
    transname:{},
    //分析描述
    description:"",
    //上层级步骤
    prevStepNames:[],
    //下层级步骤
    nextStepNames:[]
}

export default{
    namespace:"domItems",
    state:{ ...initState },
    reducers:{
        save(state,action){
            return { ...state,...action.payload }
        },
        hide(){
            return { ...initState }
        }
    },
    effects:{
        *editStep({ payload,key },{ put,call }){
            const { data } =  yield call(edit_step,{transName:payload.transname,stepName:payload.stepname});
            const { code } = data;
            if(code === "200"){
                const { type, description, stepParams, nextStepNames, prevStepNames } = data.data;
                yield put({
                    type:"save",
                    payload:{
                        visible: true, panel: type,text:payload.stepname,
                        transname: payload.transname,config: stepParams,
                        description,nextStepNames,prevStepNames,key
                    }
                })
            } 
        }
    }
}
  
  