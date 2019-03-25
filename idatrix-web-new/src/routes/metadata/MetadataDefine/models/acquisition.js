import {
  getDataOriginTable,
  WenJianSearch
} from "../../../../services/metadata";
import { convertArrayToTree } from "../../../../utils/utils";
import { getDepartments } from "services/metadataCommon";
import { GetTableDetailsByTableName,SaveTableAndFields } from "../../../../services/AcquisitionCommon";
import immutable from "immutable"
import { message } from 'antd';

const state= {
  options: [],
  optionsKey: "",
  data: [],
  data1: [],
  data2: [],
  loading: false,
  selectedRows: [],
  selectedRowKeys: [],
  tableNames: [],
  tablesNamelist: [],
  selectRowLeft: [],
  selectRowRight: [],
  tableNameX: [],
  selectRowKeysLeft: [],
  selectRowKeysRight: [],
  dsTypes: 3,
  valueGrade: 1,
  /*value:1,*/
  indexSelect: "",
  visible: false,
  info: "",
  hostname: "",
  port: "",
  username: "",
  password: "",
  databaseName: "",
  pluginId: "",
  dsName: "",
  key: "",
  dataKey: [],
  dataNameBest: "",
  modelMet: "",
  selectedRowKeysList: [],
  selectedRowsList: [],
  selectedRowKeysListName: [],
  selectedRowsListname: [],
  datalist: [],
  dataBase: [],
  dataBasename: "",
  info: {},
  metaid: "",
  FieldList: [],
  dsIdData: "",
  caijiList: [],
  keyList: {},
  schemaList: [],
  expandedTables: {},
  selectKey: [],
  selectedRowKeys: []
};
const initialData = immutable.fromJS(state);

export default {
  namespace: "acquisition",
  state: initialData,
  reducers: {
    closeModel(state, action) {
      console.log(state, action, "state,closeModel");
      return initialData;
    },

    setMetaId(state, action) {
      return state.merge(action.payload);
    },

    setMetaIdUpdata(state, action) {
      return state.merge({
        ...action.payload,
        modelMet: action.payload.modelMet
      });
    },

    showModel(state, action) {
      console.log(state, action, "state,showModel");
      return state.merge({
        ...action.payload,
        visible: true
      });
    },
    hideVisbis(state, action) {
      return state.merge({
        ...action.payload,
        model: action.model
      });
    },
    hideModel(state, action) {
      return initialData;
    },
    setExpandedTables(state,action){
      return state.merge({
        expandedTables: {
          ...state.get("expandedTables"),
          ...action.payload
        }
      })
    }
  },
  effects: {
    *GetTableDetails({ payload }, { put, call }) {
      const data = yield call(GetTableDetailsByTableName, payload);
      if (data && data.data && data.data.code === "200") {
        yield put({
          type: "setExpandedTables",
          payload: {
            [payload.tableName]: data.data.data
          }
        });
      }
    },

    *start({payload},{put,call}){
      const data = yield call(SaveTableAndFields,{...payload});
      if(data.data.code === "200"){
        yield put({type: "metaDataDefine/getList", payload:{page:1,pageSize: 10}})
        yield put({type: "metaDataDefine/hideAllViewAcquisition" })
        message.info("采集成功，完成："+ data.data.data.success + "，共计" + data.data.data.total + "。");
      }else{
        yield put({type: "metaDataDefine/hideAllViewAcquisition" })
      }
    }
  }
};
