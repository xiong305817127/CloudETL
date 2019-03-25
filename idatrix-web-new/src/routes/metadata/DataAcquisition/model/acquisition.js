import {
  getDataOriginTable,
  WenJianSearch
} from "../../../../services/metadata";
import { convertArrayToTree } from "../../../../utils/utils";
import { getDepartments } from "services/metadataCommon";

export default {
  namespace: "acquisition",
  state: {
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
    keyList: {}
  },
  reducers: {
    closeModel(state, action) {
      console.log(state, action, "state,closeModel");
      return {
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
        dsId: "",
        pluginId: "",
        key: "",
        dataKey: [],
        lengthss: "",
        type: "",
        dsIdData: "",
        infoDsId: "",
        oldTableKey: ""
      };
    },

    setMetaId(state, action) {
      console.log(state, action, "state,setMetaId");
      return {
        ...state,
        ...action.payload
      };
    },

    setMetaIdUpdata(state, action) {
      console.log(state, action, "state,setMetaId");
      return {
        ...state,
        ...action.payload,
        modelMet: action.payload.modelMet
      };
    },

    showModel(state, action) {
      console.log(state, action, "state,showModel");
      return {
        ...state,
        ...action.payload,
        visible: true
      };
    },
    hideVisbis(state, action) {
      return {
        ...state,
        ...action.payload,
        model: action.model
      };
    },
    hideModel(state, action) {
      action.selectKey = null;
      action.selectedRowKeys = null;

      selectKeyLeft = null;
      selectRowKeysLeft = null;

      selectKeyRight = null;
      selectRowKeysRight = null;
      return {
        ...state,
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
        key: "",
        dataKey: [],
        dsIdData: ""
      };
    }
  }
};
