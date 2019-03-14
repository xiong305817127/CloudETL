/**
 * 默认皮肤登录页
 */

import React from 'react';
import { withRouter } from 'react-router';
import { connect } from 'dva';
import baseInfo from 'config/baseInfo.config';
import Style from './LoginPage.css';
import LoginModel from './components/LoginModel';
import { gobackDecorator } from './loginDecorator';

@gobackDecorator
class LoginPage extends React.Component {
  render() {
    const backUrl = this.props.location.query.backUrl || '';
    return <div className={Style.pageWrap}>
      <header className={Style.head}>
        <img className={Style.logo} style={SITE_NAME==="noLogo"?{display:"none"}:null} src={baseInfo.logo} />
        <span className={Style.siteName}>{baseInfo.siteName}</span>
      </header>
      <div className={Style.contentWrap}>
        <section className={Style.swirlWrap}>
          <img className={Style.swirlBall} src={require("../../assets/images/round.png")}/>
          <img className={Style.circle} src={require("../../assets/images/round_line.png")}/>
        </section>
        <section className={Style.loginWrap}>
          <LoginModel backUrl={backUrl} />
        </section>
      </div>
    </div>
  }
}

export default connect(({ account }) => ({
  account,
}))(withRouter(LoginPage));
