import { getDataOriginTable,search_table_struct } from '../../../../services/metadata'
import { convertArrayToTree } from '../../../../utils/utils';
import {
  getDepartments,
  getAcquisition
} from 'services/metadataCommon';

export default {
  namespace: 'drmselecttable',
  state:{
    options:[],
    text:"",
    textBuMenAnNiu:"",
    tableIndex:"1",
    tabsIndex:"all",
    actionType:"",
    tableType:"",
    data:[],
    loading:false,
    metaid:"",
    searchIndex:"",
    keyword:"",
    pagination:{
      current:1,
      pageSize:10
    },
    dataTable:[],
  },
  reducers: {
    setMetaId(state,action){

      console.log({
        ...state,
        ...action.payload
      });

       return {
        ...state,
        ...action.payload
      };
    },
    showModel(state,action){
      
      return {
        ...state,
        ...action.payload,
        visible:true
     
      };
    },
    hideModel(state,action){
       action.selectKey = null;
       action.selectedRowKeys = null;

      return {
        ...state,
        visible:false,
        options:[],
        text:"",
        textBuMenAnNiu:"",
        tableIndex:"1",
        tabsIndex:"all",
        tableType:"",
        data:[],
        loading:false,
        selectKey:[],
        selectedRowKeys:[],
        searchIndex:"",
        pagination:{
          current:1,
          pageSize:10,
        }
      };
    }
  },
   effects: {
    *showSelectTable({payload}, {select, call, put}) {
      /*yield put({
        type:"showModel",
        payload:{
           loading:true
        }
      });*/
      const {renterId} = yield select(state=>state.account);
      const {data} = yield getDataOriginTable({...payload.obj,renterId},payload.paper);
      const code = data && data.code;
      console.log(data,"code");
      if(code === "200"){
          const { rows,total} = data.data;
          rows.map( (row, index) => {
            row.key = row.metaid;
            row.index = 10 * (1 - 1) + index + 1;
            return row;
          });
          let options=convertArrayToTree(data.data || '[]', 0, 'id', 'parentId', 'renterId', child => ({
            value: child.id,
            label: child.deptName,
          }));
          yield put({
            type:"setMetaId",
           /*type:"showModel",*/
            payload:{
              loading:false,
              data:rows,
              dataTable:rows,
               tableType:payload.obj.model=== 'right'?2:1,
               actionType:payload.obj.model=== 'right',
                options:options,
                pagination:{
                total:total
              }
            }
          })
      }
    },
     *showFileTable({payload}, {select, call, put}) {
      console.log(payload,"showFileTable");
       yield put({
         type:"setMetaId",
         payload:{
           loading:true,
           keyword:payload.obj.keyword
         }
       });
       const {data} = yield search_table_struct(payload.obj,payload.paper);
       const {code } = data;
       if(code === "200"){
         const { rows,total} = data.data;
         rows.map( (row) => {
           row.key = row.fileid;
           return row;
         });
         yield put({
           type:"setMetaId",
           payload:{
             loading:false,
             data:rows,
             pagination:{
               current:payload.paper.current,
               pageSize:payload.paper.pageSize,
               total:total
             }
           }
         })
       }
     },

    *getAcquisitionClick({payload}, {select, call, put}) {
      /* yield put({
         type:"setMetaId",
         payload:{
           loading:true,
           keyword:payload.obj.keyword
         }
       });*/
       const {data} = yield getAcquisition(payload.obj);
       const {code } = data;
       if(code === "200"){
         const { rows,total} = data.data;
         rows.map( (row) => {
           row.key = row.fileid;
           return row;
         });
         yield put({
           type:"setMetaId",
           payload:{
             loading:false,
             data:rows,
             pagination:{
               current:payload.paper.current,
               pageSize:payload.paper.pageSize,
               total:total
             }
           }
         })
       }
     },
     
  }
};
