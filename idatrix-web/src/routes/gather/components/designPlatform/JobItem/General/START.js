import React from "react";
import { connect } from 'dva';
import { Button, Form, Input, Radio,Select,Tabs,Checkbox,Row,Col,InputNumber } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const Option = Select.Option;

class SPECIAL extends React.Component {


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
      const {panel,description,transname,key,saveEntry,text,config} = this.props.model;
      let obj = {};
      obj.jobName = transname;
      obj.newName = (text === values.text?"":values.text);
      obj.entryName = text;
      obj.type = panel;
      console.log(panel,"panel");
      obj.description = description;
	    obj.parallel= values.parallel;
      obj.entryParams = {
          start: config.start,
          dummy: config.dummy,
          repeat:values.repeat,
          schedulerType:values.schedulerType,
          intervalSeconds:values.intervalSeconds,
          intervalMinutes:values.intervalMinutes,
          hour:values.hour,
          minutes:values.minutes,
          weekDay:values.weekDay,
          dayOfMonth:values.dayOfMonth,
          intervalDelayMinutes:values.intervalDelayMinutes,
          monthOfYear:values.monthOfYear,
      };
      saveEntry(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });
    });
  };


render() {
 const { getFieldDecorator,getFieldValue } = this.props.form;
    const { config,visible,parallel,nextStepNames } = this.props.model;
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 },
    };

    const setDisabled = ()=>{
      if(getFieldValue("schedulerType") === undefined){
        return config.schedulerType+"";
      }else{
        if(getFieldValue("schedulerType")){
          return getFieldValue("schedulerType");
        }else{
          return false;
        }
      }
    };

    return (
       <Modal
        visible={visible}
        title="作业定时调度"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        width={600}
        onCancel={this.hideModal}
        footer={[
                  <Button key="submit" type="primary" size="large"  onClick={this.handleCreate.bind(this)} >
                    确定
                  </Button>,
                  <Button key="back" size="large"  onClick={this.hideModal}>取消</Button>,
                ]}
      >
        <Form >

          <FormItem label="重复"  {...formItemLayout} style={{marginBottom:"8px"}}>
            {getFieldDecorator('repeat', {
              valuePropName: 'checked',
              initialValue: config.repeat,
            })(
               <Checkbox />
            )}
          </FormItem>
          {nextStepNames.length >= 2 ?(
              <FormItem {...formItemLayout} style={{marginBottom:"8px"}}>
                  {getFieldDecorator('parallel', {
                    valuePropName: 'checked',
                    initialValue: parallel,
                  })(
                    <Checkbox disabled={nextStepNames.length <= 1} style={{left:'11.5rem'}}>下一步骤并行运行</Checkbox>
                  )}
                </FormItem>
            ):null }
          <FormItem label="类型"  {...formItemLayout} style={{marginBottom:"8px"}}>
            {getFieldDecorator('schedulerType', {
              initialValue: config.schedulerType+"",
            })(
              <Select  >
                <Option key="0" value="0">不需要定时</Option>
                <Option key="1" value="1">时间间隔</Option>
                <Option key="2" value="2">天</Option>
                <Option key="3" value="3">周</Option>
                <Option key="4" value="4">月</Option>
                <Option key="3" value="6">年</Option>
              </Select>
            )}
          </FormItem>

          <FormItem label="以秒计算的间隔"  {...formItemLayout} style={{marginBottom:"8px"}}>
            {getFieldDecorator('intervalSeconds', {
              initialValue: config.intervalSeconds,
            })(
               <InputNumber min={0} style={{width:'95%'}} disabled={setDisabled() === "1" ? false:true}/>
            )}
          </FormItem>
          <FormItem label="以分钟计算的间隔"  {...formItemLayout} style={{marginBottom:"8px"}} >
            {getFieldDecorator('intervalMinutes', {
              initialValue: config.intervalMinutes,
            })(
               <InputNumber min={0} style={{width:'95%'}} disabled={setDisabled() === "1" ? false:true}/>
            )}
          </FormItem>
           <FormItem label="首次运行推迟分钟数"  {...formItemLayout} style={{marginBottom:"8px"}}>
            {getFieldDecorator('intervalDelayMinutes', {
              initialValue: config.intervalDelayMinutes,
            })(
               <InputNumber min={0} style={{width:'95%'}} disabled={setDisabled() === "1"? false:true}/>
            )}
          </FormItem>

      <Row>
         <Col span={13}>
             <FormItem label="每天"  {...formItemLayout} style={{marginLeft:"27%"}}>
              {getFieldDecorator('hour', {
                initialValue: config.hour,
              })(
                  <InputNumber min={0} max={23} style={{width:'120%'}} disabled={setDisabled() === "0" || setDisabled() ==="1"? true:false}/>
              )}
            </FormItem>
          </Col>
          <Col span={11}>
             <FormItem label=""  {...formItemLayout} >
              {getFieldDecorator('minutes', {
                initialValue: config.minutes,
              })(
                 <InputNumber min={0} max={59} style={{width:'102%',marginLeft:'-2%'}} disabled={setDisabled() === "0" || setDisabled() === "1"? true:false}/>
              )}
            </FormItem>
          </Col>
       </Row>
       <FormItem label="每周"  {...formItemLayout} style={{marginBottom:"8px"}}>
            {getFieldDecorator('weekDay', {
              initialValue: config.weekDay+"",
            })(
              <Select  disabled={setDisabled() === "3" ? false:true}>
                <Option key="1" value="2">星期一</Option>
                <Option key="2" value="3">星期二</Option>
                <Option key="3" value="4">星期三</Option>
                <Option key="4" value="5">星期四</Option>
                <Option key="5" value="6">星期五</Option>
                <Option key="6" value="7">星期六</Option>
                <Option key="7" value="1">星期日</Option>
              </Select>
            )}
        </FormItem>

         <FormItem label="每月"  {...formItemLayout} style={{marginBottom:"8px"}}>
            {getFieldDecorator('dayOfMonth', {
              initialValue: config.dayOfMonth,
            })(
               <InputNumber min={0} style={{width:'95%'}} 
                  disabled={setDisabled() === "4" || setDisabled() === "6"? false:true}/>
            )}
          </FormItem>

          <FormItem label="月份"  {...formItemLayout} style={{marginBottom:"8px"}}>
            {getFieldDecorator('monthOfYear', {
              initialValue: config.monthOfYear,
            })(
               <InputNumber min={0} max={12} style={{width:'95%'}} disabled={setDisabled() === "6"? false:true}/>
            )}
          </FormItem>

        </Form>
      </Modal>
    );
  }
}
const NormalForm = Form.create()(SPECIAL);
export default connect()(NormalForm);
