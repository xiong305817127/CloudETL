/**
 * 登录页修饰器
 */

import React from 'react';
import { message } from 'antd';
import MD5 from 'md5';
import { LOGIN_SUCCESS, LOGIN_FAILED } from 'constants';
import { getLocalUser } from 'utils/session';
import { hashHistory } from "dva/router";
import baseInfo from "config/baseInfo.config.js";

const md5Patt = /[0-9a-f]{32}/i;

//成功登陆强制刷新页面
// const reloadUrl = () => {
//   let myurl = location.origin + "/#/home?1";
//   let times = myurl.split("?");
//   if (times[1] != "_k=63w212") {
//     myurl += "?_k=63w212";
//     self.location.replace(myurl);
//     history.go(0);
//   }
// }

// 登录处理修饰器
export const loginDecorator = Component => class extends React.Component {

  constructor(props) {
    super(props);
    props.dispatch({ type: 'account/clear' });
  }

  state = {
    captchaImg: '',
    userName: '',
    password: '',
    rememberMe: false,
  };

  componentWillMount() {
    //获取记住密码用户信息
    const data = getLocalUser();
    const { loginModel } = this.props;
    const newState = { ...loginModel };
    if (data) { // 记住密码
      Object.assign(newState, {
        userName: data.userName,
        password: data.password,
        rememberMe: true,
      });
    }
    this.setState(newState);
    this.drawCaptcha();
  }

  componentWillReceiveProps(nextProps) {
    const { loginModel, dispatch, account } = nextProps;
    //const { query } = this.props.location;

    switch (loginModel.loginStatus) {
      case LOGIN_SUCCESS: // 登录成功
        //console.log(account, "账号信息");
        if (account.loginUser.visitTimes < 1) { // 首次登录，弹出修改密码窗口
          dispatch({ type: 'account/showPasswordEditor' });
          hashHistory.push("home");
          //重置登录状态为待定
          dispatch({ type: 'loginModel/resetStatus' });
        } else {
          //跳转新界面,并强制刷新页面
          // reloadUrl();
          hashHistory.push("home");
          window.location.reload(true)
        }
        // if (baseInfo.premit && baseInfo.premit.includes("bi")) {
        //   dispatch({ type: 'account/getLoginPrmit', payload: {} });
        // }
        //重定向登录后的界面
        //const resetUrl = query.backUrl ? query.backUrl : '#/home';
        //console.log(resetUrl,"进行登录");
        return;
      case LOGIN_FAILED: // 登录失败
        const { form } = this.props;
        if (loginModel.message && loginModel.message.indexOf('验证码') > -1) {
          form.setFields({
            captcha: {
              errors: [new Error(loginModel.message)],
            }
          });
        } else if (loginModel.message && loginModel.message.indexOf('密码') > -1) {
          form.setFields({
            userName: {
              value: form.getFieldValue('userName'),
              errors: [new Error('　')],
            },
            password: {
              errors: [new Error(loginModel.message)],
            },
          });
        } else {
          // 登陆失败后，返回message，如果没有，则返回601错误。
          message.error(loginModel.message ? loginModel.message : "登陆失败，请稍后再试。ErrorCode: 601 (登陆错误)");
        }
        //重置登录状态为待定
        dispatch({ type: 'loginModel/resetStatus' });
        break;
      default:
    };
  }

  // 绘制校验码
  drawCaptcha(e) {
    if (e) e.preventDefault();
    const { dispatch } = this.props;
    dispatch({ type: 'loginModel/getCaptcha' });
  }

  // 提交表单
  submit() {
    const { dispatch } = this.props;
    this.props.form.validateFields((err, values) => {
      if (err) return;
      const obj = {};
      obj.name = values.userName;
      obj.passwd = md5Patt.test(values.password) ? values.password : MD5(`#${values.password}`);
      obj.rememberMe = values.remember;
      obj.captcha = values.captcha;
      obj.backUrl = '';
      dispatch({
        type: 'loginModel/login',
        payload: obj,
      });
    });
  }

  render() {
    return (
      <Component
        {...this.props}
        {...this.state}
        drawCaptcha={this.drawCaptcha.bind(this)}
        submit={this.submit.bind(this)}
      />
    );
  }
};

// 回退处理修饰器
export const gobackDecorator = Component => class extends React.Component {
  componentWillMount() {
    // 禁止未登录状态回退
    this.props.router.setRouteLeaveHook(
      this.props.route,
      this.routerWillLeave.bind(this),
    );
  }

  routerWillLeave(nextLocation) {
    // 禁止绕过登录
    if (!this.props.account.id && nextLocation.pathname !== '/resetpwd') {
      const backUrl = this.props.location.query.backUrl || '';
      const loginUrl = !backUrl ? '#/login' : '#/login?backUrl=' + encodeURIComponent(backUrl);
      window.location.replace(loginUrl);
      return false;
    }
  }

  render() {
    return (
      <Component {...this.props} />
    );
  }
};
