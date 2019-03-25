import React from 'react';
import { connect } from 'dva';
import { withRouter } from 'react-router';
import { Form, Input, Table, Popover, Button, Row, Col, Tabs} from  'antd';
import Flow from './components/Flow';
import Report from './components/Report';
import Empower from '../../../components/Empower';
import Modal from 'components/Modal';

const TabPane = Tabs.TabPane;

class EditTaskManage extends React.Component{

  state = {
    activeKey: '1',
  }

  handleTabChange = (value) => {
    const { dispatch, params: { id } } = this.props;
    if (value === '2') { // 切换到执行统计时，加载统计数据
      dispatch({
        type: 'taskManage/getReport',
        payload: id,
      })
    }
    this.setState({
      activeKey: value,
    });
  }

  render(){
    const { id } = this.props.params;
    return(
      <div style={{ padding: 20, backgroundColor:'#fff',width:"100%", alignItems:"stretch"}}>
        <Tabs defaultActiveKey="1" onChange={this.handleTabChange} style={{ height:"100%"}}>
          <TabPane tab="流程图" key="1" style={{ height:"100%"}}><Flow id={id} route={this.props.route} /></TabPane>
          <TabPane tab="执行统计" key="2" style={{ height:"100%"}}>
            <Empower api="/executor/statTask" disable-type="hide"><Report activeKey={this.state.activeKey} id={id} /></Empower>
          </TabPane>
        </Tabs>
      </div>
    )
  }
}

export default connect()(withRouter(EditTaskManage));
