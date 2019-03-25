import  React  from 'react'
import {  Menu, Icon ,Input,Button } from  'antd'
const { SubMenu } = Menu;

class SiderContentThree extends React.Component{

  //2.模板方法

  //自定义方法：

  //3.输出组件页面：
  render(){

    return(
      <div style={{padding:10}}>
        <header>
          <Input placeholder="模糊搜索" style={{ width: 120 }} />
          <Button>搜索</Button>
        </header>
        <Menu
          mode="inline"
          defaultSelectedKeys={['1']}
          defaultOpenKeys={['sub1']}
          style={{ height: '100%', marginTop:10, border:'none' }}>
          <SubMenu key="sub1" title={<span><Icon type="home" />角色列表</span>}>
            <SubMenu key="sub2" title={<span><Icon type="laptop" />管理员</span>}>
              <Menu.Item key="1">管理员1</Menu.Item>
              <Menu.Item key="2">管理员2</Menu.Item>
            </SubMenu>
            <SubMenu key="sub3" title={<span><Icon type="laptop" />测试人员</span>}>
              <Menu.Item key="3">测试人员1</Menu.Item>
              <Menu.Item key="4">测试人员2</Menu.Item>
            </SubMenu>
            <SubMenu key="sub4" title={<span><Icon type="laptop" />运维监控</span>}>
              <Menu.Item key="5">运维监控1</Menu.Item>
              <Menu.Item key="6">运维监控2</Menu.Item>
              <Menu.Item key="7">运维监控3</Menu.Item>
            </SubMenu>
            <SubMenu key="sub5" title={<span><Icon type="laptop" />分析员</span>}>
              <Menu.Item key="7">分析员1</Menu.Item>
              <Menu.Item key="8">分析员2</Menu.Item>
              <Menu.Item key="9">分析员3</Menu.Item>
            </SubMenu>
          </SubMenu>

        </Menu>
      </div>
    )
  }
}

export default  SiderContentThree;
