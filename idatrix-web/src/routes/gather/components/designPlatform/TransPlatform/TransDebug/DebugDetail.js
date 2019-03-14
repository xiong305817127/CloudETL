/**
 * Created by Administrator on 2017/3/13.
 */
import React from 'react'
import { Modal, Tabs } from 'antd';
import { connect } from 'dva';
import LogInfo from './LogInfo';
import StepInfo from './StepInfo';
import PreViewList from './PreViewList';

const TabPane = Tabs.TabPane;

class DebugDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      dispatch: props.dispatch,
    }
  }

  setModelHide() {
    this.state.dispatch({
      type: "debugdetail/changeView",
      visible: false
    })
  }

  callback(key) {
    return false;
  }


  render() {
    const { visible } = this.props.debugdetail;
    const { DebugPreviewDataList } = this.props.infolog;

    return (
      <Modal
        visible={visible}
        title="执行信息"
        wrapClassName="vertical-center-modal"
        onCancel={this.setModelHide.bind(this)}
        maskClosable={false}
        width={1000}
        footer={null}
      >
        <div id="debugDetail">
          <Tabs onChange={(key) => { this.callback(key) }} animated={false} type="card" >
            <TabPane tab="日志" key="infoLog">
              <LogInfo styleClass="tabDiv1" />
            </TabPane>
            <TabPane tab="步骤度量" key="stepMeasure">
              <StepInfo styleClass="tabDiv1" />
            </TabPane>
            {
              DebugPreviewDataList.size > 0 ? (
                <TabPane tab="预览数据" key="predata">
                  <PreViewList data={DebugPreviewDataList} />
                </TabPane>
              ) : null
            }
          </Tabs>
        </div>
      </Modal>
    )
  }
}


export default connect(({ debugdetail, infolog }) => ({
  debugdetail, infolog
}))(DebugDetail)

