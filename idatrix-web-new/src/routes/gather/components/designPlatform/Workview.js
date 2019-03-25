import React from 'react';
import { Layout} from 'antd';
const { Content } = Layout;
import Workspace from 'TransPlatform/Workspace'
import Workheader from 'TransPlatform/Workheader'
import Welcome from 'TransPlatform/Welcome'
import Style from './Workview.css'
import { connect } from 'dva'


class Workview extends React.Component{
  constructor(props){
    super(props);
    this.state = {
      dispatch:props.dispatch
    }
  }

  render(){

    const { model } = this.props.workview;
    console.log(model);
    switch (model){
      case "view":
        return(
          <Content className={Style.mainCont}>
            <Workheader  />
            <Workspace />
          </Content>
        )
      default:
        return(
          <Content className={Style.welcome}>
            <Welcome />
          </Content>
        )
    }
  }
}


export default connect(({ workview }) => ({
  workview
}))(Workview)
