/**
 * Created by Administrator on 2017/7/18.
 */
import React from 'react';
import { Layout} from 'antd';
const { Content,Sider } = Layout;
import Workspace from './TransPlatform/Workspace'
import Workheader from './TransPlatform/Workheader'
import Worktools from './TransPlatform/Worktools'
import Welcome from './TransPlatform/Welcome'
import Style from './Workview.css'
import { connect } from 'dva'

const TransPlatform = ({ model,viewStatus, owner })=>{

	console.log(viewStatus,"视图状态");

  const showModel = ()=>{
    switch (model){
      case "view":
        return(
          <Content className={Style.mainCont}>
            <Workheader  viewStatus={viewStatus}/>
            <Workspace viewStatus={viewStatus}  owner={owner}/>
          </Content>
        );
      default:
        return(
          <Content className={Style.welcome}>
            <Welcome viewStatus={viewStatus}/>
          </Content>
        )
    }
  };

  return(
    <Layout>
      <Sider className={Style.Sider} width={225}>
          <div className={Style.SiderDiv}>
            <Worktools viewStatus={viewStatus}/>
          </div>
      </Sider>
      <Content className={Style.Content}>
          {
            showModel()
          }
      </Content>
    </Layout>
  )
};

export default TransPlatform;
