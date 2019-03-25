/*
  文件上传
*/

export default {
  namespace: 'worktools',
  state: {
    model1:"",
    model2:""
  },
  reducers: {
    changeModel(state,action){
        return {
          ...state,
          ...action.payload
        }
    }
  }
};

