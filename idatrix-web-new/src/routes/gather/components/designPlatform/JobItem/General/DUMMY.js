import React from "react";
import { connect } from 'dva';
import { Button, Form, Input, Radio,Select,Tabs,Checkbox,Row,Col,InputNumber } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const Option = Select.Option;

class DUMMYModel extends React.Component {


  hideModal = () => {
    const { dispatch,form } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    })
    form.resetFields();
  };


  render() {
    const { getFieldDecorator } = this.props.form;
    const { config,visible } = this.props.model;
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 },
    };


    return (
      <Modal
        visible={visible}
        title="作业定时调度"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        onCancel={this.hideModal.bind(this)}
        footer={[
                  <Button key="submit" type="primary" size="large"  onClick={this.hideModal.bind(this)} >
                    确定
                  </Button>
                ]}
      >
        <Form >
          <FormItem label="重复"  {...formItemLayout} style={{marginBottom:"8px"}}>
            {getFieldDecorator('repeat', {
              initialValue: config.repeat,
            })(
              <Checkbox  disabled/>
            )}
          </FormItem>

          <FormItem label="类型"  {...formItemLayout} style={{marginBottom:"8px"}}>
            {getFieldDecorator('schedulerType', {
              initialValue: config.schedulerType+"",
            })(
              <Select  disabled>
                <Option key="0" value="0">不需要定时</Option>
                <Option key="1" value="1">时间间隔</Option>
                <Option key="2" value="2">天</Option>
                <Option key="3" value="3">周</Option>
                <Option key="4" value="4">月</Option>
              </Select>
            )}
          </FormItem>

          <FormItem label="以秒计算的间隔"  {...formItemLayout} style={{marginBottom:"8px"}}>
            {getFieldDecorator('intervalSeconds', {
              initialValue: config.intervalSeconds,
            })(
              <InputNumber min={0} style={{width:'95%'}} disabled/>
            )}
          </FormItem>
          <FormItem label="以分钟计算的间隔"  {...formItemLayout} style={{marginBottom:"8px"}} >
            {getFieldDecorator('intervalMinutes', {
              initialValue: config.intervalMinutes,
            })(
              <InputNumber min={0} style={{width:'95%'}} disabled/>
            )}
          </FormItem>

          <Row>
            <Col span={13}>
              <FormItem label="每天"  {...formItemLayout} style={{marginLeft:"27%"}}>
                {getFieldDecorator('hour', {
                  initialValue: config.hour,
                })(
                  <InputNumber min={0} max={23} style={{width:'120%'}} disabled/>
                )}
              </FormItem>
            </Col>
            <Col span={11}>
              <FormItem label=""  {...formItemLayout} >
                {getFieldDecorator('minutes', {
                  initialValue: config.minutes,
                })(
                  <InputNumber min={0} max={59} style={{width:'102%',marginLeft:'-2%'}} disabled/>
                )}
              </FormItem>
            </Col>
          </Row>
          <FormItem label="每周"  {...formItemLayout} style={{marginBottom:"8px"}}>
            {getFieldDecorator('weekDay', {
              initialValue: config.weekDay+"",
            })(
              <Select  disabled>
                <Option key="1" value="1">星期一</Option>
                <Option key="2" value="2">星期二</Option>
                <Option key="3" value="3">星期三</Option>
                <Option key="4" value="4">星期四</Option>
                <Option key="5" value="5">星期五</Option>
                <Option key="6" value="6">星期六</Option>
                <Option key="7" value="7">星期日</Option>
              </Select>
            )}
          </FormItem>

          <FormItem label="每月"  {...formItemLayout} style={{marginBottom:"8px"}}>
            {getFieldDecorator('dayOfMonth', {
              initialValue: config.dayOfMonth,
            })(
              <InputNumber min={0} max={30} style={{width:'95%'}} disabled/>
            )}
          </FormItem>

        </Form>
      </Modal>
    );
  }
}
const DUMMY = Form.create()(DUMMYModel);
export default connect()(DUMMY);
