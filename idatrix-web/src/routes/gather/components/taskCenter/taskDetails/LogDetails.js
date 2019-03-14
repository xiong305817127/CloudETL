import React ,{PropTypes} from 'react'
import { Modal, Button,Form,Table,Popconfirm } from 'antd';
import { connect } from 'dva';

const LogModel = ({logModel,dispatch})=>{
 const { visible1,logs } =  logModel;


  const handleCancel = ()=>{
    dispatch({
       type:"taskdetails/closeLog"
    })
  };

  return(
    <Modal
      title="执行记录"
      visible={visible1}
      wrapClassName="vertical-center-modal"
      onCancel={handleCancel}
      width={"90%"}
      footer={[
                  <Button key="submit" type="primary" size="large"  onClick={handleCancel} >
                    关闭
                  </Button>
                ]}
    >
        <pre style={{maxHeight:"600px",overflow:"scroll",whiteSpace:"pre-line"}}> {logs?logs:"暂无日志"}</pre>
    </Modal>
  )
};


export default connect()(LogModel)
