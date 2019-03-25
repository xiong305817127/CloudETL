/*日志打印模块*/
export default {
  namespace: 'infostep',
  state: {
    executionId:"",
    stepMeasure:[],
  },
  reducers: {
    "printStep"(state,action){
      if(action.stepMeasure && action.stepMeasure.length>0){
        return {
          ...state,
          executionId:action.executionId,
          stepMeasure:action.stepMeasure
        }
      }else {
         return state;
      }

    },
    cleanStep(state,action){
        state.stepMeasure.splice(0);
        return {
          executionId:"",
          stepMeasure:[]
        }
    }
  }
};


