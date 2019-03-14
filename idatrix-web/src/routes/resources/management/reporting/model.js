import { convertArrayToTree } from 'utils/utils';
import { MLZYgetAllDateUploadRecords,getBrowseFormDataTitle } from  'services/DirectoryOverview';
export default {
  namespace: 'reportingModel',
  state:{
    options:[],
    text:"",
    data:[],
    loading:false,
    visiblelog:false,
    visibleSuccess:false,  //在线上报成功弹出框
    pagination: {
      current:1,
      pageSize:10
    },
    total:0,
    dataTable:[],
    selectedKeys:"",
    model:"",
    dataList:[],
    dataTitel:[],
    viewFields: [], // 表字段
    status: false,
   
    dataTableSubmit:[],
    RadioCokie:"Online",
    dataTitleSuo:[],
    successData:""
  },
  reducers: {
    setMetaId(state,action){
       return {
        ...state,
        ...action.payload
      };
    },
    showModel(state,action){
      return {
        ...state,
        ...action.payload,
        visiblelog:true
     
      };
    },
    hideModel(state,action){
      return {
         ...state,
        ...action.payload,
        visiblelog:false,
      };
    },

  },

  subscriptions:{
      setup({history,dispatch}){
        return history.listen(({ pathname,query })=>{
          if(pathname === "/resources/management/reporting"){
               dispatch({
                type:"getList",
                payload:{
                  ...query,
                  page:query.page?query.page:1,
                  pageSize:query.pageSize?query.pageSize:10
                }
              })
          }
        })
      }
  },

  effects:{
    *getList({payload},{call,select,put}){
        yield put({type:"save",payload:{loading:true}})
        const { data } = yield call(MLZYgetAllDateUploadRecords,{...payload});

        const { code } = data;

        if(code === "200"){
           yield put({
              type:'setMetaId',
              payload:{
                dataList:data.data.results,
                pagination: {
                  current:1,
                  pageSize:10
                },
                total:data.data.total,
                loading:false
              }
           })
        }
      },
      *getBrowseFormDataTitle({payload},{call,select,put}){
        yield put({type:"save",payload:{loading:true}})
        const { data } = yield call(getBrowseFormDataTitle,{...payload});
       
        const { code } = data;
 
        if(code === "200"){
          let arge=[];
          let list1=[];
          for(let index of data.data){
            arge.push({
              "title":index,
             // "key":index,
              "dataIndex":index,
              "editable": true,
            })
          }
          list1.push({
            parent:1,
            data:[]
         })
       
           yield put({
              type:'setMetaId',
              payload:{
                dataTitel:arge,
                dataTable:list1,
                loading:false
              }
           })
        }
      },
  }
};
