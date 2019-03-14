import {get_db_schema,get_db_table,get_db_table_fields } from  '../../../services/gather';

/*数据库链接*/
export default {
  namespace: 'dbtable',
  state: {
    tableList:[],
    tableFields:[],
    selectKeys:[],
    connection:"",
    schemalist:[],
    schema:"",
    tablename:"",
    visible:false,
    loading:false,

    loading1:false,

    dataType:"",
    fuc:{}
  },
  reducers: {
    'show'(state,action){
      return {
        ...state,
        ...action.payload
      };
    },
    'showLoading'(state,action){
        return {
          ...state,
          loading:true
        };
    },
    'hide'(state,action){
      return {
        tableList:[],
        tableFields:[],
        selectKeys:[],
        schemalist:[],
        connection:"",
        schema:"",
        tablename:"",
        visible:false,
        loading:false,
        dataType:"",
        fuc:{}
      };
    }
  },
  effects: {
    //job列表
    *querySchema({payload}, {put}) {
      const { data } = yield get_db_schema(payload.connection);
      const {code} = data;
      if(code === "200"){
        let str = "";
        const { schema,schemalist } = data.data;
        if(schemalist && schemalist.length>0){
          if(schemalist.indexOf(schema)>0){
            str = schema
          }else{
            str =  schemalist[0]
          }
        }else{
          str = schema
        };
        yield put({
          type: 'show',
          payload:{
            ...payload,
            schema:str,
            schemalist:schemalist
          }
        });
        yield put({
          type: 'queryTable',
          payload:{
            ...payload,
            schema:str,
            schemalist:schemalist
          }
        });
      }
    },
    *queryTable({payload}, {select, call, put}) {
      yield put({ type: 'show',payload:{ loading1:true } });
      const { data } = yield get_db_table(payload);
      const { code } = data;
      if(code === "200"){
        console.log(data.data.tables);

        data.data.tables.sort(function (a,b) {
          console.log(a.table);
          console.log(b.table);

            let str1 = a.table.toLowerCase();
            let str2 = b.table.toLowerCase();

            console.log(str1);
            console.log(str2);

            return str2 > str1?-1:(str2==str1?0:1);
        });

          yield put({
            type: 'show',
            payload:{
              ...payload,
              loading1:false,
              tableList:data.data.tables,
              dataType:data.data.type,
              tableFields:[]
            }
          });
      }
    },
    *queryTableFields({payload}, {select, call, put}) {
      yield put({ type: 'showLoading' });
      const { data } = yield get_db_table_fields({...payload,quote:true});
      const  { code } = data;
      if(code === "200"){
          let fields = [];
          for(let index of  data.data.fields){
            fields.push({
              key:index.name,
              name:index.name
            });
          };
          yield put({
            type: 'show',
            payload:{
              ...payload,
              tablename:data.data.table,
              tableFields:fields,
              loading:false
            }
          });
      }
    }
  }
};

