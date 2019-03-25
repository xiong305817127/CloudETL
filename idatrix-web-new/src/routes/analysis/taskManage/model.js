import { getTaskList, viewTask, getReport, getTaskExecLog, getNodeExecLog, getTaskNodeInfo } from '../../../services/analysisTask';

export default {

  namespace: 'taskManage',

  state: {
    taskList: {},
    view: {
      data: {}
    },
    taskReport: {
      executions: [],
    },
    taskExecLog: '',
    nodeExecLog: '',
    nodeInfo: {
      nodes: [],
    }
  },

  effects: {
    // 获取任务列表
    *getTaskList({ payload }, { put }) {
      const { data } = yield getTaskList(payload);
			const { code } = data;
			if(code === "200"){
				yield put({ type: 'save', payload: { taskList: data.data || {} } });
			}
    },

    // 获取任务详情
    *getTaskView({ payload }, { put }) {
			const { data } = yield viewTask(payload);
			

      yield put({ type: 'save', payload: { view: data } });
    },

    // 获取统计报表
    *getReport({ payload }, { put }) {
      const { data } = yield getReport(payload);
      yield put({ type: 'save', payload: { taskReport: data && data.data || { executions: [] } } });
    },

    // 获取节点信息
    *getTaskNodeInfo({ payload }, { put }) {
      const { data } = yield getTaskNodeInfo(payload);
      yield put({ type: 'save', payload: { nodeInfo: data && data.data || { nodes: [] } } });
    },

    // 获取任务执行日志
    *getTaskExecLog({ payload }, { put }) {
      const { data } = yield getTaskExecLog(payload);
      const taskExecLog = data && data.data ? data.data.data : '';
      yield put({ type: 'save', payload: { taskExecLog } });
    },

    // 获取节点执行日志
    *getNodeExecLog({ payload }, { put }) {
      const { data } = yield getNodeExecLog(payload);
      const nodeExecLog = data && data.data ? data.data.data : '';
      const length = data && data.data ? data.data.length : 0;
      yield put({ type: 'saveNodeExecLog', payload: { nodeExecLog, length, offset: payload.offset } });
    },
  },

  subscriptions: {
    setup({ history, dispatch }) {
      return history.listen(({ pathname, query }) => {
        if (pathname === '/analysis/TaskManage') {
          dispatch({
            type: 'getTaskList',
            payload: {
              pageNum: query.page,
              pageSize: query.pageSize,
            },
          });
        }
      });
    },
  },

  reducers: {
    saveNodeExecLog(state, action) {
      const { nodeExecLog, offset, length } = action.payload;
      return {
        ...state,
        nodeExecLog: offset > 0 ? state.nodeExecLog + nodeExecLog : nodeExecLog,
        nodeExecLogLen: length,
      };
    },
    save(state, action) {
      return { ...state, ...action.payload };
    },
  },

};
