// import Cookies from 'js-cookie';
import { LOGIN_SUCCESS, LOGIN_FAILED, LOGIN_SUSPEND } from '../constants';
import { submitLogin, getCaptchaImg } from '../services/login';
import { saveLocalUser } from '../utils/session';
import { resetGoLogin } from 'utils/goLogin';

export default {
  namespace: 'loginModel',

  state: {
    loginStatus: LOGIN_SUSPEND,
    //错误信息
    message:"",
    //登录态文字提示
    loginText:"登录"
  },
  effects: {
    // 登录
    *login({ payload }, { put }) {
      yield put({ type:"save",payload:{ loginText:"登录中..." } });
      const { data } = yield submitLogin(payload);
			yield put({ type:"getCaptcha" });
			const { msg,code,status } = data;
			let message = msg;
      if(code === "200"){
        const { loginUser,sysList } = data.data;
        const { username, renterId, deptId, realName,id,pswd,email,phone } = loginUser;
        //设置登录态VT
        //cookie.set('VT', vt, { Path: '/'});
        //保存用户名密码到localstorage
        if (payload.rememberMe) {
          saveLocalUser({
            userName: payload.name,
            password: payload.passwd,
          });
        } else {
          saveLocalUser(null);
        }
        //保存用户信息
        yield put({
          type: "account/save",
          payload:{
            loginUser,username, renterId, deptId, realName,sysList,
            message,id,pswd,email,phone
          }
        })
        //重置登录
        resetGoLogin();
        //更新用户登录状态
        yield put({ type:"save",payload:{ loginStatus:LOGIN_SUCCESS,loginText:"登录" } })
      }else{
        //重复请求避免判定登录失败
        if(status === 600){
          return false;
        }
        //更新用户登录状态
        yield put({ type:"save",payload:{ loginStatus:LOGIN_FAILED,message,loginText:"登录"  } })
      }
    },

    // 获取验证码
    *getCaptcha({}, { put }) {
			const { data } = yield getCaptchaImg();
			const { code } = data;
			if(code === "200"){
				yield put({
					type: 'save',
					payload: {
						captchaImg: data.data,
					},
				});
			}
    },
  },

  reducers: {
    // 重置登录状态
    resetStatus(state) {
      return { ...state, loginStatus: LOGIN_SUSPEND };
    },

    save(state, action) {
      return { ...state, ...action.payload };
    },
  },

};
