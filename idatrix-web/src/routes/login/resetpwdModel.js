// import Cookies from 'js-cookie';
import { LOGIN_SUCCESS, LOGIN_FAILED, LOGIN_SUSPEND } from 'constants';

export default {
  namespace: 'resetpwdPage',

  state: {
    loginStatus: LOGIN_SUSPEND,
  },

  effects: {
    // 注册
    // *register({ payload }, { put }) {
    //   const { data } = yield registerModel(payload);
    //   if (!data) return;
    //   const result = { message: data.message };
    //   if (data.status === 200) {
    //     const { loginUser } = data;
    //     const vt = data.vt ? data.vt : data.backUrl.replace(/^.+__vt_param__=([0-9a-f]+)$/i, '$1');
    //     result.loginStatus = LOGIN_SUCCESS;
    //     result.userInfo = { sysList: data.sysList };
    //     Cookies.set('VT', vt, {
    //       path: '/',
    //     });
  },


  reducers: {
    // 重置设置密码状态
    resetStatus(state) {
      return { ...state, ...{ loginStatus: LOGIN_SUSPEND } };
    },

    save(state, action) {
      const { payload } = action;
      return { ...state, ...payload };
    },
  },

};
