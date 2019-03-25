/*
  文件上传
*/

export default {
  namespace: 'uploadfile',
  state: {
    visible:false,

    model:"",
    fileList:[],
    filterType:"",
		title:"文件上传",

		//是否可控制覆盖
		disabled:false,
		value:false,
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

