import React from 'react';
import { connect } from 'dva';
import CommonLayout from '../../components/layout/CommonLayout';
import { ROOT_USER_NAME } from '../../constants';

// 菜单配置
const menuConfig = {
  title: '安全管理',
  list: [
    {
      name: '资源管理',
      path: '/ResourcesManagingTable',
      icon: 'appstore-o',
    },
    {
      name: '租户管理',
      path: '/security/TenantManagementTable',
      icon: 'solution',
    },
    {
      name: '组织机构管理',
      path: '/OrganizationManagementTable',
      icon: 'global',
    },
    {
      name: '角色管理',
      path: '/RoleManagementTable',
      icon: 'team',
    },
    {
      name: '用户管理',
      path: '/UserManagementTable',
      icon: 'user',
    },
    {
      name: '脱敏规则管理',
      path: '/DesensitizationRuleTable',
      icon: 'eye',
    },
	{
      name: '日志管理',
      path: '/LogoManagement',
      icon: 'file-text',
    },
  ],
};

// 输出内容
class Layout extends React.Component {
  state = {
    menuConfig: [],
  }

  componentWillMount() {
    this.mergeProps(this.props);
  }

  componentWillReceiveProps(nextProps) {
    this.mergeProps(nextProps);
  }

  mergeProps(props) {
    const { account } = props;
    const newMenuConfig = {};
    if (account.username === ROOT_USER_NAME) {
      newMenuConfig.list = menuConfig.list.filter(item => item.name === '租户管理'
        || item.name === '资源管理');
    } else {
      newMenuConfig.list = menuConfig.list.filter(item => item.name !== '租户管理'
        && item.name !== '资源管理');
    }
    this.setState({
      menuConfig: newMenuConfig,
    });
  }

  render() {
    return (<CommonLayout title="安全管理" menuConfig={this.state.menuConfig} {...this.props} />);
  }
}
export default connect(({ account }) => ({
  account,
}))(Layout);
