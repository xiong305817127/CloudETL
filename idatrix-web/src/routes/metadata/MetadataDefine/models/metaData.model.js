/**
 * 元数据定义 - 数据表类model
 */

import { DEFAULT_PAGE_SIZE } from 'constants';
import { deepCopy, dateFormat, createGUID } from 'utils/utils';
import { getDefaultLength,getDefaultPrecision } from 'utils/metadataTools';
import { getMetaTableList, getFieldsById,getMetaTableBaseInfoByMetaId,getVersionDetails } from 'services/metadataDefine';
import dbTypeValue from 'config/dbTypeValue.config';

const immutableState = {
  source: { // 基本列表
    list: [],
    total: 0,
  },
  view: { // 表详情
    optimize: {},
    sourceTable: [],
  },

  // 用于显示历史信息
  historyData: {},
  //是否直采
  direct:false,
  viewMode: 'read', // 查看详情模式  read, new, edit, acquis
  viewFields: [], // 表字段
  editorStep1Visible: false, // 编辑器第一步显示开关
  editorStep2Visible: false, // 编辑器第二步显示开关
  viewStep1Visible: false, // 查看窗口第一步显示开关
  viewStep2Visible: false, // 查看窗口第二步显示开关
  viewAcquisitionVisible: false, // 查看窗口第二步显示开关
};

// 字段模板
const immutableField = {
  "colName": "",
  "dataType": "",
  "description": "",
  "indexId": "",
  "indexType": "",
  "isDemension": "1",
  "isMetric": "1",
  "isNull": "1",
  "isPk": "0",
  "length": "",
  "standard": 0,
  "status": 0,
  "tmType": 0,
  "versionid": 0,
  "key": ""
};

// 字段编辑状态值
const modifyStatus = {
  'new': 1,
  'del': 2,
  'edit': 3,
   'acquis':4
};

export default {

  namespace: 'metaDataDefine',

  state: {
    ...deepCopy(immutableState),
  },

  effects: {

    // 通过metadataid来查询详细信息；
    *getMetaTableBaseInfoByMetaId({id},{put,call}){
      const data = yield call(getMetaTableBaseInfoByMetaId,id);
      yield put({type:"save",payload:{historyData: data.data.data}})
    },

    *getVersionDetails({metaid,version},{put,call}){
      const {data} = yield call(getVersionDetails,metaid,version);
      console.log(data)
      const viewFields = data && data.data || [];
      viewFields.forEach(row => {
          row.key = createGUID();
          row.dataType = row.dataType.toLowerCase();
      });
      console.log(viewFields,"转换后的数据");
      viewFields.id = metaid;
      yield put({ type: 'save', payload: { viewFields } });
    },
    // 查询数据表列表
    *getList({ payload }, { put, select }) {
      console.log("调用方法",payload);

      const { account } = yield select(state => state);
      const query = {
        page: payload.page || 1,
        rows: payload.pageSize || DEFAULT_PAGE_SIZE,
      };
      const formData = {
        dept: payload.dept,
        // dept: Array.isArray(payload.dept) ? payload.dept[payload.dept.length - 1] : payload.dept,
        keyword: payload.keyword,
        metaNameCn: '',
        // metaType: payload.metaType || 1,
        renterId: account.renterId,
      };
      const { data } = yield getMetaTableList(query, formData);
      const list = data && data.data && data.data.rows || [];
      const total = data && data.data && data.data.total || 0;
      yield put({ type: 'save', payload: { source: { list, total } } });
    },

    // 查询数据表字段
    *getFieldsById({ id, force=false }, { put, select }) {
      const { metaDataDefine } = yield select(state => state);
      // 防止回退时消除用户编辑的数据。下次打开编辑窗时(详见view)会自动刷新数据
      if (metaDataDefine.viewFields.id === id && !force) return;

      const query = { metaid: id };
      const { data } = yield getFieldsById(query);
      const viewFields = data && data.data || [];
      viewFields.forEach(row => {
          row.key = createGUID();
          row.dataType = row.dataType.toLowerCase();
      });
      console.log(viewFields,"转换后的数据");
      viewFields.id = id;
      yield put({ type: 'save', payload: { viewFields } });
    },

    *getFieldsByTableName({payload},{call}){
      const {} = yield call()
    }
  },

  subscriptions: {
    setup({ history, dispatch }) {
      return history.listen(({ pathname, query }) => {
        if (pathname === '/MetadataDefine' && (!query.model || query.model === 'table')) {

          console.log("调用");

          dispatch({ type: 'getList', payload: query });
        }
      });
    }
  },

  reducers: {
    // 查询表详情
    view(state, action) {
      const { id } = action;
      if (id) { // 修改
        const view = state.source.list.find(row => row.metaid === id);
        try {
          view.sourceTable = JSON.parse(view.sourceTable);
        } catch (err) {
          view.sourceTable = [];
        }
        return { ...state, view, viewFields: [] }; // 设viewFields为空是为了刷新数据
      } else { // 新增
        const view = deepCopy(immutableState.view);
        view.version = dateFormat(Date.parse(new Date()),"YYYYMMDDHHmmss")*100+Math.floor(Math.random()*100);
        return { ...state, view };
      }
    },
    // 修改表详情
    editView(state, action) {
      const { view } = state;
      return { ...state, view: { ...view, ...action.payload } };
    },
    // 添加字段
    addField(state, action) {
     
      const { viewFields } = state;
      const newField = deepCopy(immutableField);
      const dsType = state.view.dsType;
      newField.dataType = dsType == dbTypeValue['hbase'] ? 'varchar' : 'int';
      newField.length = getDefaultLength(newField.dataType);
      newField.precision = getDefaultPrecision(newField.dataType);
      if (dsType == dbTypeValue['hbase']) {
        newField.colFamily = '';
      }
      viewFields.push({ ...newField, key: createGUID(), status: modifyStatus.new });
      return { ...state, viewFields };
    },
    
    
    // 修改字段
    modifyField(state, action) {
      const { viewFields } = state;
      viewFields.forEach(row => {
        if (row.key === action.payload.key) {
          const newField = action.payload;
          if (newField.dataType !== row.dataType) { // 设置默认长度
            newField.length = getDefaultLength(newField.dataType);
            newField.precision = getDefaultPrecision(newField.dataType);
          }

          if ((newField.isPk == 1)) {
            newField.isNull = '0';
            if (typeof newField.colFamily !== 'undefined') {
              newField.colFamily = null;
            }
          }
          if (newField.status !== modifyStatus.new) { // 新增的记录不需要改变状态
            newField.status = modifyStatus.edit;
          }
          Object.assign(row, newField);
        }
      });

      console.log(viewFields,"更新后字段");
      return { ...state, viewFields };
    },
    // 删除字段
    delField(state, action) {
      const { viewFields } = state;
      const newViewFields = [];
      viewFields.forEach((row) => {
        console.log(row,"row=====================",action)
        let needDiscard = false;
        if (action.keys.indexOf(row.key) > -1) {
          if (row.status === modifyStatus.new) { // 如果是新增的记录，则直接抛弃
            needDiscard = true;
          } else {
            row.status = modifyStatus.del;
          }
        }
        if (!needDiscard) {
          newViewFields.push(row);
        }
      });
      return { ...state, viewFields: newViewFields };
    },
    // 从现有表导入字段
    importExisting(state, action) {
      const { viewFields } = state;

      // @add 如果字段为从excel中导入，则不进行初始化
      if(action.status && action.status === "import"){
        const oldFields = deepCopy(state.viewFields);
        const newFields = action.payload.map(index=>{
          return {
            ...index,
            status: modifyStatus.new,
            length: index.length ? index.length : getDefaultLength(index.dataType),
            precision: index.precision ? index.precision : getDefaultPrecision(index.dataType),
            key: createGUID()
          };
        });
        // 将原有的Fields导入

        console.log(oldFields,newFields);
        return { ...state, viewFields: typeof oldFields !== "undefined" ? [...oldFields,...newFields] : [...newFields]};
      }else{
        action.payload.forEach((field) => {
          const newField = deepCopy(immutableField);
          const dsType = state.view.dsType;
  
          // 判断采集的数据库类型是否与现在相等
          // 如果不相等则设置为空值
          if(dsType === field.dsType){
            newField.dataType = dsType == dbTypeValue['hbase'] ? 'varchar' : 'int';
            newField.length = getDefaultLength(field.dataType);

            // 修改精度
            // edited by steven on 2018/12/26
            newField.precision = getDefaultPrecision(field.dataType);
            if (dsType == dbTypeValue['hbase']) {
              newField.colFamily = '';
            }
  
            viewFields.push({ ...newField, ...field, key: createGUID(), status: modifyStatus.new });
          }else{
            const clearType = {
              colName: field.colName,
              description: field.description
            }
  
            viewFields.push({...newField,...clearType,key: createGUID(), status: modifyStatus.new })
          }
  
        });
      }

      return { ...state, viewFields };
    },
    // 显示编辑器
    showEditor(state, action) {
      const newState = {};
      if (action.step === 1) {
        newState.editorStep1Visible = true;
        newState.editorStep2Visible = false;
      } else if (action.step === 2) {
        newState.editorStep1Visible = false;
        newState.editorStep2Visible = true;
      }
      return { ...state, ...newState };
    },
    // 关闭所有编辑器
    hideAllEditor(state, action) {
      return { ...state, editorStep1Visible: false, editorStep2Visible: false };
    },
    // 显示查看窗口
    showView(state, action) {
      const newState = {};
      if (action.step === 1) {
        newState.viewStep1Visible = true;
        newState.viewStep2Visible = false;
      } else if (action.step === 2) {
        newState.viewStep1Visible = false;
        newState.viewStep2Visible = true;
      }
      return { ...state, ...newState };
    },
    // 关闭所有查看窗口
    hideAllView(state, action) {
      return { ...state, viewStep1Visible: false, viewStep2Visible: false };
    },
    save(state, action) {
      return { ...state, ...action.payload };
    },

     // 采集显示查看窗口
    showViewAcquisition(state, action) {
      const newState = {};
      console.log(state, action,"caijistate, action");
        if (action.step === 1) {
          newState.viewAcquisitionVisible = true;
        } else if (action.step === 2) {
          newState.viewAcquisitionVisible = false;
        }
    
      return { ...state, ...newState };
    },
    // 采集关闭所有查看窗口
    hideAllViewAcquisition(state, action) {
      return { ...state, viewAcquisitionVisible: false};
    },

  },

};
