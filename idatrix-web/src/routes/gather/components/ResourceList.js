/**
 * Created by Administrator on 2017/6/30.
 */
import React from 'react';
import {Layout} from 'antd';
const { Sider,Content} = Layout;
import AsiderModel from "../../../components/layout/default/AsiderModel"
import Style from './ResourceList.css'


class  ResourceList extends React.Component{

  state = {
    collapsed: false,
    mode: 'inline'
  };

  onCollapse = (collapsed) => {
    this.setState({
      collapsed,
      mode: collapsed ? 'vertical' : 'inline',
    });
  };


  render(){

    const menuConfig = {
      list: [
        {
          name: '数据系统',
          path: '/gather/resourcelist/DataSystem',
          icon: 'database',
          // empowerApi: '/DataResourceController/getAllResource',
        },
        {
          name: '服务器',
          path: '/gather/resourcelist/Server',
          icon: 'desktop',
          // empowerApi: '/myResourceController/search',
        },
        {
          name: '服务器集群',
          path: '/gather/resourcelist/Cluster',
          icon: 'appstore-o',
          // empowerApi: '/myResourceController/getMyApprove',
        },
        {
          name: 'Hadoop集群',
          path: '/gather/resourcelist/HadoopCluster',
          icon: 'exception',
          // empowerApi: '/myResourceController/getMyWillApprove',
        },
        {
          name: 'Spark引擎',
          path: '/gather/resourcelist/SparkEngine',
          icon: 'star-o'
          // empowerApi: '/myResourceController/getMyApproved',
        },
        {
          name: '执行引擎',
          path: '/gather/resourcelist/ExecutionEngine',
          icon: 'trademark'
          // empowerApi: '/myResourceController/getMyApproved',
        },
        {
          name: '文件管理',
          path: '/gather/resourcelist/FileSystem',
          icon: 'folder'
          // empowerApi: '/myResourceController/getMyApproved',
        }
      ],
      needAutoUrl:"/gather/resourcelist",
      autoShowList:[
        '/gather/resourcelist/DataSystem',
        '/gather/resourcelist/Server',
        '/gather/resourcelist/Cluster',
        '/gather/resourcelist/SparkEngine',
        '/gather/resourcelist/ExecutionEngine',
        '/gather/resourcelist/FileSystem'
      ]
    };

    return(
      <Layout id="ResourceList">
        <Sider id="common-sidebar" collapsible collapsed={this.state.collapsed} onCollapse={this.onCollapse}>
          <AsiderModel menu={menuConfig} />
        </Sider>
        <Content className={Style.Content}>
          {this.props.children}
        </Content>
      </Layout>
    )
  }
}


export default ResourceList;
