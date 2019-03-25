/**
 * Created by Administrator on 2017/3/13.
 */
import React from 'react';
import { Button, Modal,Alert} from 'antd';
import { connect } from 'dva';

class Unknown extends React.Component{

  setModelHide(){
    const { dispatch } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    })
  };

  handleCancel(){
    const { dispatch } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    })
  };


  render(){
    
    
    
    
    const { visible } = this.props.model;

    return(
      <Modal
        visible={visible}
        title="温馨提醒"
        wrapClassName="vertical-center-modal"
        okText="Create"
        style={{zIndex:50}}
        maskClosable={false}
        footer={[
                  <Button key="submit" type="primary" size="large"  onClick={()=>{this.setModelHide()}}>
                    确定
                  </Button>,
                  <Button key="back" size="large" onClick={()=>{this.handleCancel();}}>取消</Button>,
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

export default connect()(Unknown);
