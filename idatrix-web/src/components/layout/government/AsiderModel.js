/**
 * 侧边栏导航组件
 */
import React from 'react';

import SiderComponent from '../SiderComponent';
import Style from './AsiderModel.less';

class AsiderModel extends React.Component {

  render() {
    return <SiderComponent menu={this.props.menu} collapsed={this.props.collapsed}/>;
  }
}

export default AsiderModel;
