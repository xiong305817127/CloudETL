import React from "react";
import { connect } from 'dva';
import { Button, Form, Input, Radio,Select,Tabs,Checkbox,Row,Col} from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const Option = Select.Option;

class SIMPLE_EVAL extends React.Component {

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
      obj.parallel= values.parallel;
      obj.entryParams = {
        ...values
      };
      saveEntry(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });
    });
  };

  showModel(value){
    const { getFieldDecorator } = this.props.form;
    const { config } = this.props.model;
        if(value === "0"){
            return(
              <FormItem label="字段名"  {...this.formItemLayout1} style={{marginBottom:"8px"}}>
                {getFieldDecorator('fieldname', {
                  initialValue: config.fieldname,
                })(
                  <Input />
                )}
              </FormItem>
            )
        }else{
            return(
              <FormItem label="变量名"  {...this.formItemLayout1} style={{marginBottom:"8px"}}>
                {getFieldDecorator('variablename', {
                  initialValue: config.variablename,
                })(
                  <Input />
                )}
              </FormItem>
            )
        }
  }

  showModel1(value){
    const { getFieldDecorator } = this.props.form;
    const { config } = this.props.model;

      if(value === "2"){
          return(
            <FormItem label="掩码"  {...this.formItemLayout1} style={{marginBottom:"8px"}}>
              {getFieldDecorator('mask', {
                initialValue: config.mask,
              })(
                <Select   >
                  <Option value="0">yyyy/MM/dd HH\:mm\:ss.SSS</Option>
                  <Option value="1">yyyy/MM/dd HH\:mm\:ss.SSS XXX</Option>
                  <Option value="2">yyyy/MM/dd HH\:mm\:ss</Option>
                  <Option value="3">yyyy/MM/dd HH\:mm\:ss XXX</Option>
                  <Option value="4">yyyyMMddHHmmss</Option>
                  <Option value="5">yyyy/MM/dd</Option>
                  <Option value="6">yyyy-MM-dd HH\:mm\:ss</Option>
                  <Option value="7">yyyy-MM-dd HH\:mm\:ss XXX</Option>
                  <Option value="8">yyyyMMdd</Option>
                  <Option value="9">MM/dd/yyyy</Option>
                  <Option value="10">MM/dd/yyyy HH\:mm\:ss</Option>
                  <Option value="11">MM-dd-yyyy</Option>
                  <Option value="12">MM-dd-yyyy</Option>
                  <Option value="13">MM-dd-yyyy HH\:mm\:ss</Option>
                  <Option value="14">MM/dd/yy</Option>
                  <Option value="15">dd/MM/yyyy</Option>
                  <Option value="16">dd-MM-yyyy</Option>
                  <Option value="17">yyyy-MM-dd''T''HH\:mm\:ss.SSSXXX</Option>
                </Select>
              )}
            </FormItem>
          )
      }
  }

  getOption(value1){
    if( value1 ==="0"){
      return [
        <Option key="0" value="0">如果值等于</Option>,
        <Option key="1" value="1">如果值不等于</Option>,
        <Option key="2" value="2">如果值包含</Option>,
        <Option key="3" value="3">如果值不包含</Option>,
        <Option key="4" value="4">如果值的开始是</Option>,
        <Option key="5" value="5">如果值的开始不是</Option>,
        <Option key="6" value="6">如果值的结尾是</Option>,
        <Option key="7" value="7">如果值的结尾不是</Option>,
        <Option key="8" value="8">If value valid regex</Option>,
        <Option key="9" value="9">如果值在列表中</Option>,
        <Option key="10" value="10">如果值不在列表中</Option>
      ]
    }else if(value1 ==="3"){
      return[
        <Option key="0" value="0">如果值是FALSE</Option>,
        <Option key="1" value="1">如果值是TRUE</Option>
      ]
    }else{
      return [
        <Option key="0" value="0">如果值等于</Option>,
        <Option key="1" value="1">如果值不等于</Option>,
        <Option key="2" value="2">如果值小于</Option>,
        <Option key="3" value="3">If value is smaller or equal</Option>,
        <Option key="4" value="4">如果值大于</Option>,
        <Option key="5" value="5">If value is greater or equal</Option>,
        <Option key="6" value="6">If value is between</Option>,
        <Option key="7" value="7">如果值在列表中</Option>,
        <Option key="8" value="8">如果值不在列表中</Option>
      ]
    }

  }

  showModel2(value){
    const { getFieldDecorator } = this.props.form;
    const { config } = this.props.model;

      if(value != "3"){
            return(
              <FormItem label="值"  {...this.formItemLayout1} style={{marginBottom:"8px"}}>
                {getFieldDecorator('comparevalue', {
                  initialValue: config.comparevalue,
                })(
                  <Input />
                )}
              </FormItem>
            )
      }
   }

  showModel3(value){
    const { getFieldDecorator } = this.props.form;
    const { config } = this.props.model;

    if(value === "1"){
      return(
        <FormItem label=""  {...this.formItemLayout1} style={{marginBottom:"8px",marginLeft:"25%"}}>
          {getFieldDecorator('successwhenvarset', {
              valuePropName: 'checked',
            initialValue: config.successwhenvarset,
          })(
            <Checkbox>成功校验的值</Checkbox>
          )}
        </FormItem>
      )
    }
  }
  handleChange(){
     const {setFieldsValue}=this.props.form;
       setFieldsValue ({
          successcondition:""
       })
  }

  showMainModel1(bool,value,value1){
    const { getFieldDecorator } = this.props.form;
    const { config } = this.props.model;
      if( bool === false || value === "0" ){
          return(
             <div>
               <FormItem label="类型"  {...this.formItemLayout1} style={{marginBottom:"8px"}}>
                 {getFieldDecorator('fieldtype', {
                   initialValue: config.fieldtype+"",
                 })(
                   <Select   onChange={this.handleChange.bind(this)}>
                     <Option value="0">String</Option>
                     <Option value="1">Number</Option>
                     <Option value="2">Data time</Option>
                     <Option value="3">Boolean</Option>
                   </Select>
                 )}
               </FormItem>
               {
                 this.showModel1(value1)
               }
             </div>
          )
      }
  }

  showMainModel(bool,value,value1){
    const { getFieldDecorator } = this.props.form;
    const { config } = this.props.model;
    console.log(bool);
      if(bool === false || value === "0"){
        console.log("进入22222");
        return(
          <div>
            <FormItem label="成功条件"  {...this.formItemLayout1} style={{marginBottom:"8px"}}>
              {getFieldDecorator('successcondition', {
                initialValue: config.successcondition+"",
              })(
                <Select  >
                  {
                    this.getOption(value1)
                  }
                </Select>
              )}
            </FormItem>
            {
              this.showModel2(value1)
            }
          </div>
        )
      }

  }

    formItemLayout1 = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 },
    };

  render() {
     const { getFieldDecorator,getFieldValue } = this.props.form;
    const { text,config,visible,handleCheckJobName,nextStepNames,parallel } = this.props.model;

    const getType = ()=>{
      if(getFieldValue("valuetype") === undefined){
        return config.valuetype+"";
      }else{
        if(getFieldValue("valuetype")){
          return getFieldValue("valuetype");
        }
      }
    };

    const getFieldType = ()=>{
      if(getFieldValue("fieldtype") === undefined){
        return config.fieldtype+"";
      }else{
        if(getFieldValue("fieldtype")){
          return getFieldValue("fieldtype");
        }
      }
    };

    const getDisabled = ()=>{
      if(getFieldValue("successwhenvarset") === undefined){
        return config.successwhenvarset;
      }else{
        if(getFieldValue("successwhenvarset")){
          return getFieldValue("successwhenvarset");
        }else{
           return false;
        }
      }
    };




    return (
       <Modal
        visible={visible}
        title="检验字段的值"
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
            <FormItem label="作业项名称"  {...this.formItemLayout1} >
              {getFieldDecorator('text', {
                initialValue: text,
                rules: [{ whitespace:true, required: true, message: '请输入作业项名称' },
                  {validator:handleCheckJobName,message: '作业项名称已存在，请更改!' }]
              })(
                <Input  />
              )}
            </FormItem>
            {nextStepNames.length >= 2 ?(
              <FormItem {...this.formItemLayout1} style={{marginBottom:"8px"}}>
                  {getFieldDecorator('parallel', {
                    valuePropName: 'checked',
                    initialValue: parallel,
                  })(
                    <Checkbox disabled={nextStepNames.length <= 1} style={{left:'9rem'}}>下一步骤并行运行</Checkbox>
                  )}
                </FormItem>
            ):null }
            <FormItem
              {...this.formItemLayout1}
              label="源"
              style={{marginBottom:"8px"}}
            >
            </FormItem>
           <FormItem label="校验"  {...this.formItemLayout1} style={{marginBottom:"8px"}}>
            {getFieldDecorator('valuetype', {
              initialValue: config.valuetype+"",
            })(
              <Select   >
                <Option value="0">上一步结果字段</Option>
                <Option value="1">变量</Option>
              </Select>
            )}
          </FormItem>
          {
            this.showModel(getType())
          }
          {
            this.showMainModel1(getDisabled(),getType(),getFieldType())
          }
          <FormItem
            {...this.formItemLayout1}
            label="成功条件"
            style={{marginBottom:"8px"}}
          >
          </FormItem>
          {
            this.showModel3(getType())
          }
          {
            this.showMainModel(getDisabled(),getType(),getFieldType())
          }
        </Form>
      </Modal>
    );
  }
}
const SimpleForm = Form.create()(SIMPLE_EVAL);
export default connect()(SimpleForm);
