import React from 'react';
import CommonLayout from '../../components/layout/CommonLayout';

// 菜单配置
const menuConfig = {
  title: '数据资源目录',
  list: [
    {
      name: '数据资源目录',
      path: '/resources/directory',
      icon: 'menu-unfold',
      // empowerApi: '/DataResourceController/getAllResource',
    },
    {
      name: '我的资源',
      path: '/resources/myResource',
      icon: 'appstore-o',
      // empowerApi: '/myResourceController/search',
    },
    {
      name: '我的申请',
      path: '/resources/myApplication',
      icon: 'environment-o',
      // empowerApi: '/myResourceController/getMyApprove',
    },
    {
      name: '待审批',
      path: '/resources/unapproved',
      icon: 'question-circle-o',
      // empowerApi: '/myResourceController/getMyWillApprove',
    },
    {
      name: '已审批',
      path: '/resources/approved',
      icon: 'check-circle-o',
      // empowerApi: '/myResourceController/getMyApproved',
    },
  ],
};

// 输出内容
class Layout extends React.Component {
  render() {
    return (<CommonLayout title="数据资源目录" menuConfig={menuConfig} {...this.props} />);
  }
}
export default Layout;
