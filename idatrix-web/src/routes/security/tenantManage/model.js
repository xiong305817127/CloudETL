import { getList, getServicesList, getResourcesList, disalbeTenant,resetPassword } from 'services/securityTenant';
import {message} from "antd"
import {hashHistory} from "dva/router"

export default {

  namespace: 'tenantManage',

  state: {
    list: [],
    serviceProperties: {},
    resourcesList: [],
  },

  effects: {
    *getList({ payload }, { put }) {
      const { data } = yield getList(payload);
      yield put({ type: 'save', payload: data.data });
    },
    *disable({payload}){
      const {data} = yield disalbeTenant(payload)

      if(data.code === "200"){
        message.success("操作成功");
        hashHistory.push("/security/TenantManagementTable")
      }
    },
    *resetPassword({payload}){
      const {data} = yield resetPassword(payload)

      if(data.code === "200"){
        message.success("操作成功");
        hashHistory.push("/security/TenantManagementTable")
      }
    },
    // fast-fix!
    *getServicesList({}, { put,call }) {
      const { data } = yield call(getServicesList);
      console.log("datawsjeifjisejf",data)
      if (data && data.data) {
        const properties = (data.data.items[0] && data.data.items[0].configurations[0].properties) || {};
        yield put({ type: 'save', payload: { serviceProperties: properties } });
      }
    },
    *getResourcesList({}, { put }) {
      const { data } = yield getResourcesList();
      if (data && data.data) {
        yield put({ type: 'save', payload: { resourcesList: data.data } });
      }
    },
  },

  subscriptions: {
    setup({ history, dispatch }) {
      // 监听 history 变化，当进入 `/` 时触发 `load` action
      return history.listen(({ pathname, query }) => {
        if (pathname === '/security/TenantManagementTable') {
          dispatch({
            type: 'getList',
            payload: {
              pageNo: query.page,
              pageSize: query.pageSize,
            },
          });
          dispatch({ type: 'getResourcesList' })
        }
        // 新增/修改租户
        if (pathname === '/security/TenantManagementTable/NewTableFlow'
          || pathname.indexOf('ModifyTableFlow') > -1
        ) {
          dispatch({type: 'getServicesList'});
          dispatch({type: 'getResourcesList'});
        }
      });
    },
  },

  reducers: {
    save(state, action) {
      return { ...state, ...action.payload };
    },
  },

};
