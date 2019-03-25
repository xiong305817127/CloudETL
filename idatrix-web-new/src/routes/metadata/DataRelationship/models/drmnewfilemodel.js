// 完成 UI 后，现在开始处理数据和逻辑。
// dva 通过 model 的概念把一个领域的模型管理起来，包含同步更新 state 的 reducers，处理异步逻辑的 effects，订阅数据源的 subscriptions 。
// 新建 model models/products.js ：
// 这个 model 里：
// namespace 表示在全局 state 上的 key
// state 是初始值，在这里是空数组
// reducers 等同于 redux 里的 reducer，接收 action，同步更新 state
// 然后别忘记在 index.js 里载入他：

import {SJGXGLbatchUpdateTableAndField,SJGXGLqueryTableAndFiledById,GetBiaoziduan,SJGXGLjianli,SJGXGByTwoId ,SJGXGLisExists,SJGXGtableAndFiledRelation} from  '../../../../services/metadata';
import { convertArrayToTree } from '../../../../utils/utils';
import { message} from 'antd';
export default {
  namespace: 'drmnewfilemodel',
  state:{
    updataTable:{},
    statusList:[],
    actionType:"",
    rsType:"1",
    fileTable:false,
    visible:false,
    hostType:"",
    dataLeft:[],
    leftName:"",
    selectLeft:{},
    status:"1",
    rightName:"",
    dataRight:[],
    selectRight:{},
    metaid:"",
    loadingLeft:false,
    loadingRight:false,
    selectedIds:[],
    rightmetaid:"",
    leftmetaid:"",
    relationId:"",
    data:[],
    loading:false,
    data1:[],
    selectTable:[],
    value:'',
    metadataBase:[],
    dsId:'',
    metadataFilst:[],
    metadataId:"",
    selectedRowKeysLeft:[],
    selectedRowKeysRight:[],
  },
  reducers: {
    hideModel(state,action){
      return {
        ...state,
        updataTable:{},
        statusList:[],
        actionType:"",
        rsType:"1",
        fileTable:false,
        visible:false,
        hostType:"",
        dataLeft:[],
        leftName:"",
        selectLeft:{},
        status:"1",
        rightName:"",
        dataRight:[],
        selectRight:{},
        metaid:"",
        loadingLeft:false,
        loadingRight:false,
        selectedIds:[],
        rightmetaid:"",
        leftmetaid:"",
        relationId:"",
        data:[],
        loading:false,
        data1:[],
        selectTable:[],
        value:'',
        metadataBase:[],
        dsId:'',
        metadataFilst:[],
        metadataId:"",
        selectedRowKeysLeft:[],
        selectedRowKeysRight:[],
      };
    },
    changeModel(state,action){
      console.log( ...action.payload," ...action.payload");
        return {
          ...state,
          ...action.payload
        }
    }
  },

  effects: {
    *queryTableAndFiledById({payload}, {select, call, put}) {
      let data = null;
      if(payload.hasData){
        data = payload.data
      }else{
        data = yield SJGXGLqueryTableAndFiledById(payload.id);
      }
   
      const { msg } = data.data.code;
      const relationId = data.data && data.data.data && data.data.data.dataFieldRelation && data.data.data.dataFieldRelation.length && data.data.data.dataFieldRelation[0].relationId || null;
      console.log(payload,"queryTableAndFiledById.payload",data,"relationId",relationId);
      if(data.data.code === "200" ){
        if(data.data.data.dataRelation[0].tableType === 2){
            yield put({
              type: 'changeModel',
              payload:{
                visible:true,
                updataTable:payload.updataTable,
                dataRight:[{
                  relationId,
                  key:data.data.data.dataRelation[0].childId,
                  dirName:data.data.data.dataRelation[0].childTable,
                  metaid:data.data.data.dataRelation[0].metaid,
                }],
                rsType:data.data.data.dataRelation[0].rsType+"",
                actionType:payload.actionType,
                data:[],
                fileTable:true
              }
            });
        }else{
          let args = [];
          for(let index of data.data.data.dataFieldRelation){
            console.log(index,"index");
            args.push({
              ...index,
              key:index.fcolName+index.fmetaid+index.scolName+index.smetaid
            })
          }
          yield put({
            type: 'changeModel',
            payload:{
              visible:true,
               relationId,
              updataTable:payload.updataTable,
              rsType:data.data.data.dataRelation[0].rsType+"",
              actionType:payload.actionType,
              data:args,
              fileTable:false
            }
          });
          if(data.data.data.dataRelation[0].childId){
            yield put({
              type: 'getTableFields',
              payload:{
                tableType:2,
                metaNameCn:data.data.data.dataRelation[0].childTable,
                id:data.data.data.dataRelation[0].childId
              }
            });
            }
        }
        if(data.data.data.dataRelation[0].metaid){
          yield put({
            type: 'getTableFields',
            payload:{
              tableType:1,
              metaNameCn:data.data.data.dataRelation[0].tableName,
              id:data.data.data.dataRelation[0].metaid
            }
          });
        }
      }
    },
    *TableAndFiledById({payload}, {select, call, put}) {
      let data = null;
      if(payload.hasData){
        data = payload.data
      }else{
        data = yield SJGXGtableAndFiledRelation(payload.id);
      }
      console.log(payload,"TableAndFiledById.payload");
      const { msg } = data.data.code;

      if(data.data.code === "200" ){
        if(data.data.data.dataRelation[0].tableType === 2){
            yield put({
              type: 'changeModel',
              payload:{
                visible:true,
                dataRight:[{
                  key:data.data.data.dataRelation[0].childId,
                  dirName:data.data.data.dataRelation[0].childTable,
                  metaid:data.data.data.dataRelation[0].metaid,
                }],
                rsType:data.data.data.dataRelation[0].rsType+"",
                actionType:payload.actionType,
                data:[],
                fileTable:true
              }
            });
        }else{
          let args = [];
          for(let index of data.data.data.dataFieldRelation){
            console.log(index,"index");
            args.push({
              ...index,
              key:index.fcolName+index.fmetaid+index.scolName+index.smetaid
            })
          }
          yield put({
            type: 'changeModel',
            payload:{
              visible:true,
              rsType:data.data.data.dataRelation[0].rsType+"",
              actionType:payload.actionType,
              data:args,
              fileTable:false
            }
          });
          if(data.data.data.dataRelation[0].childId){
            yield put({
              type: 'getTableFields',
              payload:{
                tableType:2,
                metaNameCn:data.data.data.dataRelation[0].childTable,
                id:data.data.data.dataRelation[0].childId
              }
            });
            }
        }
        if(data.data.data.dataRelation[0].metaid){
          yield put({
            type: 'getTableFields',
            payload:{
              tableType:1,
              metaNameCn:data.data.data.dataRelation[0].tableName,
              id:data.data.data.dataRelation[0].metaid
            }
          });
        }
      }
    },

    *getTableFields({payload}, {select, call, put}){
       if((payload.tableType === 2) || (payload.actionType === "right")){
        yield put({
          type: 'changeModel',
          payload:{
            loadingRight:true
          }
        });
      }else{
        yield put({
          type: 'changeModel',
          payload:{
            loadingLeft:true
          }
        });
      }
      console.log(payload,"getTableFields.payload");
      const {data} = yield GetBiaoziduan(payload.id);
      const  { code } = data;
      if(code === "200"){

        const  rows = data.data;
        rows.map( (row, index) => {
          row.key = row.id;
          return row;
        });
        if((payload.tableType === 2) || (payload.actionType === "right")){
            yield put({
              type: 'changeModel',
              payload:{
                loadingRight:false,
                dataRight:rows,
                 rightmetaid:payload.id,
                rightName:payload.metaNameCn,
              }
            });
        }else{
          yield put({
            type: 'changeModel',
            payload:{
              loadingLeft:false,
              dataLeft:rows,
               leftmetaid:payload.id,
              leftName:payload.metaNameCn,
            }
          });
        }


      }
    },
     //新建
    *saveFields({payload}, {select, call, put}){
      const {data} = yield SJGXGLjianli(payload.obj);
      const { code } = data;
       yield put({
          type: 'changeModel',
          payload:{
             updataTable:payload.Request
          }
        });
      console.log(payload,"saveFields.payload");
      if(code === "200"){
        yield put({
          type: 'hideModel',
          payload:{
            status:"1",
            ...payload
          }
        });
        payload.Request();
      }
    },
    //编辑
    *savebiuanjiFields({payload}, {select, call, put}){

      console.log(payload,"22222222222222222222222222222222222222");
         /* yield put({
              type: 'queryTableAndFiledById',
              payload:{
                visible:true,
                  relationId:payload.obj.dataFieldRelation[0].relationId,
              }
            });*/
     /*  const itme= payload.obj.data;
        itme.forEach(it => it.status?2:3);
         itme.forEach(it => it.relationId = payload.obj.relationId);
          console.log(itme,"itme",payload,"payload");
      */
      

      const {data} = yield SJGXGLbatchUpdateTableAndField(payload.obj);
      const { code } = data;
      if(code === "200"){
        console.log(payload,"savebiuanjiFields.payload1111");
       const newData = data || []; yield put({
          type: 'hideModel',
          payload:{
            ...payload,
            status:2,
          }
        });
        payload.Request();
      }
    },
    *SJGXGLisExists({payload}, {select, call, put}) {

      const drmnewfilemodel = yield select(state=>state.drmnewfilemodel);

      console.log(payload,"SJGXGLisExists.payload");

      const {leftmetaid,rightmetaid,data } = drmnewfilemodel;

      if((leftmetaid && payload.id) || (rightmetaid && payload.id)){
         let metaid = leftmetaid;
          let childId = rightmetaid;
          let rsType = rsType;
          let tableType = tableType;
        if(payload.actionType === "left"){
            metaid = payload.id;
            childId = rightmetaid;
            rsType = drmnewfilemodel.rsType;
            tableType = payload.tableType;
        }else{
            metaid = leftmetaid;
            childId = payload.id;
            rsType = drmnewfilemodel.rsType;
            tableType = payload.tableType;
        }
        const {data} = yield SJGXGLisExists({metaid:metaid,childId:childId,rsType:rsType,tableType:tableType});
          yield put({
               type:"SJGXGByTwoId",
                payload:{
                ...payload
              }
            })
        /* const dataRelation =data.dataRelation[0];*/
       /* const {code} = data;*/
        const code = data && data.code;
         console.log(code,"data");
        const dataRelation = data && data.data ? data.data.dataRelation : data.dataRelation;
        console.log(data, code,dataRelation,"dataRelation");
        console.log(data.data,"data.data === 'false'");
        if(code === "200" && dataRelation){
            yield put({
               type:"queryTableAndFiledById",
               payload:{
                data:data.data ? data.data : data,
                id:data.data.dataRelation[0].id
               }
            });

        }else{
            yield put({
               type:"getTableFields",
                payload:{
                  data:data,
                ...payload
              }
            })
        }
      }else{
          yield put({
             type:"getTableFields",
             payload:{
              data:data,
                ...payload
             }
          })
      }
    },

    *SJGXGByTwoId({payload}, {select, call, put}) {

      const drmnewfilemodel = yield select(state=>state.drmnewfilemodel);
      const {leftmetaid,rightmetaid,data } = drmnewfilemodel;
      if((leftmetaid && payload.id) || (rightmetaid && payload.id)){
         let metaid = leftmetaid;
          let childId = rightmetaid;
        if(payload.actionType === "left"){
            metaid = payload.id;
            childId = rightmetaid;
        }else{
            metaid = leftmetaid;
            childId = payload.id;
        }

        const {data} = yield SJGXGByTwoId({metaid:metaid,childId:childId});

        /* const dataRelation =data.dataRelation[0];*/
       /* const {code} = data;*/
        const code = data && data.code;
         console.log(code,data,"data");
        const dataRelation = data && data.data ? data.data.dataRelation : data.dataRelation;
        console.log(payload,"SJGXGByTwoId.payload");
        if(code === "200"){
          const newData = data.data ? data.data : data;
          console.log(newData,"newData1111");
          if (!newData.dataRelation) {
            console.log(newData,"newData");
            newData.dataRelation = [];
            yield put({
              type: 'changeModel',
              payload: {
                data: [],
                id: null,
              }
            })
          } else {
            yield put({
               type:"queryTableAndFiledById",
               payload:{
                data:newData,
                id: newData.dataRelation.length ? newData.dataRelation[0].id : null,
               }
            })
            //data.dat
          }
            // a.dataRelation = [];
        }else{
            yield put({
               type:"getTableFields",
                payload:{
                  data:data,
                ...payload
              }
            })
        }
      }else{
          yield put({
             type:"getTableFields",
             payload:{
              data:data,
                ...payload
             }
          })
      }
    },

    /* *SJGXGByTwoId({payload}, {select, call, put}) {

     }
*/

  }
};
