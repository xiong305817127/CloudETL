/**
 * 页头组件
 */
import React from 'react';
import { connect } from 'dva';
import { Icon } from 'antd';
import { Link } from 'react-router';
import PropTypes from 'prop-types';
import baseInfo from 'config/baseInfo.config';
import UserProfile from '../../../routes/user/Profile';
import Style from './HeaderModel.css';
import Modal from 'components/Modal';
import { siteName,version } from "config/baseInfo.config";

class HeaderModel extends React.Component {

  // 退出
  methodLogout(e){
    const { dispatch } = this.props;
    e.preventDefault();
    Modal.confirm({
      title: '退出登录提醒',
      content: '您将退出登录，是否继续？',
      onOk: () => {
        dispatch({ type: 'account/loginOut', payload: '/login'});
      }
    });
  }

  // 点击用户名
  handleClickUserInfo = (e) => {
    const { dispatch } = this.props;
    e.preventDefault();
    dispatch({type: 'account/showProfile'});
  }

  //点击问号
  getPlatformInfo = ()=>{
    Modal.confirm({
      title: siteName,
      content: '版本 '+version,
      iconType: SITE_NAME==="noLogo"?"info-circle":baseInfo.iconType
    });
  };

  render() {
    const { account,getPlatformInfo } = this.props;
    return <header className={Style.header}>
      <div className={Style.hLeft}>
        <Link className={Style.a} to="/home" title="返回首页">
          <img className={Style.logoImg} style={SITE_NAME==="noLogo"?{display:"none"}:null} src={baseInfo.logo}/>
          {baseInfo.siteName}
        </Link>
        <h1 className={Style.title}>{this.props.title}</h1>
      </div>
      <div className={Style.hRight}>
        <a className={Style.a} href="#" onClick={this.handleClickUserInfo}>
          <Icon className={Style.icon} type="user" />
          <span className={Style.text}>{account.username}</span>
        </a>
        <i className={Style.virgule}></i>
        <a className={Style.a}><Icon className={Style.icon} onClick={getPlatformInfo?getPlatformInfo:this.getPlatformInfo} type="question-circle-o" /></a>
        <i className={Style.virgule}></i>
        <a className={Style.a} href="#" onClick={this.methodLogout.bind(this)}><Icon className={Style.icon} type="poweroff" /></a>
      </div>
      <UserProfile />
    </header>
  }
}

HeaderModel.propTypes = {
  // 菜单配置
  menu: PropTypes.object.isRequired,
};

export default connect(({ system, account }) => ({
  system,
  account,
}))(HeaderModel);
