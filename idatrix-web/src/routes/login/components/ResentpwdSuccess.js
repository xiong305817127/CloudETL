import React from 'react';
import { Form, Input, Button  } from "antd";
const FormItem = Form.Item;
import Style from '../resetpwdPage.css';
const formItemLayout = {
  labelCol: { span: 4, offset: 5 },
  wrapperCol: { span: 6 },
};
class ResentpwdSuccess extends React.Component{
  //确认密码
  checkPassword = (rule, value, callback) => {
    const form = this.props.form;
    const newPwd = form.getFieldValue('newPassword');
    const confirmPwd = form.getFieldValue('confirmPassword');
    if (rule.field === 'newPassword' && confirmPwd) {
      form.validateFields(['confirmPassword'], { force: true });
      callback();
    } else if (confirmPwd && confirmPwd !== newPwd) {
      callback('两次输入的密码必须一致');
    } else {
      callback();
    }
  };
  // 下一步
  next = (e) => {
    e.preventDefault();
    this.props.form.validateFields({ force: true }, (err, values) => {
      if (!err) {
        // console.log('333重设密码: ', values);
        this.setState({ ...values });
        this.props.onNext(values);
      }
    });
  };

  render(){
    const { getFieldDecorator } = this.props.form;
    return(
          <Form  onSubmit={this.next} className={Style.resetpwdForm} style={{paddingTop:120}}>
            <FormItem
              wrapperCol={{offset:8}}
            >
              <h2>重置密码：</h2>
            </FormItem>
            <FormItem
              {...formItemLayout}
              label="新密码"
            >
              {getFieldDecorator('newPassword', {
                validateTrigger:'onBlur',
                rules: [{ required: true, message: '密码长度须为6~18之间，且不允许纯字母或纯数字' ,pattern: /^(?!^\d+$)(?!^[a-z]+$).{6,18}$/i }, {
                  validator: this.checkPassword},]
              })(
                <Input  placeholder="请输入新密码" type='password' maxLength="18" spellCheck={false} style={{height:40,marginBottom:10}}/>
              )}
            </FormItem>

            <FormItem
              {...formItemLayout}
              label="确认密码"
            >
              {getFieldDecorator('confirmPassword', {
                validateTrigger:'onBlur',
                rules: [{
                  required: true, message: '确认密码不能为空',
                }, {
                  validator: this.checkPassword,
                }],
              })(
                <Input type="password" onBlur={this.handleConfirmBlur} maxLength="18" spellCheck={false} style={{height:40,marginBottom:10}} placeholder='请输入确认密码'/>
              )}
            </FormItem>

            <FormItem
              wrapperCol={{offset:9}}
            >
              <Button type='primary'  htmlType="submit" style={{height:40,width:'40%'}} disabled={this.props.nextdisabled}>完成</Button>
            </FormItem>

          </Form>
    )
  }
}

const App = Form.create()(ResentpwdSuccess);
export default App;
