/**
 * Created by Administrator on 2017/3/13.
 */
import React from 'react'
import { Button, Modal, Form,Tabs} from 'antd';
const TabPane = Tabs.TabPane;
import { connect } from 'dva';
import LogInfo from './LogInfo';
import StepInfo from './StepInfo';

class DebugDetail extends React.Component{
  constructor(props){
    super(props);
    this.state = {
      dispatch:props.dispatch,
    }
  }

  setModelHide(){
     this.state.dispatch({
      type:"debugdetail/changeView",
      visible:false
    })
  }

  callback(key){
    return false;
  }


  render(){
    const { visible } = this.props.debugdetail;

    return (
      <Modal
        visible={visible}
        title="执行信息"
        wrapClassName="vertical-center-modal"
        onCancel={this.setModelHide.bind(this)}
        maskClosable={false}
        width={1200}
        footer={null}
      >
        <div id="debugDetail">
        <Tabs onChange={(key)=>{this.callback(key)}} animated={false} type="card" >
          <TabPane tab="日志" key="infoLog">
            <LogInfo  styleClass="tabDiv1"/>
          </TabPane>
          <TabPane tab="步骤度量" key="stepMeasure">
            <StepInfo  styleClass="tabDiv1"/>
          </TabPane>
        </Tabs>
        </div>
      </Modal>
    )
  }
}


export default connect(({ debugdetail }) => ({
  debugdetail
}))(DebugDetail)

