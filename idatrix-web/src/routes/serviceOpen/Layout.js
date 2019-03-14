import React from 'react';
import CommonLayout from '../../components/layout/CommonLayout';

// 菜单配置
const menuConfig = {
  title: '服务开放&治理',
  list: [
    {
      name: '我的应用',
      path: '/MyAppTable',
      icon: 'inbox',
    },
    /*{
      name: '服务授权',
      path: '/ServiceTableAuthor',
      icon: 'key',
    },*/
    {
      name: '数据访问API',
      path: '/service/ServiceTableVisitApi',
      icon: 'api',
    },
  ],
};

// 输出内容
class Layout extends React.Component {
  render() {
    return (<CommonLayout title="服务开放&治理" menuConfig={menuConfig} {...this.props} />);
  }
}
export default Layout;
