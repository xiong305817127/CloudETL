export default {
  namespace: 'newstoragetable',
  state: {
    visible:false,
    model:"",
    info:{},
    metaid:"",
     viewFields: [], // 表字段
     frequency:"",
  },
  reducers: {
    "show"(state,action){
      return {
        ...state,
        visible:action.visible,
        model:action.model,
        info:action.info,
        metaNameEn:action.metaNameEn,
        metaNameCn:action.metaNameCn,
        metaid:action.metaid,
        frequency:action.frequency,
        serverName:action.serverName,
        dbDatabasename:action.dbDatabasename,
        dsId:action.dsId,
        viewFields:action.data2,
      };
    },
    "showTabel"(state,action){
      return {
        ...state,
        visible:action.visible,
        model:action.model,
        info:action.info,
        metaNameEn:action.metaNameEn,
        metaNameCn:action.metaNameCn,
        metaid:action.metaid,
        frequency:action.frequency,
        serverName:action.serverName,
        dbDatabasename:action.dbDatabasename,
        dsId:action.dsId,
        viewFields:action.data2,
      };
    },
    "hide"(state,action){
      return {
        ...state,
        visible:action.visible,
         model:"",
        info:"",
        metaNameEn:"",
        metaNameCn:"",
        metaid:"",
        frequency:"",
        serverName:"",
        dbDatabasename:"",
        dsId:"",
        viewFields:[],
      };
    },
    "model"(state,action){
        return{
           ...state,
            metaNameEn:action.metaNameEn,
            metaNameCn:action.metaNameCn,
            metaid:action.metaid,
            frequency:action.frequency,
            serverName:action.serverName,
             viewFields:action.data2,
             model:action.model,
        };
    },
     "nameModel"(state,action){
        return{
           ...state,
           ...action,
        };
    },
     // 添加字段
  /*  "addField"(state, action) {
      const { viewFields } = state;
      const newField = deepCopy(immutableField);
      const dsType = state.view.dsType;
      newField.dataType = dsType == dbTypeValue['hbase'] ? 'varchar' : 'int';
      newField.length = getDefaultLength(newField.dataType);
      viewFields.push({ ...newField, key: createGUID(), status: modifyStatus['new'] });
      return { ...state, viewFields };
    },*/
  },
};
