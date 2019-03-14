// Big data integration platform.
import React from 'react';
import { connect } from 'dva';
import { Layout, Row, Col } from 'antd';
import HomeComponent from 'components/HomeComponent';
import Modal from 'components/Modal';
import baseInfo from 'config/baseInfo.config';
import SlickTrack from './components/SlickTrack';
import GridModule from './components/GridModule';
import UserProfile from '../user/Profile';

import Style from './HomePage.css';

const { Header, Footer, Content } = Layout;

class HomePage extends HomeComponent {

  methodAdmin = () => {
    const { dispatch } = this.props;
    dispatch({type: 'account/showProfile'});
  }

  methodLogout(e){
    const { dispatch } = this.props;
    e.preventDefault();
    Modal.confirm({
      title: '退出登录提醒',
      content: '您将退出登录，是否继续？',
      onOk: () => {
        dispatch({ type: 'account/loginOut', payload: '/login'});
      },
      okText:"确定",
      cancelText:"取消"
    });
  }

  render() {
    const { inletsConfig } = this.state;
    return (
      <div id="HomePage">
        <Layout className="LayoutModel">
          <Header className="HeaderModel">
            {/*1.LOGO与用户管理*/}
            <Row>
              {/*1.1.LOGO*/}
              <Col offset={2} span={8} className="LogoAndText">
                <img className={Style.logo} style={SITE_NAME==="noLogo"?{display:"none"}:null} src={baseInfo.logo} height="50" />
                <span className={Style.siteName}>{baseInfo.siteName}</span>
              </Col>

              {/*1.2.admin*/}
              <Col offset={6} span={5} className="adminAndOut" style={{textAlign: 'right', paddingRight: 30}}>
                <a href="#home" onClick={this.methodAdmin} className="AdminImg">
                  <img src={require('../../assets/images/user.png')}/>
                  <span style={{paddingLeft: 7}}>{this.props.account.username}</span>
                </a>
              </Col>
              {/*修改admin密码*/}
              <UserProfile />

              {/*1.3.退出*/}
              <Col span={2} className="adminAndOut">
                <a href="#" onClick={this.methodLogout.bind(this)} className="OutImg">
                  <img src={require('../../assets/images/drop_out.png')}/>
                  <span style={{paddingLeft: 7}}>退出</span>
                </a>
              </Col>

            </Row>
            {/*2.1.轮播图片：文字*/}
            <Row style={{textAlign: 'center'}}>
              <img src={require('../../assets/images/header-title.png')} height="67" />
            </Row>
           {/*2.2.走马灯*/}
           <Row>
            <SlickTrack inletsConfig={inletsConfig} />
           </Row>
          </Header>

          {/*3.内容*/}
          <Content className="ContentModel">
            <GridModule inletsConfig={inletsConfig} />
          </Content>

          {/*尾部*/}
          <Footer className="FooterModel">
            {baseInfo.copyright}
          </Footer>
        </Layout>

      </div>
    )
  }
}

export default connect(({ account, system }) => ({
  account,
  system,
}))(HomePage);
