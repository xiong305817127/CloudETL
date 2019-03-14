/**
 * Created by Administrator on 2017/9/11 0011,update on 2017/11/1
 * 配置服接口：getRuleList
 */
import { getRoleList } from '../../../services/menuManage';

export default {

  namespace: 'desensitizationRuleManage',

  // state: {
  //   list: [],
  // },
  //
  // effects: {
  //   *getList({ payload }, { put }) {
  //     const res = yield getRoleList(payload);
  //     yield put({ type: 'save', payload: res.data });
  //   },
  // },
  //
  // subscriptions: {
  //   setup({ history, dispatch }) {
  //     return history.listen(({ pathname, query }) => {
  //       if (pathname === '/RoleManagementTable') {
  //         dispatch({
  //           type: 'getList',
  //           payload: {
  //             pageNo: query.page,
  //             pageSize: query.pageSize,
  //           },
  //         });
  //       }
  //     });
  //   },
  // },
  //
  // reducers: {
  //   save(state, action) {
  //     return { ...state, ...action.payload };
  //   },
  // },

};
