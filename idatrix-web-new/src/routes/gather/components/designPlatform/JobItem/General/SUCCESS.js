import React from "react";
import { connect } from 'dva';
import { Button, Form, Input, Radio} from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;

class SuccessForm extends React.Component {

  hideModal = () => {
    const { dispatch } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    });
  };

  handleCreate(e){
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (err) {
        return;
      }

      const {panel,description,transname,key,saveEntry,text} = this.props.model;

      let obj = {};

      obj.jobName = transname;
      obj.newName = (text === values.text?"":values.text);
      obj.entryName = text;
      obj.type = panel;
      obj.description = description;
      obj.entryParams = {};
      saveEntry(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });
    });
  };


  render() {
    const { getFieldDecorator } = this.props.form;
    const { text,visible,handleCheckJobName } = this.props.model;
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14},
    };

    return (
       <Modal
        visible={visible}
        title="成功"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        onCancel={this.hideModal.bind(this)}
        footer={[
                  <Button key="submit" type="primary" size="large"  onClick={this.handleCreate.bind(this)} >
                    确定
                  </Button>,
                  <Button key="back" size="large"  onClick={this.hideModal.bind(this)}>取消</Button>,
                ]}
      >
        <Form >
          <FormItem label="作业项名称"  {...formItemLayout} style={{marginBottom:"8px"}}>
            {getFieldDecorator('text', {
              initialValue: text,
              rules: [{ whitespace:true, required: true, message: '请输入作业项名称' },
                {validator:handleCheckJobName,message: '作业项名称已存在，请更改!' }]
            })(
               <Input  />
            )}
          </FormItem>
        </Form>
      </Modal>
    );
  }
}
const  SUCCESS= Form.create()(SuccessForm);
export default connect()(SUCCESS);
