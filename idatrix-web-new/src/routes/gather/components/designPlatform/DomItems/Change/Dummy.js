import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col,Card,Icon } from 'antd';
import Modal from "components/Modal.js";
const Option = Select.Option;
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
const confirm = Modal.confirm;
import EditTable from '../../../common/EditTable'

class DummyRows extends React.Component {

  constructor(props){
    super(props);
    const { visible } = props.model;
    this.state={
       visibleS:false,
    };
  }

 hideModal = () => {
    const { dispatch } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    });
  };

  handleCreate = () => {
    const form = this.props.form;
    const { panel,transname,description,key,saveStep,text,config,formatTable } = this.props.model;
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
        ...values,
      };
      saveStep(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });
    });
  }

  render() {
    const { getFieldDecorator } = this.props.form;
    const { text,visible,handleCheckName } = this.props.model;
    const formItemLayout1 = { 
      labelCol: { span: 7 },
      wrapperCol: { span: 14 },
    };

    return (
      <Modal
        visible={visible}
        title="空操作"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        width={650}
        footer={[
                  <Button key="submit" type="primary" size="large" onClick={this.handleCreate.bind(this)} >
                    确定
                  </Button>,
                  <Button key="back" size="large" onClick={this.hideModal.bind(this)}>取消</Button>,
                ]}
        onCancel = {this.hideModal}>
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
        </Form>
      </Modal>
    );
  }
}
const Dummy = Form.create()(DummyRows);

export default connect()(Dummy);
