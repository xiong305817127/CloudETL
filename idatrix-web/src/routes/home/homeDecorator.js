/**
 * 首页专用decorator
 */

import React from 'react';
import { API_BASE_MONITOR } from 'constants';
import base64 from 'base64-utf8';
import request from 'utils/request';
import { CLUSTER_USER, CLUSTER_PWD } from 'config/cluster.config';
import {hashHistory} from "dva/router";

/**
 * 子系统入口修饰器
 * 将会向组件注入以下属性
 * @property {function}  openSystem 进入子系统方法
 */
export const inletDecorator = Component => class extends React.Component {

  async openSystem(inlet) {
    if ((inlet || '').indexOf('/uniom') === 0) { // 如果是运营监控系统，先登录系统
      const basic = base64.encode(`${CLUSTER_USER}:${CLUSTER_PWD}`);
      await request(`${API_BASE_MONITOR}/clusters`, {
        headers: {
          'Authorization': `Basic ${basic}`,
          'X-Requested-By': 'ambari',
        },
      });
      // hashHistory.push(inlet.substr(1));
      window.location.href = inlet;
    } else {
      // window.location.href = inlet;
      // document.getElementById('root').innerHTML = '';
      // window.location.reload();
      console.log(inlet,"路由");

      hashHistory.push(inlet ? inlet.substr(1) : "/");

    }
  }

  render() {
    return (
      <Component
        {...this.props}
        openSystem={this.openSystem.bind(this)}
      />
    );
  }
};
