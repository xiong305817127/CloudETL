/**
 * 公共布局组件，内页大部分页面都可使用
 */
import React from 'react';
import { Layout, Card,Breadcrumb } from 'antd';
import PropTypes from 'prop-types';
import baseInfo from 'config/baseInfo.config';
import HeaderModel from './HeaderModel';
import AsiderModel from './AsiderModel';
import PageLoading from '../PageLoading.js';
import MyIframe from "../../Iframe/index";

import Style from './Style.less';

const { Header, Content, Sider, Footer } = Layout;
const pattAbsPath = /^\//; // 匹配绝对路径正则表达式

function getLastChildren(child) {
  if (child !== null && child.props.children !== null) {
    return getLastChildren(child.props.children);
  }
  return child;
}

/**
 * 创建路径
 * @param  {string} path   原始路径
 * @param  {object} params 路由参数
 * @return {string}        配置后的路径
 */
function createPath(path, params) {
  let newPath = path;
  Object.keys(params).forEach((key) => {
    const patt = new RegExp(`:${key}`, 'g');
    newPath = newPath.replace(patt, params[key]);
  });
  return newPath;
}

// 输出内容
class Index extends React.Component {

  static propTypes = {
    // 菜单配置
    menuConfig: PropTypes.object.isRequired,
  };

  state = {
    collapsed: false,
    mode: 'inline'
  };

  onCollapse = (collapsed) => {
    this.setState({
      collapsed,
      mode: collapsed ? 'vertical' : 'inline',
    });
  };

  render() {
    const { menuConfig, routes, title, className,params,location } = this.props;
     let stackPath = '';
     const { noCard } =  menuConfig;
     const { pathname } = location;

     const showCard = noCard?noCard.some(index=>pathname.indexOf(index) !== -1):false;

    console.log(this.props.routes,"路由状况");
    console.log(this.props);
    const childrenComponent =  getLastChildren(this.props.children)?getLastChildren(this.props.children):<MyIframe url={pathname} />;


    const handleGetTitle = ()=>{
      return ( 
        <Breadcrumb>
              {routes.map((item, index) => {
                stackPath += pattAbsPath.test(item.path) ? item.path : `/${item.path}`;
                if (index === 0 || index === routes.length - 1) {
                  return <Breadcrumb.Item key={index}>{item.breadcrumbName}</Breadcrumb.Item>
                }
                return <Breadcrumb.Item key={index}>
                {item.breadcrumbName}
                </Breadcrumb.Item>
              })}
            </Breadcrumb>
        )
    }

    return (<div id="common-page" className={className}>
      <Layout style={{ height: '100%' }}>
        <Header id="gov-header">
          <PageLoading style={{ position: 'absolute', top: 0 }} />
          <HeaderModel title={this.props.title} menu={menuConfig}  getPlatformInfo={this.props.getPlatformInfo}/>
        </Header>
        <Layout>
          <Sider id="common-sidebar" collapsible collapsed={this.state.collapsed} onCollapse={this.onCollapse}>
            <AsiderModel menu={menuConfig}  collapsed={this.state.collapsed} />
          </Sider>
          <Layout id="common-main-wrapper">
            <Content id="common-main-content" className={Style.cardWrap}>
              {
                showCard?(
                  <div className={Style.noCard}>
                    <div className={Style.cardTitle}>{handleGetTitle()}</div>
                    <div className={Style.cardContent} >
                        {childrenComponent}
                    </div>
                  </div>
                ):(
                  <Card title={handleGetTitle()}>
                    {childrenComponent}
                  </Card>
                )
              }
            </Content>
            <Footer id="common-main-footer">{baseInfo.copyright}</Footer>
          </Layout>
        </Layout>
      </Layout>
    </div>);
  }
}

export default Index;


/*
  <div className={Style.cardTitle}>{handleGetTitle()}</div>
  <div className={Style.cardContent} >
      {getLastChildren(this.props.children)}
  </div>

*/