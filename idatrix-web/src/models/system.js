export default {
  namespace: 'system',

  state: {
    currentSystemId: '',
    pathname: '/',
    query: {},
    permits: {},
    routesConfig: [],
    pageLoading: true, // 页面是否正在加载
  },

  subscriptions: {
    setup({ history, dispatch }) {
      return history.listen(({ pathname, query, key }) => {
        dispatch({
          type: 'save',
          payload: {
            pathname,
            query,
            _k: key,
          },
        });
      });
    },
  },
  effects:{
    *setcurrentSystemId({payload},{select,put}){
      const { currentSystemId } = yield select(state=>state.system);

      if(payload && payload.trim() && currentSystemId !== payload){
          yield put({
            type:"currentSystemId",payload:{ currentSystemId:payload }
          })
      }
    }
  },
  reducers: {
    // 设置当前系统id
    currentSystemId(state, action) {
      console.log("设置当前系统id",action);
      return {...state,...action.payload};
    },

    // 注入权限
    // 
    injectPermits(state, action) {
      const { id, permits } = action.payload;
      const newState = { ...state };
			newState.permits[id] = permits;
      return newState;
    },

    // 保存路由信息
    saveFullPathRoute(state, action) {
      return { ...state, routesConfig: action.payload };
    },

    // 更新页面加载状态
    updatePageLoading(state, action) {
      return { ...state, pageLoading: action.payload };
    },

    // 清空授权
    clearPermits(state, action) {
      return { ...state, permits: {} };
    },

    save(state, action) {
      return { ...state, ...action.payload };
    },
  },

};
