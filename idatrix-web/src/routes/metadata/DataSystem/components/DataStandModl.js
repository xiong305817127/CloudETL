import React from 'react';
import {connect} from 'dva';
import { Button, Form, Input,Select } from 'antd';
import Style from './DatabaseModel.css'
import Modal from 'components/Modal';

const FormItem = Form.Item;

class DataStandModl extends React.Component {
  state = {
    radioValue: "1",
    data: []
  };
  handleSubmit = (e) => {
    e.preventDefault();
    this.hideModel();
  };
  hideModel() {
    const {dispatch, form} = this.props;
    dispatch({
      type: "databasemodel/hide",
      visible: false
    })
    form.resetFields();
  }
  formItemLayout1 = {
    labelCol: {span: 5},
    wrapperCol: {span: 17},
  };
  formItemLayout3 = {
    labelCol: {span: 2},
    wrapperCol: {span: 21},
  };
  handleRadioChange(e) {
    this.setState({
      radioValue: e.target.value
    })
  }
  handleFocus() {
    render()
    {
      const {getFieldDecorator} = this.props.form;
      const {visible, info} = this.props.databasemodel;
      const options = this.state.data.map(d => <Select.Option key={d.id}
                                                              value={d.id+""}>{d.serverName}</Select.Option>);

      return (
        <Modal
          visible={visible}
          title="元素据标准查询"
          wrapClassName="vertical-center-modal DatabaseModel"
          footer={[
            <Button key="back" size="large" onClick={()=>{this.hideModel()}}>取消</Button>,
            <Button key="submit" type="primary" size="large"  onClick={this.handleSubmit}>上传</Button>,
          ]}
          onCancel={()=>{this.hideModel()}}
        >

          <Form onSubmit={this.handleSubmit} className="login-form" style={{margin:'0 5%'}}>
            <FormItem label="选择文件: "  {...this.formItemLayout1} >
              {getFieldDecorator('dbPassword', {
                initialValue: info.dbPassword,
                rules: [{required: true, message: '请选择上传内容'}]
              })
              }
            </FormItem>
            <FormItem label="备注：" {...this.formItemLayout3} style={{marginBottom:"8px"}}>
              {getFieldDecorator('remark', {
                initialValue: info.remark,
              })(<Input type="textarea" maxLength="200" style={{height:60}}/>)}
            </FormItem>
          </Form>
        </Modal>
      )
    }
  }
}
const DataStandModl = Form.create()(DataStandModl);
export default connect(({ datastandmodl }) => ({
  datastandmodl
}))(DataStandModl);
