import { getUserSession } from 'utils/session';
import { loginOut, submitLoginForThirdParty } from 'services/login';
import { unNeedLoginPass } from 'services/securityCommon';
import { resetOtherUser } from 'utils/goLogin';
import { routerRedux } from 'dva/router';
import { STANDALONE_ETL } from '../constants';
import unsafeRoutes from "../unsafeRoutes";


const initState = {
  //存储Bi Id 
  JSESSIONID: "",
  //显示用户概况
  profileVisible: false,
  //显示修改密码
  passwordEditorVisible: false,
  //系统访问权限列表
  sysList: [],
  //登录的用户信息
  loginUser: {},
  //用户名
  username: "",
  //租户id
  renterId: "",
  //部门id
  deptId: "",
  //真实姓名
  realName: "",
  //用户id
  id: "",
  //用户密码
  pswd: "",
  //邮箱
  email: "",
  //电话号码
  phone: ""
}

export default {
  namespace: 'account',
  state: { ...initState },

  effects: {
    //第三方登录接口
    *submitLoginForThirdParty({ payload }, { put }) {
			const { data } = yield submitLoginForThirdParty(payload);
			const { code,msg } = data;
      if (code === "200") {
				let message = msg;
        const { loginUser, sysList } = data.data;
        const { username, renterId, deptId, realName, id, pswd, email, phone } = loginUser;
        //设置登录态VT
        //cookie.set('VT', vt, { Path: '/'});
        //保存用户信息
        yield put({
          type: "save",
          payload: {
            loginUser, username, renterId, deptId, realName, sysList,
            message, id, pswd, email, phone
          }
        });

        //如果为重庆系统
        // if (baseInfo.premit && baseInfo.premit.includes("bi") && !STANDALONE_ETL) {
        //   const { data } = yield call(getLoginPrmit, {
        //     username: payload.username ? payload.username : username,
        //     password: payload.pswd ? payload.pswd : pswd,
        //   });
        //   if (data && data.message === "success") {
        //     cookie.set('JSESSIONID', data.data, { Path: '/bi' });
        //     yield put({ type: "save", payload: { JSESSIONID: data.data } });
        //   }
        // }

        //保存系统权限
        yield put(routerRedux.push("/home"));
      }
    },
    // 退出登陆
    *loginOut({ payload }, { put, call }) {
      yield put({ type: 'clearSystem' });
      //cookie.remove("VT", { Path: "/" });
      //退出系统
			const { data } = yield call(loginOut);
			const { code } = data;
			if(code === "200"){
				yield put(routerRedux.push(payload));
			}
      //刷新验证码
      //yield put({ type:"loginModel/getCaptcha" })
    },
    // *getLoginPrmit({ payload }, { select, call, put }) {
    //   const { username, pswd } = yield select(state => state.account);
    //   const { data } = yield call(getLoginPrmit, {
    //     username: payload.username ? payload.username : username,
    //     password: payload.pswd ? payload.pswd : pswd,
    //   });
    //   // if (data && data.message === "success") {
    //   //   cookie.set('JSESSIONID', data.data, { Path: '/bi' });
    //   //   yield put({ type: "save",payload: { JSESSIONID: data.data } });
    //   // }
    // },
    // 清理各种系统级缓存
    *clearSystem({ }, { put, call, select }) {
      //清除数据集成数据
      yield put({ type: 'transheader/clearHeader' });
      yield put({ type: 'transspace/clear' });
      yield put({ type: 'jobheader/clearHeader' });
      yield put({ type: 'jobspace/clear' });
      //清除数据质量
      yield put({ type: "designSpace/clear" });

      // 清理授权数据
      yield put({ type: 'system/clearPermits' });
      // 清理元数据公共数据
      yield put({ type: 'metadataCommon/clear' });
      // 清理数据资源目录公共数据
      yield put({ type: 'resourcesCommon/clear' });
      //清空account 内数据
      yield put({ type: 'clear' });
      //清空saiku  cookie
      //cookie.remove("JSESSIONID", { Path: "/bi" });
    },
    // 带VT鉴权
    *unNeedLoginPass({ path }, { call, put, select }) {
      const userVt = getUserSession();
      const { id } = yield select(state => state.account);

      if (userVt && !id) {
				const { data } = yield call(unNeedLoginPass);
				const { code,msg } = data;
        if (code === "200") {
					let message = msg;
          const { loginUser, sysList } = data.data;
          const { username, renterId, deptId, realName, id, pswd, email, phone } = loginUser;
          //如果为重庆系统
          // if (baseInfo.premit && baseInfo.premit.includes("bi")  && !STANDALONE_ETL) {
          //   const { data } = yield call(getLoginPrmit, { username, password:pswd });
          //   if (data && data.message === "success") {
          //     cookie.set('JSESSIONID', data.data, { Path: '/bi' });
          //     yield put({ type: "save", payload: { JSESSIONID:data.data } });
          //   }
          // }
          //设置登录态VT
          //cookie.set('VT', vt, { Path: '/'});
          //保存用户信息
          yield put({
            type: "save",
            payload: {
              loginUser, username, renterId, deptId, realName,
              sysList, message, id, pswd, email, phone
            }
          });
        } else {
          yield put({ type: "clearSystem" })
        }
      }
    }
  },

  subscriptions: {
    setup({ history, dispatch }) {
      return history.listen(({ pathname, query }) => {

        console.log(pathname,"无须鉴权");
        if(unsafeRoutes.some(index=>index.path === pathname) || STANDALONE_ETL){
            return false;
        }

        if (pathname === "/home" && query.name && query.passwd) {
          resetOtherUser(true);
          dispatch({
            type: "submitLoginForThirdParty",
            payload: {
              ...query, rememberMe: false
            }
          })
        } else {
          //不在登录页的时候
          if (pathname !== "/login") {
            dispatch({ type: 'unNeedLoginPass', path: pathname });
          }
        }
      });
    },
  },

  reducers: {
    // 显示用户概况弹窗
    showProfile(state) {
      return { ...state, profileVisible: true };
    },
    // 隐藏用户概况弹窗
    hideProfile(state) {
      return { ...state, profileVisible: false };
    },
    // 显示修改密码弹窗
    showPasswordEditor(state) {
      return { ...state, passwordEditorVisible: true };
    },
    // 隐藏修改密码弹窗
    hidePasswordEditor(state) {
      return { ...state, passwordEditorVisible: false };
    },
    // 清空用户信息
    clear(state) {
      return { ...initState }
    },
    save(state, action) {
      return { ...state, ...action.payload };
    },
  },

};
