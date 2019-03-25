import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;

class GetSlaveDialog extends React.Component {

  constructor(props){
    super(props);
    this.state = {
      serverList: []
    }
  };

  componentDidMount(){
    this.Request();
  };

  Request(){
    const { getServerList } = this.props.model;
    getServerList(data => {
      this.setState({serverList:data })
    })
  };

  hideModal = () => {
    const { dispatch } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    });
  };

  handleCreate = () => {

    const form = this.props.form;
    const { panel,transname,description,key,saveStep,text } = this.props.model;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let obj = {};
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        ...values
      };

      saveStep(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });

    });
  };



  render() {
    const { getFieldDecorator } = this.props.form;
    const { text,config,visible,handleCheckName } = this.props.model;

    const formItemLayout1 = {
      labelCol: { span: 7 },
      wrapperCol: { span: 14 }
    };

    return (

      <Modal
        visible={visible}
        title="Get ID from slave server"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        footer={[
                  <Button key="submit" type="primary" size="large" onClick={this.handleCreate} >
                    确定
                  </Button>,
                  <Button key="back" size="large" onClick={this.hideModal}>取消</Button>,
                ]} onCancel = {this.hideModal}>
        <Form >
          <FormItem label="步骤名称"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
            {getFieldDecorator('text', {
              initialValue:text,
              rules: [{ whitespace:true, required: true, message: '请输入步骤名称' },
                {validator:handleCheckName,message: '步骤名称已存在，请更改!' }]
            })(
              <Input />
            )}
          </FormItem>
          <FormItem label="值名称"  {...formItemLayout1} style={{margin:"5px 0"}}>
            {getFieldDecorator('valuename', {
              initialValue: config.valuename
            })(
              <Input />
            )}
          </FormItem>
          <FormItem
            {...formItemLayout1}
            label="从服务器"
            hasFeedback
            style={{marginBottom:"8px"}}
          >
            {getFieldDecorator('slaveServerName', {
              initialValue: config.slaveServerName==="slave server name"?null:config.slaveServerName
            })(
              <Select placeholder="请选择服务器" >
                {
                  this.state.serverList.map((index)=>
                    <Select.Option  key={index.name} value={index.name}>{index.name}</Select.Option>
                  )
                }
              </Select>
            )}
          </FormItem>
          <FormItem label="序列名称"  {...formItemLayout1} style={{margin:"5px 0"}}>
            {getFieldDecorator('sequenceName', {
              initialValue: config.sequenceName==="Slave Sequence Name -- To be configured"?null:config.sequenceName
            })(
              <Input />
            )}
          </FormItem>
          <FormItem label="增量或批量大小"  {...formItemLayout1} style={{margin:"5px 0"}}>
            {getFieldDecorator('increment', {
              initialValue: config.increment
            })(
              <Input />
            )}
          </FormItem>
        </Form>
      </Modal>
    );
  }
}
const GetSlaveSequence = Form.create()(GetSlaveDialog);

export default connect()(GetSlaveSequence);
