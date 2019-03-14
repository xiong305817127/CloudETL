/**
 * Created by Administrator on 2017/7/18.
 */
import React from 'react';
import { Layout} from 'antd';
const { Content,Sider } = Layout;
import Workspace from './JobPlatform/Workspace'
import Workheader from './JobPlatform/Workheader'
import Worktools from './JobPlatform/Worktools'
import Welcome from './JobPlatform/Welcome'

import Style from './Workview.css'
import { connect } from 'dva'

const JobPlatform = ({ model,viewStatus })=>{

  const showModel = ()=>{
    switch (model){
      case "view":
        return(
          <Content className={Style.mainCont}>
            <Workheader  viewStatus={viewStatus}/>
            <Workspace viewStatus={viewStatus}/>
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

export default connect()(JobPlatform)
