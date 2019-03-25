/**
 * 政府版登录页
 */
import React from 'react';
import { withRouter, Link } from 'react-router';
import { Form, Icon, Input, Button, Checkbox, Row, Col } from 'antd';
import { connect } from 'dva';
import baseInfo from 'config/baseInfo.config';
import { loginDecorator, gobackDecorator } from './loginDecorator';
import Style from './loginPage-gov.less';

const FormItem = Form.Item;

@loginDecorator
@gobackDecorator
class LoginPage extends React.Component {

  render() {
    const { getFieldDecorator } = this.props.form;
    const { captchaImg,loginText } = this.props.loginModel;
    return (<div className={Style.bg}>
      <div className={Style.formBg}>
        <div className={Style.formWrap}>
          <header style={SITE_NAME==="noLogo"?{paddingTop:"40px"}:null}>
            <img className={Style.logo} style={SITE_NAME==="noLogo"?{display:"none"}:null} src={baseInfo.logo} />
            <p>{baseInfo.siteName}</p>
          </header>
          <Form className={Style.form}>
            <FormItem>
              {getFieldDecorator('userName', {
                rules: [{ required: true, message: '请输入用户名' }],
                initialValue: this.props.userName,
              })(
                <Input
                  prefix={<Icon type="user" className={Style.icon} />}
                  placeholder="请输入用户名"
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
                  prefix={<Icon type="lock" className={Style.icon} />}
                  type="password"
                  placeholder="请输入密码"
                  onPressEnter={this.props.submit.bind(this)}
                />
              )}
            </FormItem>
            <Row>
              <Col span={12}>
                <FormItem>
                  {getFieldDecorator('captcha', {
                    rules: [{ required: true, message: '请输入验证码' }],
                  })(
                    <Input
                      placeholder="请输入验证码"
                      autoComplete={false}
                      onPressEnter={this.props.submit.bind(this)}
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem>
                  <a className={Style.captcha} onClick={this.props.drawCaptcha.bind(this)}>
                    <img src={captchaImg} alt="loading..." />
                  </a>
                </FormItem>
              </Col>
            </Row>
            <Row>
              <Col span={12}>
                <FormItem>
                  {getFieldDecorator('remember', {
                    valuePropName: 'checked',
                    initialValue: this.props.rememberMe,
                  })(
                    <Checkbox style={{ float: 'left' }}>记住密码</Checkbox>
                  )}
                </FormItem>
              </Col>
              <Col span={12} style={{ textAlign: 'right' }}>
                <Link to="/resetpwd" style={{ color: '#333' }}>忘记密码？</Link>
              </Col>
            </Row>
            <Button type="primary" onClick={this.props.submit.bind(this)} disabled={loginText!=="登录"} className={Style.submit}>
              {loginText}
            </Button>
          </Form>
        </div>
      </div>
    </div>);
  }
}

export default connect(({ account, loginModel }) => ({
  account,
  loginModel,
}))(withRouter(Form.create()(LoginPage)));
