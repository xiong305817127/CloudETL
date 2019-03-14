const initState = {
  log_list: [],
  executionId: "",
  viewId: "",
  visible: false,
  //最新的预览数据
  title: "",
  columns: [],
  dataSource: [],
  //预览框是否展示
  previewVisible: false,
  //所有的数据集
  DebugPreviewDataList: new Map()
}

/*日志打印模块*/
export default {
  namespace: 'infolog',
  state: {
    ...initState
  },
  reducers: {
    save(state, action) {
      return { ...state, ...action.payload };
    },
    'printLog'(state, action) {
      action.infoLog.log = decodeURIComponent(action.infoLog.log.trim().replace(/[\r\n]/g, ''));
      action.infoLog.key = jsPlumbUtil.uuid();
      if (state.executionId === action.executionId) {
        state.log_list.push(action.infoLog);
        let args = state.log_list;
        return {
          ...state,
          log_list: args
        };
      } else {
        return {
          ...state,
          executionId: action.executionId,
          log_list: [
            ...[],
            action.infoLog
          ]
        }
      }
    },
    cleanLog(state, action) {
      state.columns.splice(0);
      state.dataSource.splice(0);
      state.log_list.splice(0);
      state.DebugPreviewDataList.clear();

      return {
        ...initState
      }
    },
    clean(state, action) {
      return {
        ...state,
        log_list: []
      }
    },
    preview(state, action) {
      const { DebugPreviewData } = action;
      const { DebugPreviewDataList } = state;

      let title = [...Object.keys(DebugPreviewData)][0];
      let columns = DebugPreviewData[title].shift();
      let dataSource = DebugPreviewData[title].map((index, key) => {
        let i = 0; let obj = {};
        for (let name of columns) {
          obj[name] = index[i++];
        }
        obj.key = key;
        return obj;
      });

      if (DebugPreviewDataList.has(title)) {
        let data = DebugPreviewDataList.get(title);
        let length = data.dataSource.length;

        let newDatasource = data.dataSource.concat(dataSource.map(index => {
          return {
            ...index,
            key: index.key + length
          }
        }));
        DebugPreviewDataList.set(title, { ...data, dataSource: newDatasource });
      } else {
        DebugPreviewDataList.set(title, { columns, dataSource })
      }

      return {
        ...state, title, columns, dataSource,
        previewVisible: true,
        DebugPreviewDataList,
        executionId: action.executionId
      }
    }
  }
};


