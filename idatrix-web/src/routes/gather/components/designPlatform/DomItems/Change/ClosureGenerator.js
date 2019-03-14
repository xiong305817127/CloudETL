import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;

class ClosurDialog extends React.Component {

  constructor(props){
    super(props);

    this.state = {
      InputData: []
    }
  }

  componentDidMount(){
    this.Request();
  }

  Request(){
    const { getInputFields,transname,text } = this.props.model;
    let obj = {};
    obj.transname = transname;
    obj.stepname = text;
    getInputFields(obj, data => {
      this.setState({
        InputData:data
      })
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
      wrapperCol: { span: 14 },
    };

    return (

      <Modal
        visible={visible}
        title="Closure Generator"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        footer={[
                  <Button key="submit" type="primary" size="large" onClick={this.handleCreate} >
                    确定
                  </Button>,
                  <Button key="back" size="large" onClick={this.hideModal}>取消</Button>,
                ]}
        onCancel = {this.hideModal}
      >
        <Form >
          <FormItem label="步骤名称"    {...formItemLayout1}>
            {getFieldDecorator('text', {
              initialValue:text,
              rules: [{ whitespace:true, required: true, message: '请输入步骤名称' },
                {validator:handleCheckName,message: '步骤名称已存在，请更改!' }]
            })(
              <Input />
            )}
          </FormItem>
          <FormItem
            {...formItemLayout1}
            label="父ID字段名"
            hasFeedback
            style={{marginBottom:"8px"}}
          >
              {getFieldDecorator('parentIdFieldName', {
                initialValue: config.parentIdFieldName
              })(
                <Select>
                  {
                    this.state.InputData.map((index)=>
                      <Select.Option  key={index.name} value={index.name}>{index.name}</Select.Option>
                    )
                  }
                </Select>
              )}
          </FormItem>
          <FormItem
            {...formItemLayout1}
            label="子ID字段名"
            hasFeedback
            style={{marginBottom:"8px"}}
          >
              {getFieldDecorator('childIdFieldName', {
                initialValue: config.childIdFieldName
              })(
                <Select  >
                  {
                    this.state.InputData.map((index)=>
                      <Select.Option  key={index.name} value={index.name}>{index.name}</Select.Option>
                    )
                  }
                </Select>
              )}

          </FormItem>
          <FormItem label="距离字段名"  {...formItemLayout1} style={{margin:"5px 0"}}>
            {getFieldDecorator('distanceFieldName', {
              initialValue: config.distanceFieldName
            })(

              <Input />
            )}
          </FormItem>
          <FormItem  style={{margin:"0px",marginLeft:"25%"}} >
            {getFieldDecorator('rootIdZero', {
              valuePropName: 'checked',
              initialValue: config.rootIdZero,
            })(
              <Checkbox>根是零（整数）？</Checkbox>
            )}
          </FormItem>
        </Form>
      </Modal>
    );
  }
}
const ClosureGenerator = Form.create()(ClosurDialog);

export default connect()(ClosureGenerator);
