/**
 * 公共布局组件，内页大部分页面都可使用
 */
import React from 'react';
import { Layout, Breadcrumb } from 'antd';
import { Link } from 'react-router';
import PropTypes from 'prop-types';
import baseInfo from 'config/baseInfo.config';
import HeaderModel from './HeaderModel';
import AsiderModel from './AsiderModel';
import PageLoading from '../PageLoading.js';
import MyIframe from "../../Iframe/index";

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
    mode: 'inline',
  };

  onCollapse = (collapsed) => {
    this.setState({
      collapsed,
      mode: collapsed ? 'vertical' : 'inline',
    });
  };

  render() {
    const { menuConfig, routes, params,location } = this.props;
    let stackPath = '';
    const { noCard } =  menuConfig;
    const { pathname } = location;
    const showCard = noCard?noCard.some(index=>pathname.indexOf(index) !== -1):false;

    const childrenComponent =  getLastChildren(this.props.children)?getLastChildren(this.props.children):<MyIframe url={pathname} height={"805px"} />;

    return (<div id="common-page">
      <Layout style={{height: '100%'}}>
        <Header id="default-header">
          <PageLoading style={{ position: 'absolute', top: 0 }} />
          <HeaderModel title={this.props.title} menu={menuConfig} />
        </Header>
        <Layout>
          <Sider id="common-sidebar" collapsible collapsed={this.state.collapsed} onCollapse={this.onCollapse}>
            <AsiderModel menu={menuConfig} />
          </Sider>
          <Layout id="common-main-wrapper">
            <Breadcrumb>
              {routes.map((item, index) => {
                stackPath += pattAbsPath.test(item.path) ? item.path : `/${item.path}`;
                if (index === 0 || index === routes.length - 1) {
                  return <Breadcrumb.Item key={index}>{item.breadcrumbName}</Breadcrumb.Item>
                }
                return <Breadcrumb.Item key={index}>
                  <Link to={createPath(pattAbsPath.test(item.path) ? item.path : stackPath, params)}>{item.breadcrumbName}</Link>
                </Breadcrumb.Item>
              })}
            </Breadcrumb>
            <Content id="common-main-content" style={{display:showCard?"flex":"block"}}>   
                {childrenComponent}
            </Content>
            <Footer id="common-main-footer">{baseInfo.copyright}</Footer>
          </Layout>
        </Layout>
      </Layout>
    </div>);
  }
}

export default Index;
