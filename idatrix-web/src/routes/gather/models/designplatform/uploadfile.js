/*
  文件上传
*/

export default {
  namespace: 'uploadfile',
  state: {
    visible:false,
    value:false,
    model:"",
    fileList:[],
    filterType:"",
    title:"文件上传",

    //适配数据质量
    action:"cloudelt"
  },
  reducers: {
    showModal(state,action){
      return {
        ...state,
        ...action.payload
      };
    },
    hideModal(state,action){
      return {
        ...state,
        ...action.payload,
        value:false,
        model:"",
        fileList:[],
        filterType:"",
        title:"文件上传"
      };
    }
  }
};

