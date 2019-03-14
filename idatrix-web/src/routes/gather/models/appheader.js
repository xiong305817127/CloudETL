/*云化数据集成头部*/
import {
  getTrans_list,
  getServer_list,
  getDb_list,
  getCluster_list,
  getDefaultEngineList,
  getHadoop_list,
  getSpark_list
} from "../../../services/gather";
import { getJob_list } from "../../../services/gather1";

const config = new Map([
  ["jobList", "queryJobList"],
  ["transList", "queryTransList"],
  ["serverList", "queryServerList"],
  ["dbList", "queryDbList"],
  ["clusterList", "queryClusterList"],
  ["engineList", "queryDefaultEngine"],
  ["hadoopList", "queryHadoopList"],
  ["sparkList", "querySparkList"]
]);

const setMsg = dispatch => {
  for (let [key, value] of config) {
    let args = localStorage.getItem(key);
    if (!args || args.length === 0) {
      dispatch({
        type: value,
        payload: {}
      });
    }
  }
};

export default {
  namespace: "appheader",
  state: {
    viewStatus: "taskCenter"
  },
  reducers: {
    changeStatus(state, action) {
      return { ...state, ...action.payload };
    }
  },
  subscriptions: {
    setup({ dispatch, history }) {
      history.listen(location => {
        let args = location.pathname.split("/");
        if (args[1] === "gather") {
          console.count("调用次数");
          /* setMsg(dispatch);*/
          if (args[2] !== "designplatform") {
            dispatch({
              type: "transheader/changeModel",
              payload: {
                shouldUpdate: true
              }
            });
            dispatch({
              type: "jobheader/changeModel",
              payload: {
                shouldUpdate: true
              }
            });
            //直接清空日志
            dispatch({
              type: "transdebug/cleanDebug"
            });
            dispatch({
              type: "jobdebug/cleanDebug"
            });
          }

          if (args[2] === "taskcenter") {
            dispatch({
              type: "changeStatus",
              payload: {
                viewStatus: "taskCenter"
              }
            });
          } else if (args[2] === "designplatform") {
            dispatch({
              type: "changeStatus",
              payload: {
                viewStatus: "designCenter"
              }
            });
          } else {
            dispatch({
              type: "changeStatus",
              payload: {
                viewStatus: "resourceCenter"
              }
            });
          }
        }
      });
    }
  },
  effects: {
    //job列表
    *queryJobList({ payload }, { select, call, put }) {
      const { data } = yield call(getJob_list);
      console.log(data);
      console.log("job列表");

      if (data) {
        localStorage.setItem("jobList", data);
      }
    },
    //trans列表
    *queryTransList({ payload }, { select, call, put }) {
      const { data } = yield call(getTrans_list);

      console.log(data);
      console.log("trans列表");
      if (data) {
        localStorage.setItem("transList", data);
      }
    },
    //服务器
    *queryServerList({ payload }, { select, call, put }) {
      const { data } = yield call(getServer_list);

      console.log(data);
      console.log("服务器");
      if (data) {
        localStorage.setItem("serverList", data);
      }
    },
    //数据库
    *queryDbList({ payload }, { select, call, put }) {
      const { data } = yield call(getDb_list);

      console.log(data);
      console.log("数据库");
      if (data) {
        localStorage.setItem("dbList", data);
      }
    },
    //服务器集群
    *queryClusterList({ payload }, { select, call, put }) {
      const { owner } = yield select(state => state.transheader);
      const { data } = yield call(getCluster_list, { owner });

      console.log(data);
      console.log("服务器集群");
      if (data) {
        localStorage.setItem("clusterList", data);
      }
    },
    //执行引擎
    *queryDefaultEngine({ payload }, { select, call, put }) {
      const { data } = yield call(getDefaultEngineList);

      console.log(data);
      console.log("执行引擎");
      if (data) {
        localStorage.setItem("engineList", data);
      }
    },
    //获取hadoop列表
    *queryHadoopList({ payload }, { select, call, put }) {
      const { data } = yield call(getHadoop_list);

      console.log(data);
      console.log("获取hadoop列表");
      if (data) {
        localStorage.setItem("hadoopList", data);
      }
    },
    //获取spark列表
    *querySparkList({ payload }, { select, call, put }) {
      const { data } = yield call(getSpark_list);

      console.log(data);
      console.log("获取spark列表");
      if (data) {
        localStorage.setItem("sparkList", data);
      }
    }
  }
};
