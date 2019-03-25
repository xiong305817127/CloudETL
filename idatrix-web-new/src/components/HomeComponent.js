/**
 * 首页组件基类
 * 所有皮肤的首页，都应继承此组件
 */
import React from 'react';
import { ROOT_USER_NAME } from 'constants';
import { fetchPermission } from 'services/login';
import inletsConfig from 'config/homeInlet.config'; // 导入首页入口信息定义
import sysIds from 'config/systemIdsMap.config'; // 导入子系统id映射表
import { deepCopy } from 'utils/utils';
import { getUserSession } from 'utils/session';

// 初始state设置
const initState = {
  inletsConfig: {},
};

class HomeComponent extends React.Component {

  constructor(props) {
    super(props);
    // 处理子组件state
    if (this.state) {
      const oldState = this.state;
      this.state = {
        ...oldState,
        ...initState,
      };
    } else {
      this.state = {
        ...initState,
      };
    }
    // 处理子组件componentWillMount
    if (this.componentWillMount) {
      this.childComponentWillMount = this.componentWillMount;
      this.componentWillMount = () => {
        this.superComponentWillMount();
        this.childComponentWillMount();
      };
    } else {
      this.componentWillMount = this.superComponentWillMount;
    }
    // 处理子组件componentWillReceiveProps
    if (this.componentWillReceiveProps) {
      this.childComponentWillReceiveProps = this.componentWillReceiveProps;
      this.componentWillReceiveProps = (nextProps) => {
        this.superComponentWillReceiveProps(nextProps);
        this.childComponentWillReceiveProps(nextProps);
      };
    } else {
      this.componentWillReceiveProps = this.superComponentWillReceiveProps;
    }
  }

  superComponentWillMount() {
    const { dispatch, system } = this.props;
    const { permits, routesConfig } = system;
    const userVT = getUserSession();
    // 自动加载各子系统授权信息
    Object.keys(sysIds).forEach((key) => {
      const systemId = sysIds[key];
      if (!permits[systemId]) {
        fetchPermission({
          cid: systemId,
          __vt_param__: userVT,
        }).then(res=> {
					const { code,data } = res.data;
					if(code === "200"){
						dispatch({
              type: 'system/injectPermits',
              payload: {
                id: systemId,
                permits: data.permits,
              },
            });
					}
        });
      }
    });

    this.checkPermits(permits, routesConfig);
  }

  superComponentWillReceiveProps(nextProps) {
    const { system } = nextProps;
    const { permits, routesConfig } = system;
    this.checkPermits(permits, routesConfig);
  }

  /**
   * 核验入口授权
   * @param  {object} permits      各子系统授权信息
   * @param  {array}  routesConfig 路由配置信息
   * @return {[type]}              [description]
   */
  checkPermits(permits, routesConfig) {
    const { account: { username, sysList } } = this.props;
    const inlets = deepCopy(inletsConfig);
    Object.keys(inlets).forEach((key) => {
			inlets[key].allow = sysList.some(item => item.id === key); // 核验子系统入口授权

      if (inlets[key].allow && Array.isArray(inlets[key].paths) && permits[key]) { // 有入口授权，核验路由授权
        const hasValidPath = inlets[key].paths.some((path) => {
          if (!/^\/?#/.test(path)) { // 非路由地址，直接通过
            inlets[key].path = path;
            return true;
          }
          const routePath = path.replace(/^\/?#\/?/, '/');
          const found = routesConfig.find(it => it.path === routePath); // 检出路由配置
          if (found && found.empowerApi) { // 该路由需要授权
            const permit = permits[key].find(p => p.url === found.empowerApi); // 检出授权信息
            if (permit && permit.isShow) {
              inlets[key].path = path;
              return true;
            }
          } else { // 不需要授权的路由直接通过
            inlets[key].path = path;
            return true;
          }
        });
        if (!hasValidPath) { // 如果在所有备选paths中均未检出有效path，则禁止进入子系统
          inlets[key].allow = false;
        }
      }
    });
    // root用户进入安全子系统时，入口修改为资源管理页面
    if (username === ROOT_USER_NAME) {
      inlets[sysIds['routes/security']].path = '#/ResourcesManagingTable';
    }
    this.setState({
      inletsConfig: inlets,
    });
  }

}

export default HomeComponent;
