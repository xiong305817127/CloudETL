import _ from "lodash";
import {
  getResource,
  getTableList,
  getProcess,
  getResourceFile,
  getDatabaseListByRentId,
  dataUpload
} from "services/catalog";
import { getResourceShareDict, getResourceTypeDict } from "services/DirectoryOverview";
import {
  getSchemasByDsId,
} from "services/metadataCommon";

import { message } from "antd";

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

const arrayToString = args => {
  return args.map(index => index + "");
};

const immutableState = {
  //不同意确定框
  rejectVisible: false,
  //自定义格式描述 info  服务名 service  数据库 database
  controlVisible: "other",
  //1 无条件 2 有条件 3 不予共享
  shareType: "1",
  //资源代码前缀
  catalogCode: "",
  //保存提供方代码
  bindTables: [],
  //编辑的参数配置
  config: {},
  //字段列表
  fieldsList: [],
  //表格数据
  dataSource: [],
  //是否弹出
  visible: false,
  //操作类型 see 查看  check 审批
  actionType: "see",
  //控制文件列表
  fileVisible: false,
  //文件數據
  fileSource: [],
  //總數
  total: 0,
  //加載
  loading: false,
  //搜素名
  name: "",
  current: 1,
	pageSize: 10,
	//资源格式名称
	ResourceFormat: []
};

export default {
  namespace: "checkview",

  state: { ..._.cloneDeep(immutableState) },

  reducers: {
    save(state, action) {
      return { ...state, ...action.payload };
    },
    clear() {
      return { ..._.cloneDeep(immutableState) };
    }
  },
  effects: {
    *getTable({ payload }, { call, select, put }) {
      const { account } = yield select(state => state);
      let arrType = payload.dsType === "MYSQL" ? 3 : "" || payload.dsType === "ORACLE" ? 2 : "" || payload.dsType === "DM" ? 14 : "";
      const { bindTableId } = payload;
      const dataDataBase = yield call(getDatabaseListByRentId, { dsType: arrType, rentId: account.renterId })
      const dataTables = yield call(getTableList, { name: payload.name, rentId: account.renterId });      
      const { code } = dataDataBase.data;
      const oldData = dataDataBase.data;
    
      if (code === "200") {
        let args = [];
        let unqieArgs = [];
        let i = 0;
        for (let index of oldData.data) {
          if (!unqieArgs.includes(index.name)) {
            unqieArgs.push(index.name);
            args.push({ value: index.name, label: index.name, isLeaf: false, dsId: index.dsId })
          }
        }

        const databaseIndex = args.findIndex(val => val.value === bindTableId[0]);
        const DsId = args[databaseIndex].dsId;
        // 获取到对应的schema
        const dataSchema = yield call(getSchemasByDsId, DsId);
        args[databaseIndex].children = dataSchema.data.data.map(val => ({
          value: val.name,
          valuelist: val.id,
          label: val.name,
          isLeaf: false
        }));

        const schemaIndex = args[databaseIndex].children.findIndex(val => val.value === bindTableId[1]);
				if(schemaIndex >= 0){
          args[databaseIndex].children[schemaIndex].children = dataTables.data.data.tables.map(val => (
            {
              value: val.id,
              label: val.tableName
            }
          ));
        }

        if (dataTables.data.code === "200") {
          yield put({ type: "save", payload: { bindTables: args } })
        }
      }
    },
    *getEditResource({ payload, actionType }, { call, put }) {
      const { data } = yield call(getResource, { ...payload });
      const { code } = data;
      if (code === "200") {
        const config = data.data;

        let args = [];
        if (config.libTableId) {
          yield put({
            type: "getTable",
            payload: {
              dsType: config.formatInfo,
              bindTableId: config.libTableId.split(","),
              name: config.libTableId.split(",")[0]
            }
          });
          config.bindTableId = config.libTableId.split(",")
          if (config.bindTableId[2]) {
            config.bindTableId[2] = parseInt(config.bindTableId[2])
          }

        }

        config.deptNameIdArray = config.deptNameIdArray.split(",");
        let controlVisible = getType(config.formatType);
        config.formatType && args.push(config.formatType);
        config.formatInfo && args.push(config.formatInfo.toUpperCase());
        config.formatType = args;
        let catalogCode = config.catalogCode + "/";
        let shareType = config.shareType + "";
        config.shareDeptArray = arrayToString(config.shareDeptArray);
        yield put({
          type: "save",
          payload: {
            statue: payload.statue,
            config,
            controlVisible, //保存提供方代码
            catalogCode,
            shareType,
            dataSource: config.resourceColumnVOList
              ? config.resourceColumnVOList
              : [],
            visible: true,
            actionType: actionType === "check" ? "check" : "see"
          }
        });
      }
    },
    *getProcess({ payload }, { call, put }) {
      const { data } = yield call(getProcess, { ...payload });
      const { code } = data;

      if (code === "200") {
        message.success("审批完成！");
        yield put({ type: "clear" });
        yield put({ type: "releaseApproval/getList" });
      }
    },
    *getResourceFile({ payload, action }, { call, select, put }) {
      const { checkview } = yield select(state => state);
      const { current, pageSize, name, config } = checkview;

      let page = payload.current ? payload.current : current;
      delete payload.current;
      yield put({ type: "save", payload: { loading: true } });
      const { data } = yield call(getResourceFile, {
        page,
        pageSize,
        name,
        id: config.id,
        ...payload
      });

      const { code } = data;

      if (code === "200") {
        if (action === "init" && !data.data) {
          message.info("暂无相关文件！");
        } else {
          yield put({
            type: "save",
            payload: {
              fileSource: data.data ? data.data.results : [],
              total: data.data ? data.data.total : 0,
              loading: false,
              fileVisible: true,
              current: page,
              pageSize
            }
          });
        }
      }
    },
    *getDataUpload({ payload }, { call }) {
      const { data } = yield call(dataUpload, { ...payload });
      const { code } = data;
      if (code === "200") {
        message.success("下载成功");
      }
		},
		//获取资源格式
		*getResourceTypeDict({ payload }, { call, put }) {
			const { data } = yield call(getResourceTypeDict, { ...payload });
			const { code } = data;
			if (code === "200") {
				yield put({ type: "save", payload: { ResourceFormat: data.data } })
			}
		}
  }
};
