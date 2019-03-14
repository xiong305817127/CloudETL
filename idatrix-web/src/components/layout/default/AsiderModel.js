/**
 * 侧边栏导航组件
 */
import React from 'react';

import SiderComponent from '../SiderComponent';
import Style from './AsiderModel.css';

class AsiderModel extends React.Component {

  render() {
    return <SiderComponent menu={this.props.menu} />;
  }
}

export default AsiderModel;
