import {
  getTrans_list,
  getTrans_group,
  getServer_list,
	batchTransStop,
	downloadFile
} from "../../../../services/gather";
import {
  getJob_list,
  batchJobsStop,
  getJob_group
} from "../../../../services/gather1";
import {
  runStatus,
  errorStatus,
  statusType,
  transPageSize,
  jobPageSize,
  statusType1
} from "../../constant";
import { message } from "antd";
import _ from "lodash";

const getTaskList = (select, value1, args) => {
  let taskList = [];
  let value = "";
  if (value1) {
    value = value1.toLowerCase();
  }
  if (value1 && select) {
    taskList = args.filter(index => {
      if (index.description) {
        return (
          (index.name.toLowerCase().indexOf(value) >= 0 ||
            index.description.toLowerCase().indexOf(value) >= 0) &&
          index.status === select
        );
      } else {
        return (
          index.name.toLowerCase().indexOf(value) >= 0 &&
          index.status === select
        );
      }
    });
  } else if (value && !select) {
    taskList = args.filter(index => {
      if (index.description) {
        return (
          index.name.toLowerCase().indexOf(value) >= 0 ||
          index.description.toLowerCase().indexOf(value) >= 0
        );
      } else {
        return index.name.toLowerCase().indexOf(value) >= 0;
      }
    });
  } else if (!value && select) {
    taskList = args.filter(index => {
      return index.status === select;
    });
  } else {
    taskList = args;
  }
  return taskList;
};

const getOptionList = args => {
  let options = [];
  let newSet = new Set(args);
  for (let index of newSet) {
    options.push({
      type: index,
      name: statusType.get(index)
    });
  }
  return options;
};

const getTotal = args => {
  let runTotal = 0;
  let errorTotal = 0;
  for (let index of args) {
    if (errorStatus.has(index.status)) {
      errorTotal++;
    }
    if (runStatus.has(index.status)) {
      runTotal++;
    }
  }
  return {
    errorTotal: errorTotal,
    runTotal: runTotal
  };
};

/*提示框消息模块*/
export default {
  namespace: "taskcontent",
  state: {
    loading: false,
    taskList: [],
    taskListMap: {}, // map格式的list数据
    groupList: [],
    dataList: [],
    serverTotal: 0,
    runTotal: 0,
    errorTotal: 0,
    btn1: "btn1Click",
    btn2: "btn2",
    model: "trans",
    keyWord: "",
    status: "",
    optionList: [],
    selectWord: "",
    total_trans: 0,
    total_job: 0,
    selectedRows: [],
    selectedType: "",
    group: "all",
    runType: "default",
    /*运行中的转换*/
    runList: [],
    self: false,
    defaultkeyTaskOpenKey: "",

    isMap: false, // 是否为map格式显示转换列表
    transHistory: {
      page: 1,
      pageSize: 8,
      search: "",
      searchType: ""
    },

    jobsHistory: {
      page: 1,
      pageSize: 8,
      search: "",
      searchType: ""
    }
  },
  reducers: {
    showLoading(state, action) {
      return { ...state, ...action.payload };
    },
    changeBtn(state, action) {
      let obj = {};
      if (action.click === "btn1") {
        obj.btn1 = "btn1Click";
        obj.btn2 = "btn2";
      } else {
        obj.btn2 = "btn2Click";
        obj.btn1 = "btn1";
      }
      return { ...state, ...obj };
    },

    setTaskList(state, action) {
      action.payload.optionList = [];
      if (action.payload.status === "init") {
        action.payload.keyWord = "";
        action.payload.selectWord = "";
      } else {
        action.payload.taskList = getTaskList(
          state.selectWord,
          state.keyWord,
          action.payload.dataList
        );
      }

      if (action.payload.taskList.length > 0) {
        let args = [];
        for (let index of action.payload.taskList) {
          args.push(index.status);
        }
        action.payload.optionList = getOptionList(args);
      }

      let total = getTotal(action.payload.taskList);

      return {
        ...state,
        ...action.payload,
        ...total,
        loading: false
      };
    },
    setServerLength(state, action) {
      return {
        ...state,
        ...action.payload
      };
    },
    search(state, action) {
      let taskList = getTaskList(
        state.selectWord,
        action.keyWord,
        state.dataList
      );
      let optionList = [];
      if (taskList.length > 0) {
        let args = [];
        for (let index of taskList) {
          args.push(index.status);
        }
        optionList = getOptionList(args);
      }

      return {
        ...state,
        keyWord: action.keyWord,
        taskList: taskList,
        optionList: optionList
      };
    },
    select(state, action) {
      let taskList = getTaskList(
        action.selectWord,
        state.keyWord,
        state.dataList
      );
      return {
        ...state,
        taskList: taskList,
        selectWord: action.selectWord
      };
    }
  },
  subscriptions: {
    setup({ dispatch, history }) {
      history.listen(location => {
        let args = location.pathname.split("/");
        if (args[1] === "gather" && args[2] === "taskcenter") {
          dispatch({
            type: "serverList",
            payload: {}
          });
        }

        const { query, pathname } = location;
        let page = query.page ? query.page : 1;
        let pageSize = query.pageSize ? query.pageSize : transPageSize;
        let search = query.keyword ? query.keyword : "";
        let searchType = query.searchType ? query.searchType : "";

        switch (pathname) {
          case "/gather/taskcenter/jobscenter":
            dispatch({
              type: "queryJobList",
              payload: {
                status: "init",
                obj: {
                  page: page,
                  pageSize: pageSize,
                  search: search,
                  searchType: searchType,
                  isMap: true,
                  self: true
                }
              }
            });
            break;
          case "/gather/taskcenter/jobscenter/all":
            dispatch({
              type: "queryJobList",
              payload: {
                status: "init",
                obj: {
                  page: page,
                  pageSize: pageSize,
                  search: search,
                  searchType: searchType,
                  isMap: true,
                  self: false
                }
              }
            });
            break;
          case "/gather/taskcenter/transcenter":
            dispatch({
              type: "queryTransList",
              query,
              payload: {
                status: "init",
                obj: {
                  page: page,
                  pageSize: pageSize,
                  search: search,
                  searchType: searchType,
                  isMap: true,
                  self: true
                }
              }
            });
            break;
          case "/gather/taskcenter/transcenter/all":
            dispatch({
              type: "queryTransList",
              query,
              payload: {
                status: "init",
                obj: {
                  page: page,
                  pageSize: pageSize,
                  search: search,
                  searchType: searchType,
                  isMap: true,
                  self: false
                }
              }
            });
            break;

          default:
            return;
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
    *handleJobTypeChange({ payload }, { call, put }) {
      yield put({ type: "queryJobList" });
    },
    *handleTransTypeChange({ payload }, { call, put }) {
      yield put({ type: "queryTransList" });
    },
    *queryJobList({ payload }, { select, call, put }) {
      const { btn2, group } = yield select(data => data.taskcontent);
      let owner = "";
      let groupList = [];

      if (payload.obj.self) {
        owner = yield select(state => state.account.username);
        payload.obj.owner = owner;
        payload.obj.group =
          typeof payload.obj.group !== "undefined" ? payload.obj.group : group;
        if (payload.obj.group == "all") {
          payload.obj.group = undefined;
        }

        const groupData = yield call(getJob_group, {
          owner: payload.obj.owner
        });
        if (groupData.data.code === "200") {
          groupList = groupData.data.data;
        }
      }

      if (btn2 === "btn2Click" && payload.obj.pageSize === jobPageSize) {
        payload.obj.pageSize = 8;
      }

      yield put({ type: "showLoading", payload: { loading: true } });

      if (payload.obj && payload.obj.searchType) {
        payload.obj.searchType = statusType1.get(
          decodeURIComponent(payload.obj.searchType)
        );
      }

      payload.obj.isMap = payload.obj.isMap ? payload.obj.isMap : true;

      const { data } = yield getJob_list(payload.obj);
      const { code } = data;
      if (code === "200") {
        let taskList = [];
        let runList = [];
        let taskListMap = {};
        let taskListMapSelf = {};
        let total = 0;

        if (!payload.obj.isMap) {
          for (let index of data.data.rows) {
            if (runStatus.has(index.status)) {
              runList.push(index.name);
            }
            total += index.total;
            taskList.push({
              key: index.name,
              ...index
            });
          }
        } else {
          if (Object.prototype.toString.call(data.data) === "[object Object]") {
            for (let index in data.data) {
              data.data[index].rows.forEach(val => {
                if (runStatus.has(val.status)) {
                  runList.push(val.name);
                }

                taskList.push({
                  key: val.name,
                  ...val
                });
              });

              total += data.data[index].total;
            }

            const preTaskListMap = yield select(
              state => state.taskcontent.taskListMap
            );
            if (payload.obj.self) {
              taskListMapSelf = data.data;
            } else {
              taskListMap = { ...preTaskListMap, ...data.data };
            }
          }
        }

        const total_job = yield select(state => state.taskcontent.total_job);

        yield put({
          type: "setTaskList",
          payload: {
            dataList: taskList,
            taskList: taskList,
            status: payload.status,
            model: "job",
            groupList: groupList,
            selectedRows: [],
            runList: [],
            taskListMap: taskListMap,
            taskListMapSelf: taskListMapSelf,
            isMap: payload.obj.isMap,
            self: payload.obj.self,
            self_total_job: total,
            group: payload.obj.group ? payload.obj.group : "all",
            total_job: total_job ? total_job : total,
            defaultkeyTaskOpenKey: payload.obj.owner ? payload.obj.owner : ""
          }
        });
      }
    },

    *queryTransList({ payload, query }, { select, call, put }) {
      const { btn2, group } = yield select(data => data.taskcontent);
      let obj = payload.obj;
      let groupList = [];

      if (btn2 === "btn2Click" && obj.pageSize === transPageSize) {
        obj.pageSize = 8;
      }

      yield put({ type: "showLoading", payload: { loading: true } });

      if (obj && obj.searchType) {
        obj.searchType = statusType1.get(decodeURIComponent(obj.searchType));
      }

      obj.isMap = obj.isMap ? obj.isMap : true;

      let owner = "";
      if (payload.obj.self) {
        owner = yield select(state => state.account.username);
        obj.owner = owner;
        obj.group = typeof obj.group !== "undefined" ? obj.group : group;
        if (payload.obj.group == "all") {
          payload.obj.group = undefined;
        }
        const groupData = yield call(getTrans_group, {
          owner: payload.obj.owner
        });
        if (groupData.data.code === "200") {
          groupList = groupData.data.data;
        }
      }

      const { data } = yield getTrans_list(obj);
      const { code } = data;
      if (code === "200") {
        let taskList = [];
        let runList = [];
        let taskListMap = {};
        let taskListMapSelf = {};
        let total = 0;

        if (!obj.isMap) {
          for (let index of data.data.rows) {
            if (runStatus.has(index.status)) {
              runList.push(index.name);
            }
            total += index.total;
            taskList.push({
              key: index.name,
              ...index
            });
          }
        } else {
          if (Object.prototype.toString.call(data.data) === "[object Object]") {
            for (let index in data.data) {
              data.data[index].rows.forEach(val => {
                if (runStatus.has(val.status)) {
                  runList.push(val.name);
                }
                taskList.push({
                  key: val.name,
                  ...val
                });
              });

              total += data.data[index].total;
            }

            const preTaskListMap = yield select(
              state => state.taskcontent.taskListMap
            );

            if (payload.obj.self) {
              taskListMapSelf = data.data;
            } else {
              taskListMap = { ...preTaskListMap, ...data.data };
            }
          }
        }

        const total_trans = yield select(
          state => state.taskcontent.total_trans
        );

        yield put({
          type: "setTaskList",
          payload: {
            dataList: taskList,
            taskList: taskList,
            model: "trans",
            isMap: obj.isMap,
            status: payload.status,
            selectedRows: [],
            groupList: groupList,
            runList: runList,
            self_total_trans: total,
            total_trans: total_trans ? total_trans : total,
            taskListMap: taskListMap,
            defaultkeyTaskOpenKey: payload.obj.owner ? payload.obj.owner : "",
            self: payload.obj.self,
            group: payload.obj.group ? payload.obj.group : "all",
            taskListMapSelf: taskListMapSelf
            /*  transHistory:newHistory*/
          }
        });
      }
    },
    *serverList({ config }, { select, call, put }) {
      const { data } = yield call(getServer_list);
      const { code } = data;
      if (code === "200") {
        yield put({
          type: "setServerLength",
          payload: {
            serverTotal: data.data.length
          }
        });
      }
    },
    *queryBatchStop({ payload }, { select, call, put }) {
      const { selectedRows, owner } = payload;
      const { model } = yield select(state => state.taskcontent);
      let args = [];
      const filterOwner = selectedRows.filter(val => val.owner === owner);

      if (filterOwner.length > 0) {
        for (let index of filterOwner) {
          args.push(`${index.group}/${index.name}`);
        }

        if (model === "trans") {
          const { data } = yield call(batchTransStop, {
            transNames: args,
            owner
          });

          const { code } = data;
          if (code === "200") {
            message.success("批量停止成功，请稍后刷新页面！");
          }
        } else {
          const { data } = yield call(batchJobsStop, {
            jobNames: args,
            owner
          });

          const { code } = data;
          if (code === "200") {
            message.success("批量停止成功，请稍后刷新页面！");
          }
        }
      } else {
        message.info("请先勾选需要操作的任务");
      }
    }
  }
};
