import {
  getSubtreeAndDepth,
  getFieldList,
  getResource,
  getTableList,
  saveInfo,
  getProcess,
  getDatabaseListByRentId
} from "services/catalog";
import { getSchemasByDsId } from "services/metadataCommon";
import {
  getResourceShareDict,
  getResourceTypeDict
} from "services/DirectoryOverview";
import { message } from "antd";
import _ from "lodash";
import { hashHistory } from "react-router";

//需要屏蔽的字段
const unUseField = ["ds_sync_flag", "ds_sync_time", "ds_batch"];

const getType = num => {
  switch (num) {
    case 3:
      return "database";
    case 6:
      return "info";
    case 7:
      return "service";
    default:
      return "other";
  }
};

// 2019年3月20日修改的类型
const typeToCode = {
	mysql: 1,
	oracle: 2,
	dm: 3,
	postgresql: 4,
	hive: 5,
	hbase: 6,
	hdfs: 7,
	elasticsearch: 8
};
const arrayToString = args => {
  return args.map(index => index + "");
};

const immutableState = {
  //自定义格式描述 info  服务名 service  数据库 database
  controlVisible: "other",
  //1 无条件 2 有条件 3 不予共享
  shareType: "1",
  //资源代码前缀
  catalogCode: "",
  //保存提供方代码
  deptCode: "",
  //保存提供方名字
  deptName: "",
  //数据库表名级联
  bindTables: [],
  //编辑的参数配置
  config: {},
  //更新类型
  updataType: "field",
  //字段列表
  fieldsList: [],
  //表格数据
  dataSource: [],
  //是否为验证视图
  checkview: false,
  //验证表单的值是否发生改变
  formChange: false,

  //是否向社会开放
  open: false,

  //是否需要更新
  shoudleUpdate: false,

  //资源目录
  resourcesList: [],
  //资源格式名称
  ResourceFormat: [],
  //共享方式
  ResourceDict: []
};

export default {
  namespace: "sourceEditView",
  state: { ..._.cloneDeep(immutableState) },
  reducers: {
    // 合并状态
    save(state, action) {
      return { ...state, ...action.payload };
    },
    // 清理状态
    clear() {
      return {
        ..._.cloneDeep(immutableState)
      };
    }
  },
  effects: {
    /**
     *  逐级查询资源目录
     *  根据传入的id,逐级加载数据
     */
    *getResourcesFolder(
      { payload, resolve, targetOption, resourcesList },
      { put, call, select }
    ) {
      const { data } = yield call(getSubtreeAndDepth, { ...payload });
      const { code } = data;
      if (code === "200") {
        if (resourcesList && resourcesList.length > 0) {
          targetOption.loading = false;
          targetOption.children = data.data.map(index => {
            index.value = index.id;
            index.code = index.resourceEncode;
            index.label = index.resourceName;
            index.isLeaf = !index.hasChildFlag;
            index.children = null;
            return index;
          });
          resolve();
          yield put({ type: "save", payload: { resourcesList } });
        } else {
          const sourceEditView = yield select(state => state.sourceEditView);
          for (let index of data.data) {
            index.value = index.id;
            index.code = index.resourceEncode;
            index.label = index.resourceName;
            index.children = null;
            index.isLeaf = !index.hasChildFlag;
            sourceEditView.resourcesList.push(index);
          }
          yield put({
            type: "save",
            payload: { resourcesList: sourceEditView.resourcesList }
          });
        }
      }
    },
    *getDatabase({ payload, force }, { call, select, put }) {
      const { account, sourceEditView } = yield select(state => state);
      if (sourceEditView.bindTables.length > 0 && !force) return;

      let arrType = typeToCode[payload.dsType.toLowerCase()];
      const { data } = yield call(getDatabaseListByRentId, {
        dsType: arrType,
        rentId: account.renterId
      });
      const { code } = data;

      if (code === "200") {
        let args = [];
        let unqieArgs = [];
        let i = 0;
        for (let index of data.data) {
          if (!unqieArgs.includes(index.name)) {
            unqieArgs.push(index.name);
            args.push({
              value: index.name,
              label: index.name,
              dsId: index.dsId,
              isLeaf: false
            });
          }
        }
        yield put({
          type: "save",
          payload: { bindTables: args, updataType: "database" }
        });
      }
    },
    *getTable({ payload }, { call, select, put }) {
      const { account } = yield select(state => state);
	  const { dsType } = payload;

	  const dbType = typeToCode[dsType.toLowerCase()];
      const { bindTableId } = payload;
      const dataDataBase = yield call(getDatabaseListByRentId, {
        dsType: dbType,
        rentId: account.renterId
      });
      //   const dataTables = yield call(getTableList, {
      //     name: payload.name,
      //     rentId: account.renterId
      //   });
      const { code } = dataDataBase.data;
      const oldData = dataDataBase.data;

      if (code === "200") {
        let args = [];
        let unqieArgs = [];
        let i = 0;
        for (let index of oldData.data) {
          if (!unqieArgs.includes(index.name)) {
            unqieArgs.push(index.name);
            args.push({
              value: index.name,
              label: index.name,
              isLeaf: false,
              dsId: index.dsId
            });
          }
        }

		// 不用获取此schema
		// 修改时间2019-03-22
        // const databaseIndex = args.findIndex(
        //   val => val.value === bindTableId[0]
        // );
        // const DsId = args[databaseIndex].dsId;

        // 获取到对应的schema
        // const dataSchema = yield call(getSchemasByDsId, DsId);
        // args[databaseIndex].children = dataSchema.data.data.map(val => ({
        //   value: val.name,
        //   valuelist: val.id,
        //   label: val.name,
        //   isLeaf: false
        // }));

        yield put({ type: "save", payload: { bindTables: args } });
      }
    },
    *getFields({ payload }, { call, select, put }) {
      const { account } = yield select(state => state);
      const { data } = yield call(getFieldList, {
        metaId: payload.tableName,
        userId: account.username
      });

      let fieldsList = [];
      const { code } = data;

      if (code === "200") {
        if (typeof data.data.fields !== "undefined") {
          for (let index of data.data.fields) {
            if (!unUseField.includes(index.fieldName.toLowerCase())) {
              fieldsList.push(index);
            }
          }
        }
        console.log(fieldsList);

        yield put({
          type: "save",
          payload: { fieldsList, updataType: "field" }
        });
      }
    },
    *getEditResource({ payload }, { call, select, put }) {
      const { data } = yield call(getResource, { ...payload });
      const { account } = yield select(state => state);
      const { code } = data;
      if (code === "200") {
        const config = data.data;
        let args = [];
        let dataSource = [];
        if (config.libTableId) {
          const tempIds = config.libTableId.split(",");
          tempIds.splice(2, 1, tempIds[2] ? parseInt(tempIds[2]) : "");
          config.bindTableId = tempIds;
          yield put({
            type: "getTable",
            payload: {
              dsType: config.formatInfo,
              bindTableId: tempIds,
              name: tempIds[0]
            }
          });
          yield put({
            type: "getFields",
            payload: { userId: account, tableName: tempIds[1] }
          });
        }
        config.deptNameIdArray = config.deptNameIdArray.split(",");
        let controlVisible = getType(config.formatType);
        config.formatType && args.push(config.formatType + "");
        config.formatInfo && args.push(config.formatInfo.toUpperCase());
        config.formatType = args;
        let catalogCode = config.catalogCode + "/";
        config.shareType = config.shareType + "";
        let open = config.openType && config.openType === 1 ? true : false;
        config.shareDeptArray = arrayToString(config.shareDeptArray);

        if (config.resourceColumnVOList) {
          let i = 0;
          for (let index of config.resourceColumnVOList) {
            dataSource.push({
              ...index,
              uniqueFlag: index.uniqueFlag ? index.uniqueFlag : "false",
              requiredFlag: index.requiredFlag ? index.requiredFlag : "false",
              key: i++
            });
          }
        }

        yield put({
          type: "save",
          payload: {
            config,
            updataType: "config",
            controlVisible,
            open,
            shareType: config.shareType, //保存提供方代码
            deptCode: config.deptCode,
            deptName: config.deptName,
            catalogCode,
            dataSource,
            shoudleUpdate: true
          }
        });
      }
    },
    *saveFormInfo({ payload, str }, { call, select, put }) {
      const { data } = yield call(saveInfo, { ...payload });
      const { code } = data;
      if (code === "200") {
        if (str && str === "save") {
          message.success("保存成功");
          yield put({
            type: "save",
            payload: {
              formChange: false,
              dataSource: payload.resourceColumnVOList
            }
          });
        } else {
          message.success("新增成功");
          yield put({ type: "clear" });
          hashHistory.push("/resources/management/mysource");
        }
      }
    },
    *getProcess({ payload }, { call, put }) {
      const { data } = yield call(getProcess, { ...payload });
      const { code } = data;
      if (code === "200") {
        message.success("审批完成！");
        yield put({ type: "clear" });
        hashHistory.push("/resources/register/approval");
      }
    },
    //获取资源格式
    *getResourceTypeDict({ payload }, { call, put }) {
      const { data } = yield call(getResourceTypeDict, { ...payload });
      const { code } = data;
      if (code === "200") {
        yield put({ type: "save", payload: { ResourceFormat: data.data } });
      }
    },
    //获取共享方式
    *getResourceShareDict({ payload }, { call, put }) {
      const { data } = yield call(getResourceShareDict, { ...payload });
      const { code } = data;
      if (code === "200") {
        yield put({ type: "save", payload: { ResourceDict: data.data } });
      }
    }
  }
};
