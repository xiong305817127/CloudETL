import React from 'react';
import { connect } from 'dva';
import { Form, Icon, Input, Button, Checkbox, Row, Col } from 'antd';
import { loginDecorator } from '../loginDecorator';

import Style from './LoginModel.css';

const FormItem = Form.Item;

@loginDecorator
class LoginForm extends React.Component {
  // 3.输出组件页面：
  render() {
    const { getFieldDecorator } = this.props.form;
		const { loginText } = this.props.loginModel;
		
    return (
      <div  id="LoginForm">
        <Row className="LoginModel">
          <Col span={24}><h1 style={{ lineHeight: '3em', textAlign:'center' }}>用户登录</h1></Col>
          <Col span={24} style={{ maxWidth: 350}}>
            <Form style={{ marginLeft:'50px'}} >
              <FormItem>
                {getFieldDecorator('userName', {
                  rules: [{ required: true, message: '请输入用户名' }],
                  initialValue: this.props.userName,
                })(
                  <Input
                    prefix={<Icon type="user" style={{ fontSize: '30px' }} />}
                    placeholder="请输入用户名"
                    name="account"
                    onPressEnter={this.props.submit.bind(this)}
                    spellCheck={false}
                  />
                )}
              </FormItem>
              <FormItem>
                {getFieldDecorator('password', {
                  rules: [{ required: true, message: '请输入密码' }],
                  initialValue: this.props.password,
                })(
                  <Input
                    prefix={<Icon type="lock" style={{ fontSize: '30px' }} />}
                    type="password"
                    placeholder="请输入密码"
                    name="password"
                    onPressEnter={this.props.submit.bind(this)}
                  />
                )}
              </FormItem>
              <Row>
                {/*验证码输入框*/}
                <Col span={12} style={{height:'55px'}}>
                  <FormItem className="VerifyName">
                    {getFieldDecorator('captcha', {
                      rules: [{ required: true, message: '请输入验证码' }],
                    })(
                      <Input
                        placeholder="请输入验证码"
                        autoComplete="false"
                        name="captcha"
                        onPressEnter={this.props.submit.bind(this)}
                        style={{height:'40px'}}
                      />
                    )}
                  </FormItem>
                </Col>
                {/*后台验证码图片/*/}
                <Col span={12} style={{height:'55px'}}>
                  <FormItem>
                    <a href="#" onClick={this.props.drawCaptcha.bind(this)}><img style={{height:'40px',width:'150px',border:'1px solid #d9d9d9'}} src={this.props.loginModel.captchaImg} alt="loading..." /></a>
                  </FormItem>
                </Col>
              </Row>
              <FormItem>
                {getFieldDecorator('remember', {
                  valuePropName: 'checked',
                  initialValue: this.props.rememberMe,
                })(
                  <Checkbox style={{float: 'left'}}>记住密码</Checkbox>
                )}
                <Button type="primary" onClick={this.props.submit.bind(this)} className="login-form-button" style={{backgroundColor: '#ff7900',width: '300px',
                  height: 40,border:'transparent',marginTop:'10px'}} disabled={loginText!=="登录"}>
                 {loginText}
                </Button>
              </FormItem>
              <FormItem style={{fontSize:'16px',textAlign:'center'}}>
                <a  href="#resetpwd" style={{color:'#a5a5a5'}}>忘记密码？</a>
              </FormItem>
            </Form>
          </Col>
        </Row>
      </div>
    )
  }
}

const LoginModel = Form.create()(LoginForm);

export default connect(({ loginModel, account }) => ({
  loginModel,
  account,
}))(LoginModel);
