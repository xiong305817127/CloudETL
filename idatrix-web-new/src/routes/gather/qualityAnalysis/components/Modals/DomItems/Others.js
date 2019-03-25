/**
 * Created by Administrator on 2017/3/13.
 */
import React from 'react';
import { Button, Modal, Form, Alert, Select } from 'antd';
import { connect } from 'dva';

class Tip1 extends React.Component {

  setModelHide() {
    const { dispatch } = this.props;
    dispatch({
      type: 'domItems/hide',
      visible: false
    })
  };

  handleCancel() {
    const { dispatch } = this.props;
    dispatch({
      type: 'domItems/hide',
      visible: false
    })
  };


  render() {
    const { visible } = this.props;

    return (
      <Modal
        visible={visible}
        title="温馨提醒"
        wrapClassName="vertical-center-modal"
        okText="Create"
        style={{ zIndex: 50 }}
        maskClosable={false}
        footer={[
          <Button key="submit" type="primary" size="large" onClick={() => { this.setModelHide() }}>
            确定
                  </Button>,
          <Button key="back" size="large" onClick={() => { this.handleCancel(); }}>取消</Button>,
        ]}
      >
        <Alert
          description="组件未实现，暂不提供编辑功能。"
          type="info"
          showIcon
        />
      </Modal>
    )
  }
}


const Others = Form.create()(Tip1);
export default connect()(Others);
