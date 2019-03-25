import  React  from 'react'
import {  Menu, Icon  } from  'antd'
const { SubMenu } = Menu;



class SiderContentTwo extends React.Component{

  //2.模板方法

  //自定义方法：

  //3.输出组件页面：
  render(){

    return(
      <div>
        <Menu mode="inline" defaultSelectedKeys={['1']} defaultOpenKeys={['sub1']} style={{ height: '100%' }}>
          <SubMenu key="sub1" title={<span><Icon type="home" />组织和机构</span>}>
            <SubMenu key="sub2" title={<span><Icon type="laptop" />组织1</span>}>
              <Menu.Item key="1">部门1.1</Menu.Item>
              <Menu.Item key="2">部门1.2</Menu.Item>
            </SubMenu>
            <SubMenu key="sub3" title={<span><Icon type="laptop" />组织2</span>}>
              <Menu.Item key="3">部门2.1</Menu.Item>
              <Menu.Item key="4">部门2.2</Menu.Item>
            </SubMenu>
            <SubMenu key="sub4" title={<span><Icon type="laptop" />组织3</span>}>
              <Menu.Item key="5">部门A1</Menu.Item>
              <Menu.Item key="6">部门A2</Menu.Item>
              <Menu.Item key="7">部门A3</Menu.Item>
            </SubMenu>
            <Menu.Item key="8"><Icon type="laptop" />互联网</Menu.Item>
          </SubMenu>

        </Menu>
      </div>
    )
  }
}

export default  SiderContentTwo;
