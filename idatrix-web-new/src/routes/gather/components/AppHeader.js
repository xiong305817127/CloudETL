/**
 * Created by Administrator on 2017/7/18.
 */
import { connect } from 'dva';
import { Row, Col, Layout, Modal, Icon, message } from 'antd';
import baseInfo from '../../../config/baseInfo.config';
import { STANDALONE_ETL } from '../../../constants';
import { getVersion } from '../../../services/gather';
import { Link } from 'react-router';
import UserProfile from '../../../routes/user/Profile';
import Style from './AppHeader.css';

const { Header, Content } = Layout;


const AppHeader = ({ account, appheader, dispatch, designplatform }) => {
  const { viewStatus } = appheader;
  const { status } = designplatform;


  const clean = () => {
    dispatch({
      type: 'transdebug/cleanDebug',
    });
    dispatch({
      type: 'jobdebug/cleanDebug',
    });
    dispatch({
      type: 'transheader/clearHeader',
    });
    dispatch({
      type: 'jobheader/clearHeader',
    });
  };
  // 退出
  const methodLogout = (e) => {
    e.preventDefault();
    Modal.confirm({
      title: '退出登录提醒',
      content: '您将退出登录，是否继续？',
      onOk: () => {
        clean();
        dispatch({ type: 'account/loginOut', payload: '/login' });
      },
    });
  };

  // 点击用户名
  const handleClickUserInfo = (e) => {
    e.preventDefault();
    dispatch({ type: 'account/showProfile' });
  };

  const handleVersionInfo = (e) => {
    e.preventDefault();
    getVersion().then((res) => {
      const { code } = res.data;
      if (code === "200") {
        Modal.info({
          title: '版本信息',
          content: `${res.data.msg}`,
          iconType: SITE_NAME==="noLogo"?"info-circle":baseInfo.iconType
        });
      }
    });
  };

  return (
    <Row className={Style.Row}>
      <Col span={9} className={Style.ColLeft}>
        {
            STANDALONE_ETL ? <Header className={Style.HeaderLeft} >
              <img className={Style.logoImg} style={SITE_NAME==="noLogo"?{display:"none"}:null} src={baseInfo.logo} />
              {baseInfo.siteName}
            </Header> : <Link to="#home" onClick={clean}>
              <Header className={Style.HeaderLeft} >
                <img className={Style.logoImg} style={SITE_NAME==="noLogo"?{display:"none"}:null} src={baseInfo.logo} />
                {baseInfo.siteName}
              </Header>
            </Link>
          }
      </Col>
      <Col span={15} className={Style.ColRight}>
        <Header className={Style.HeaderRight} >
          <Row className={Style.RowRight}>
            {
                STANDALONE_ETL ? null : <div className={Style.hRight}>

                  <a className={Style.a} href="#" onClick={handleClickUserInfo}>
                    <Icon className={Style.icon} type="user" />
                    <span className={Style.text}>{account.username}</span>
                  </a>
                  <i className={Style.virgule} />
                  <a className={Style.a}><Icon className={Style.icon} onClick={handleVersionInfo} type="question-circle-o" /></a>
                  <i className={Style.virgule} />
                  <a className={Style.a} href="#" onClick={methodLogout}><Icon className={Style.icon} type="poweroff" /></a>
                </div>
              }
          </Row>
        </Header>
        <Content className={Style.ContentRight}>
          <Row className={Style.RowRightContent} type="flex" justify="end" >

            <Col span={5} className={`${Style.ColClildRight} ${Style[viewStatus === 'taskCenter' ? 'taskCenterClick' : 'taskCenter']}`}>
              <Link to={status === 'trans' ? '/gather/taskcenter/transcenter' : '/gather/taskcenter/jobscenter'} >
                <Layout className={Style.ColLayout} >
                  <div className={Style.borderLayout}>
                    <Header className={Style.ColLayoutHeader}>
                      任务中心
                    </Header>
                  </div>
                </Layout>
              </Link>
            </Col>

            <Col span={5} className={`${Style.ColClildRight} ${Style[viewStatus === 'designCenter' ? 'designCenterClick' : 'designCenter']}`}>
              <Link to="/gather/designplatform">
                <Layout className={Style.ColLayout} >
                  <div className={Style.borderLayout}>
                    <Header className={Style.ColLayoutHeader}>
                        设计平台
                      </Header>
                  </div>
                </Layout>
              </Link>
            </Col>
            <Col span={5} className={`${Style.ColClildRight} ${Style[viewStatus === 'resourceCenter' ? 'resourceCenterClick' : 'resourceCenter']}`}>
              <Link to="/gather/resourcelist">
                <Layout className={Style.ColLayout} >
                  <div className={Style.borderLayout}>
                    <Header className={Style.ColLayoutHeader}>
                      资源中心
                    </Header>
                  </div>
                </Layout>
              </Link>
            </Col>
          </Row>
        </Content>
      </Col>
      <UserProfile />
    </Row>
  );
};

export default connect(({ account, appheader, designplatform }) => ({
  account, appheader, designplatform,
}))(AppHeader);

