import React from "react";
import { connect } from 'dva';
import { Button, Form, Input,Select,Checkbox} from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const Option = Select.Option;

class DELAY extends React.Component {
 hideModal = () => {
    const { dispatch,form } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    })
    form.resetFields();
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
      obj.parallel= values.parallel;
      obj.entryParams = {
        scaleTime:values.scaleTime,
        maximumTimeout:values.maximumTimeout
      };
      saveEntry(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });
    });
  };


  render() {
    const { getFieldDecorator } = this.props.form;
    const { config, text,visible,handleCheckJobName,parallel,nextStepNames } = this.props.model;
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 }
    };
   
  
    return (
       <Modal
        visible={visible}
        title="等待"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        onCancel={this.hideModal}
        footer={[
                  <Button key="submit" type="primary" size="large"  onClick={this.handleCreate.bind(this)} >
                    确定
                  </Button>,
                  <Button key="back" size="large"  onClick={this.hideModal}>取消</Button>,
                ]}
      >
        <Form >
          <FormItem label="作业项名称"  {...formItemLayout} style={{marginBottom:"8px"}}>
            {getFieldDecorator('text', {
              initialValue: text,
              rules: [{ whitespace:true, required: true, message: '请输入作业项名称' },
                {validator:handleCheckJobName,message: '作业项名称已存在，请更改!' }]
            })(
               <Input />
            )}
          </FormItem>
          {nextStepNames.length >= 2 ?(
              <FormItem {...formItemLayout} style={{marginBottom:"8px"}}>
                  {getFieldDecorator('parallel', {
                    valuePropName: 'checked',
                    initialValue: parallel,
                  })(
                    <Checkbox disabled={nextStepNames.length <= 1} style={{left:'9rem'}}>下一步骤并行运行</Checkbox>
                  )}
                </FormItem>
            ):null }
          

           <FormItem label="最大超时"  {...formItemLayout} style={{marginBottom:"8px"}}>
            {getFieldDecorator('maximumTimeout', {
              initialValue: config.maximumTimeout,
            })(
               <Input />
            )}
          </FormItem>

           <FormItem label=""  {...formItemLayout} style={{marginBottom:"8px"}}>
            {getFieldDecorator('scaleTime', {
              initialValue:config.scaleTime+"",
            })(
                <Select  style={{ width: 285 ,marginLeft:"43%"}} >
                  <Option value="0">秒</Option>
                  <Option value="1">分钟</Option>
                  <Option value="2">小时</Option>
               </Select>
            )}
          </FormItem>

        </Form>
      </Modal>
    );
  }
}
const DelayForm = Form.create()(DELAY);
export default connect()(DelayForm);
