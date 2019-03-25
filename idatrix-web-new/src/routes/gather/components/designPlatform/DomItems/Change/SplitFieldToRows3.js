import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Card } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;

class SplitFieldDialog extends React.Component {

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
    const { getFieldDecorator,getFieldValue } = this.props.form;
    const { text,config,visible,handleCheckName } = this.props.model;

    const formItemLayout1 = {
      labelCol: { span: 7 },
      wrapperCol: { span: 14 }
    };


    const setDisabled = ()=>{
      if(getFieldValue("includeRowNumber") === undefined){
        return config.includeRowNumber;
      }else{
        if(getFieldValue("includeRowNumber")){
          return getFieldValue("includeRowNumber");
        }else {
          return false;
        }
      }
    }

    return (

      <Modal
        visible={visible}
        title="列拆分为多行"
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
          <FormItem label="字段名称"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
            {getFieldDecorator('text', {
              initialValue:text,
              rules: [{ required: true, message: '请输入字段名称' },
                {validator:handleCheckName,message: '字段名称已存在，请更改!' }]
            })(
              <Input />
            )}
          </FormItem>
          <FormItem
            {...formItemLayout1}
            label="要拆分的字段"
            hasFeedback
            style={{marginBottom:"8px"}}
          >
            {getFieldDecorator('splitField', {
              initialValue: config.splitField
            })(
              <Select placeholder="请选择字段" >
                {
                  this.state.InputData.map((index)=>
                    <Select.Option  key={index.name} value={index.name}>{index.name}</Select.Option>
                  )
                }
              </Select>
            )}
          </FormItem>
          <FormItem label="分割符"  {...formItemLayout1} style={{margin:"5px 0"}}>
            {getFieldDecorator('delimiter', {
              initialValue: config.delimiter
            })(
              <Input />
            )}
          </FormItem>
          <FormItem  style={{margin:"0px",marginLeft:"25%"}} >
            {getFieldDecorator('delimiterRegex', {
              valuePropName: 'checked',
              initialValue: config.delimiterRegex,
            })(
              <Checkbox>分割符是一个正则表达</Checkbox>
            )}
          </FormItem>
          <FormItem label="新字段名"  {...formItemLayout1} style={{margin:"5px 0"}}>
            {getFieldDecorator('newFieldname', {
              initialValue: config.newFieldname
            })(
              <Input />
            )}
          </FormItem>
          <div style={{margin:"20px 10% 0"}}>
          <Card title="附加字段"  className="CetFileName" style={{ width: "100%",marginBottom:"10px"}}>
            <FormItem  style={{margin:"0px",marginLeft:"25%"}} >
              {getFieldDecorator('includeRowNumber', {
                valuePropName: 'checked',
                initialValue: config.includeRowNumber,
              })(
                <Checkbox>输出中包括行号</Checkbox>
              )}
            </FormItem>
            <FormItem label="行号字段"  {...formItemLayout1} style={{margin:"5px 0"}}>
              {getFieldDecorator('rowNumberField', {
                initialValue: config.rowNumberField
              })(
                <Input disabled={!setDisabled()} />
              )}
            </FormItem>
            <FormItem  style={{margin:"0px 0 20px 25%"}} >
              {getFieldDecorator('resetRowNumber', {
                valuePropName: 'checked',
                initialValue: config.resetRowNumber,
              })(
                <Checkbox  disabled={!setDisabled()} >对接收到的每一行重置行号?</Checkbox>
              )}
            </FormItem>
          </Card>
        </div>
        </Form>
      </Modal>
    );
  }
}
const SplitFieldToRows3 = Form.create()(SplitFieldDialog);

export default connect()(SplitFieldToRows3);
