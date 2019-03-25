import  React  from 'react'
import {  Menu, Icon  } from  'antd'
const { SubMenu } = Menu;


class SiderContent extends React.Component{

  //2.模板方法
  handleClick = (e) => {
    console.log('click ', e);
  }
  //自定义方法：

  //3.输出组件页面：
  render(){

    return(
      <div>
        <Menu mode="inline" defaultSelectedKeys={['1']} defaultOpenKeys={['sub1']} style={{ height: '100%' }} onClick={this.handleClick}>
          <SubMenu key="sub1" title={<span><Icon type="home" />子系统</span>}>

            <Menu.Item key="1"><Icon type="laptop" />首页</Menu.Item>

            <SubMenu key="sub2" title={<span><Icon type="laptop" />数据资源展现</span>}>
              <Menu.Item key="2"><Icon type="user" />数据资源全景图</Menu.Item>
              <Menu.Item key="3"><Icon type="user" />数据资源权限管理</Menu.Item>
            </SubMenu>

            <SubMenu key="sub3" title={<span><Icon type="laptop" />元数据管理</span>}>
              <Menu.Item key="4"><Icon type="user" />组织机构管理</Menu.Item>
              <Menu.Item key="5"><Icon type="user" />前置机资源管理</Menu.Item>
              <Menu.Item key="6"><Icon type="user" />数据系统注册</Menu.Item>
              <Menu.Item key="7"><Icon type="user" />元数据定义及授权</Menu.Item>
              <Menu.Item key="8"><Icon type="user" />数据关系管理</Menu.Item>
              <Menu.Item key="9"><Icon type="user" />元数据分析</Menu.Item>
              <Menu.Item key="10"><Icon type="user" />数据标准查看</Menu.Item>
            </SubMenu>

            <Menu.Item key="11"><Icon type="laptop" />数据采集&集成</Menu.Item>

            <SubMenu key="sub4" title={<span><Icon type="laptop" />数据分析&探索</span>}>
              <SubMenu key="sub5" title={<span><Icon type="laptop" />查询分析</span>}>
                <Menu.Item key="12"><Icon type="user" />数据查询</Menu.Item>
                <Menu.Item key="13"><Icon type="user" />统计分析</Menu.Item>
              </SubMenu>
              <SubMenu key="sub6" title={<span><Icon type="laptop" />任务调度</span>}>
                <Menu.Item key="14"><Icon type="user" />任务管理</Menu.Item>
                <Menu.Item key="15"><Icon type="user" />调度管理</Menu.Item>
              </SubMenu>
            </SubMenu>

            <SubMenu key="sub7" title={<span><Icon type="laptop" />服务开放&治理</span>}>
              <SubMenu key="sub8" title={<span><Icon type="laptop" />应用中心</span>}>
                <Menu.Item key="16"><Icon type="user" />我的应用</Menu.Item>
                <Menu.Item key="17"><Icon type="user" />服务授权</Menu.Item>
              </SubMenu>
              <Menu.Item key="18"><Icon type="user" />服务中心</Menu.Item>
            </SubMenu>

            <SubMenu key="sub9" title={<span><Icon type="laptop" />安全管理</span>}>
              <Menu.Item key="19"><Icon type="user" />用户管理</Menu.Item>
              <Menu.Item key="20"><Icon type="user" />角色管理</Menu.Item>
              <Menu.Item key="21"><Icon type="user" />资源管理</Menu.Item>
              <SubMenu key="sub10" title={<span><Icon type="laptop" />授权管理</span>}>
                <Menu.Item key="22"><Icon type="user" />菜单操作权限</Menu.Item>
                <Menu.Item key="23"><Icon type="user" />数据权限</Menu.Item>
              </SubMenu>
            </SubMenu>

            <Menu.Item key="24"><Icon type="laptop" />运维管理</Menu.Item>
            <Menu.Item key="25"><Icon type="laptop" />监控管理</Menu.Item>


          </SubMenu>

        </Menu>
      </div>
    )
  }
}

export default  SiderContent;
