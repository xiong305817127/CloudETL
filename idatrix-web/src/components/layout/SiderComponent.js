/**
 * 侧边栏导航公共组件，所有皮肤都应从此组件导入侧边栏
 */
import React from 'react';
import { connect } from 'dva';
import { Menu, Icon } from 'antd';
import { Link } from 'react-router';
import PropTypes from 'prop-types';
import { STANDALONE_ETL } from 'constants';

const { SubMenu } = Menu;

/**
 *
 * @param list  需要自动展现的路由列表
 * @param routesConfig
 * @param permitsList
 */
function getAutoShowList(list, routesConfig, permitsList) {
  let key = '';
  let autoList = list;
  while (autoList.length > 0 && !key) {

    if(!STANDALONE_ETL){
      const found = routesConfig.find(it => it.path === autoList[0]);
      if (found && found.empowerApi) {
        const permit = permitsList.find(p => p.url === found.empowerApi);
        if (!permit || !permit.isShow) {
          autoList.splice(0, 1);
        } else {
          key = autoList[0];
        }
      } else {
        key = autoList[0];
      }
    }else{
      key = autoList[0];
    }
  }

  if (key) {
     window.location.href = '#' + key;
  }

  return key;
}

// 获取当前菜单key值
function getSelectedKeys(path, menu, permitsList, routesConfig, oldKey = '') {
  const { list, autoShowList, needAutoUrl } = menu;

  let key = oldKey;
  list.forEach(item => {
    if (Array.isArray(item.list)) {
      key = getSelectedKeys(path, item, permitsList, routesConfig, key);
    } else if (path.indexOf(item.path) === 0 && item.path.length > key.length) {
      key = item.path;
    }
  });

  // needAutoUrl  匹配该路由,才进行自动选择
  if (autoShowList && autoShowList.length > 0 && path === needAutoUrl) {
    key = getAutoShowList(autoShowList, routesConfig, permitsList);
  }

  return key;
}

class SiderComponent extends React.Component {

  rootSubmenuKeys = [];

  state = {
    selectedKey: '',
    openKeys:[],
    permitsList: [],
  };

  componentWillMount() {
    this.updateStateByProps(this.props);
  }

  componentWillReceiveProps(nextProps) {
    console.log();
    this.updateStateByProps(nextProps);
  }

  
  onOpenChange = (openKeys) => {
    console.log(openKeys);

    const latestOpenKey = openKeys.find(key => this.state.openKeys.indexOf(key) === -1);

    console.log(latestOpenKey);
    console.log(this.rootSubmenuKeys,"数字");
    if (this.rootSubmenuKeys.indexOf(latestOpenKey) === -1) {
      this.setState({ openKeys });
    } else {
      this.setState({
        openKeys: latestOpenKey ? [latestOpenKey] : [],
      });
    }
  }


/*  componentDidUpdate(prevProps, prevState) {
    // 修复当收起菜单状态下点击菜单后再展开显示不全的bug
    const menuDom = ReactDOM.findDOMNode(this.refs.menu);
    menuDom.className = menuDom.className.replace('ant-menu-inline-collapsed', '');
  }*/

  updateStateByProps(props) {
    const { system, menu } = props;
    const { routesConfig } = system;
    const permitsList = system.permits[system.currentSystemId] || [];

    this.rootSubmenuKeys.splice(0);
    menu.list.map(index=>{
      this.rootSubmenuKeys.push(index.path)
    })

    const selectedKey = getSelectedKeys(system.pathname, menu, permitsList, routesConfig);
    this.setState({
      selectedKey,
      permitsList
    });
  }

  /**
   * 根据菜单列表生成菜单
   * @param  {array}  list 菜单列表
   * @return {object}      返回Menu
   */
  createMenu(list) {
    const { permitsList } = this.state;
    const { routesConfig } = this.props.system;
    return list.map((item, index) => {
      if (Array.isArray(item.list)) {
        const children = this.createMenu(item.list).filter(child => child !== null);
        return children.length > 0 ? (
          <SubMenu
            key={item.path}
            title={<span><Icon type={item.icon} /><span>{item.title}</span></span>}
          >
            {children}
          </SubMenu>
        ) : null;
      } else {
        // 如果菜单配套路由配置了权限，则根据权限信息决定是否显示
        if(!STANDALONE_ETL){
          const found = routesConfig.find(it => it.path === item.path);
          if (found && found.empowerApi) {
            const permit = permitsList.find(p => p.url === found.empowerApi);
            if (!permit || !permit.isShow) {
              return null;
            } else {
              return (<Menu.Item key={item.path}>
                <Link to={item.path}>
                  <Icon type={item.icon} />
                  <span className="nav-text">{permit.name}</span>
                </Link>
              </Menu.Item>);
            }
          }
        }

        return (
          <Menu.Item key={item.path}>
            <Link to={item.path}>
              <Icon type={item.icon} />
              <span className="nav-text">{item.name}</span>
            </Link>
          </Menu.Item>
        );
      }
    });
  }

  render() {
    const { menu } = this.props;
    const { selectedKey,openKeys } = this.state;

    console.log(menu,'菜单');
    
    return (
      <Menu mode="inline" 
        openKeys={openKeys}
        onOpenChange={this.onOpenChange}
        selectedKeys={[selectedKey]} 
        ref="menu" 
        inlineCollapsed={this.props.collapsed}
      >
        {this.createMenu(menu.list)}
      </Menu>
    );
  }
}

SiderComponent.propTypes = {
  // 菜单配置
  menu: PropTypes.object.isRequired,
};

export default connect(({ system }) => ({
  system,
}))(SiderComponent);
