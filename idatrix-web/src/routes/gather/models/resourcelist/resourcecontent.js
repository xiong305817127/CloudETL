/**
 * Created by Administrator on 2017/9/4.
 */
import {
  getServer_list,
  getDb_list2,
  getCluster_list,
  getDefaultEngineList,
  getHadoop_list,
  getSpark_list,
  test_DbConnection,
  test_Server,
  getFileList,
  edit_db_connection,
  deleteDb_list,
  geteditServer,
  getdeleteServer,
  getedit_cluster,
  getdelete_list,
  editEngine,
  deleteEngine,
  edit_hadoop,
  delete_hadoop,
  geteditCluster,
  getdeleteSpark,
  deleteFile,
  downloadFile,
  getJndiList,
  editJndi,
  deleteJndi,
  createJndi,
  getRoles,
  setRole,
  getCurrentRole
} from "../../../../services/gather";
import { message } from "antd";
let Timer = 5000;

const delay = timeout => {
  return new Promise(resolve => {
    setTimeout(resolve, timeout);
  });
};

const config = new Map([
  ["/gather/resourcelist/DataSystem", "queryDbList"],
  ["/gather/resourcelist/Server", "queryServerList"],
  ["/gather/resourcelist/Cluster", "queryClusterList"],
  ["/gather/resourcelist/ExecutionEngine", "queryDefaultEngine"],
  ["/gather/resourcelist/HadoopCluster", "queryHadoopList"],
  ["/gather/resourcelist/SparkEngine", "querySparkList"],
  ["/gather/resourcelist/FileSystem", "getFileTree"],
  ["/gather/resourcelist/auth", "getRoles"]
]);

const editConfig = new Map([
  [
    "DataSystem",
    {
      edit: edit_db_connection,
      delete: deleteDb_list,
      query: "#/gather/resourcelist/DataSystem"
    }
  ],
  [
    "Server",
    {
      edit: geteditServer,
      delete: getdeleteServer,
      query: "#/gather/resourcelist/Server"
    }
  ],
  [
    "Cluster",
    {
      edit: getedit_cluster,
      delete: getdelete_list,
      query: "#/gather/resourcelist/Cluster"
    }
  ],
  [
    "ExecutionEngine",
    {
      edit: editEngine,
      delete: deleteEngine,
      query: "#/gather/resourcelist/ExecutionEngine"
    }
  ],
  [
    "HadoopCluster",
    {
      edit: edit_hadoop,
      delete: delete_hadoop,
      query: "#/gather/resourcelist/HadoopCluster"
    }
  ],
  [
    "SparkEngine",
    {
      edit: geteditCluster,
      delete: getdeleteSpark,
      query: "#/gather/resourcelist/SparkEngine"
    }
  ]
]);

const dbList = new Map([
  ["GENERIC", "Generic database"],
  ["MSACCESS", "MS Access"],
  ["MYSQL", "MySQL"],
  ["MSSQL", "MS SQL Server"],
  ["ORACLE", "Oracle"],
  ["POSTGRESQL", "PostgreSQL"],
  ["SYBASE", "Sybase"],
  ["HIVE3", "Hadoop Hive2"],
  ["DB2", "IBM DB2"],
  ["HBASETABLE", "Phoenix Hbase Table"],
  ["MARIADB", "MariaDB"],
  ["GREENPLUM", "Greenplum"],
  ["DM7", "DM"]
]);

export default {
  namespace: "resourcecontent",
  state: {
    loading: false,
    view: "list",
    needUpdate: false,
    dataList: [],
    serverList: [],
    clusterList: [],
    hadoopList: [],
    sparkList: [],
    engineList: [],
    fileList: [],
    config: {},
    roles: [],
    currentRole: {},

    JNDIargs: [],
    JNDIconfig: {},
    visible: false,

    Timer: null
  },
  reducers: {
    changeStatus(state, action) {
      return { ...state, ...action.payload };
    },
    save(state, action) {
      return { ...state, ...action.payload };
    },
    showLoading(state, action) {
      return { ...state, loading: true, config: {}, view: "list" };
    }
  },
  subscriptions: {
    setup(obj) {
      const { history, dispatch } = obj;
      history.listen(location => {
        const { pathname, query } = location;

        if (pathname === "/gather/resourcelist/auth") {
          dispatch({
            type: "getCurrentRole"
          });
        }

        if (config.has(pathname)) {
          let obj = {};
          obj.page = query.page ? query.page : 1;
          obj.pageSize = query.pageSize ? query.pageSize : 8;
          obj.search = query.keyword ? query.keyword : "";
          if (pathname === "/gather/resourcelist/FileSystem") {
            obj.type = query.type ? query.type : "txt";
            obj.path = "";
          }

          dispatch({
            type: "showLoading"
          });

          delay(5000).then(_ => {
            dispatch({
              type: "changeStatus",
              payload: {
                loading: false
              }
            });
          });

          dispatch({
            type: config.get(pathname),
            payload: {
              ...obj
            }
          });
        }
      });
    }
  },
  effects: {
    //下载文件
    *downloadFile({ payload }, { select, call, put }) {
      const { username } = yield select(state => state.account);
      yield downloadFile({ ...payload, owner: username });
    },

    // 获取全部角色
    *getRoles({}, { call, put }) {
      const { data } = yield call(getRoles, { pageSize: 10000 });
      if (data.data) {
        yield put({ type: "save", payload: { roles: data.data.list } });
      }
    },

    // 设置管理员角色
    *setRole({ payload }, { call }) {
      const { data } = yield call(setRole, {
        ...payload
      });
      if (data.code === "200") {
        message.success("授权成功！");
      }
    },

    // 获取当前的管理角色，用于显示使用，如果没有则不显示
    *getCurrentRole({}, { call, put }) {
      const { data } = yield call(getCurrentRole);
      if (data.code === "200") {
        yield put({
          type: "save",
          payload: {
            currentRole: data.data.length > 0 ? data.data[0] : {}
          }
        });
      }
    },
    //删除文件
    *deleteFile({ payload }, { select, call, put }) {
      const { username } = yield select(state => state.account);
      const { data } = yield deleteFile({ ...payload, owner: username });
      const { code } = data;
      if (code === "200") {
        let str = "?";
        if (payload.type !== undefined && payload.type) {
          str += "type=" + payload.type;
        }
        if (payload.keyword !== undefined && payload.keyword) {
          if (str === "?") {
            str += "keyword=" + payload.keyword;
          } else {
            str += "&keyword=" + payload.keyword;
          }
        }
        if (str === "?") {
          str = "";
        }
        window.location.href = "#/gather/resourcelist/FileSystem" + str;
        message.success("删除成功！");
      }
    },
    //删除
    *delete({ payload }, { select, call, put }) {
      const { username } = yield select(state => state.account);
      let method = editConfig.get(payload.model)["delete"];
      const { data } = yield method({ name: payload.name, owner: username });
      const { code } = data;
      if (code === "200") {
        message.success("删除成功！");
        let str = "";
        if (payload.keyword !== undefined && payload.keyword) {
          str = "?keyword=" + payload.keyword;
        }
        window.location.href = editConfig.get(payload.model)["query"] + str;
      }
    },
    //编辑
    *edit({ payload }, { select, call, put }) {
      const { username } = yield select(state => state.account);
      let method = editConfig.get(payload.model)["edit"];
      const { data } = yield method({ name: payload.name, owner: username });

      const { code } = data;
      if (code === "200") {
        yield put({
          type: "changeStatus",
          payload: {
            config: data.data,
            view: "model"
          }
        });
      }
    },
    /*获取JNDI列表*/
    *getJndiList({ payload }, { select, call, put }) {
      const { username } = yield select(state => state.account);
      const { data } = yield call(getJndiList, { ...payload, owner: username });
      const { code } = data;

      if (code === "200") {
        let args = [];
        for (let index of data.data) {
          args.push({
            name: index.name,
            key: index.name
          });
        }
        yield put({
          type: "changeStatus",
          payload: {
            JNDIargs: args,
            visible: false
          }
        });
      }
    },
    /*编辑JNDI*/
    *editJndi({ payload }, { select, call, put }) {
      const { username } = yield select(state => state.account);
      const { data } = yield call(editJndi, {
        type: payload.type,
        name: payload.name,
        owner: username
      });
      const { code } = data;

      if (code === "200" && data.data) {
        yield put({
          type: "changeStatus",
          payload: {
            JNDIconfig: data.data,
            visible: payload.visible
          }
        });
      }
    },
    /*编辑JNDI*/
    *createJndi({ payload, actionType }, { select, call, put }) {
      const { username } = yield select(state => state.account);
      const { data } = yield call(createJndi, { ...payload, owner: username });
      const { code } = data;

      if (code === "200") {
        yield put({
          type: "getJndiList",
          payload: {
            type: payload.type
          }
        });
        if (actionType === "new") {
          message.success("新建成功！");
        } else {
          message.success("保存成功！");
        }
      }
    },
    *deleteJndi({ payload }, { select, call, put }) {
      const { username } = yield select(state => state.account);
      const { data } = yield call(deleteJndi, { ...payload, owner: username });
      const { code } = data;

      if (code === "200") {
        yield put({
          type: "getJndiList",
          payload: {
            type: payload.type
          }
        });
        message.success("删除成功！");
      }
    },
    //服务器
    *queryServerList({ payload }, { select, call, put }) {
      const { username } = yield select(state => state.account);
      const { data } = yield getServer_list({ ...payload, owner: username });

      const { code } = data;
      if (code === "200") {
        for (let index of data.data.rows) {
          index.key = index.name;
          index.master = index.master ? "是" : "否";
        }
        yield put({
          type: "changeStatus",
          payload: {
            serverList: data.data.rows,
            total: data.data.total,
            loading: false,
            view: "list",
            needUpdate: true
          }
        });
        /* for(let index of data.data.rows){
          yield put({
            type:"testServer",
            payload:{
              name:index.name,
              data:data.data.rows
            }
          });
        }*/
      }
    },
    //服务器
    *testServer({ payload }, { select, call, put }) {
      try {
        const { username } = yield select(state => state.account);
        const { data } = yield test_Server({
          name: payload.name,
          owner: username
        });
        const { code } = data;
        if (code === "200") {
          for (let index of payload.data) {
            if (index.name === data.data.name) {
              index.status = data.data.message;
            }
          }
        } else {
          for (let index of payload.data) {
            if (index.name === payload.name) {
              index.status = "Error";
            }
          }
        }
        yield put({
          type: "changeStatus",
          payload: {
            serverList: payload.data
          }
        });
      } catch (e) {}

      console.log("第一次出来");
    },
    //数据库
    *queryDbList({ payload }, { select, call, put }) {
      const { username } = yield select(state => state.account);
      const { data } = yield getDb_list2({ ...payload, owner: username });
      const { code } = data;
      if (code === "200") {
        for (let index of data.data.rows) {
          index.type = dbList.get(index.type);
          index.key = index.name;
        }
        yield put({
          type: "changeStatus",
          payload: {
            dataList: data.data.rows,
            total: data.data.total,
            loading: false,
            view: "list",
            needUpdate: true
          }
        });
        /*  for(let index of data.data.rows){
              yield put({
                type:"testDb",
                payload:{
                  name:index.name,
                  data:data.data.rows
                }
              });
          }*/
      }
    },
    //测试数据库状态
    *testDb({ payload }, { select, call, put }) {
      const { username } = yield select(state => state.account);
      const { data } = yield test_DbConnection({ ...payload.name, owner: username });
      const { code } = data;

      if (code === "200") {
        for (let index of payload.data) {
          if (index.name === data.data.name) {
            index.testStatus = data.data.message;
          }
        }
      } else {
        for (let index of payload.data) {
          if (index.name === payload.name) {
            index.testStatus = "Error";
          }
        }
      }
      yield put({
        type: "changeStatus",
        payload: {
          dataList: payload.data
        }
      });
    },
    //获得文件树
    *getFileTree({ payload }, { select, call, put }) {
      const { username } = yield select(state => state.account);
      const { data } = yield getFileList({ ...payload, owner: username });
      const { code } = data;
      if (code === "200") {
        if (data.data.rows) {
          let i = 0;
          for (let index of data.data.rows) {
            index.key = i++;
          }
        }
        yield put({
          type: "changeStatus",
          payload: {
            fileList: data.data.rows || [],
            total: data.data.total,
            loading: false,
            view: "list"
          }
        });
      }
    },
    //服务器集群
    *queryClusterList({ payload }, { select, call, put }) {
      const { username } = yield select(state => state.account);
      const { data } = yield getCluster_list({ ...payload, owner: username });

      const { code } = data;
      if (code === "200") {
        for (let index of data.data.rows) {
          index.key = index.name;
          index.dynamic = index.dynamic ? "是" : "否";
        }
        yield put({
          type: "changeStatus",
          payload: {
            clusterList: data.data.rows,
            total: data.data.total,
            loading: false,
            view: "list"
          }
        });

        console.log(data.data.rows);
      }
    },
    //执行引擎
    *queryDefaultEngine({ payload }, { select, call, put }) {
      const { username } = yield select(state => state.account);
      const { data } = yield getDefaultEngineList({ ...payload, owner: username });
      const { code } = data;
      if (code === "200") {
        for (let index of data.data.rows) {
          index.key = index.name;
        }
        yield put({
          type: "changeStatus",
          payload: {
            engineList: data.data.rows,
            total: data.data.total,
            loading: false,
            view: "list"
          }
        });
      }
    },
    //获取hadoop列表
    *queryHadoopList({ payload }, { select, call, put }) {
      const { username } = yield select(state => state.account);
      const { data } = yield getHadoop_list({ ...payload, owner: username });

      const { code } = data;
      if (code === "200") {
        for (let index of data.data.rows) {
          index.key = index.name;
        }
        yield put({
          type: "changeStatus",
          payload: {
            hadoopList: data.data.rows,
            total: data.data.total,
            loading: false,
            view: "list"
          }
        });
      }
    },
    //获取spark列表
    *querySparkList({ payload }, { select, call, put }) {
      const { username } = yield select(state => state.account);
      const { data } = yield getSpark_list({ ...payload, owner: username });
      const { code } = data;

      if (code === "200") {
        for (let index of data.data.rows) {
          index.key = index.name;
        }
        yield put({
          type: "changeStatus",
          payload: {
            sparkList: data.data.rows,
            total: data.data.total,
            loading: false,
            view: "list"
          }
        });
      }
    }
  }
};
