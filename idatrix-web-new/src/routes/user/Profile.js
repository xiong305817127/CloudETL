// 用户概况
import React from 'react';
import { connect } from 'dva';
import { Form, Input, Icon } from 'antd';
import UpdatePassword from './UpdatePassword';
import { updateUser } from 'services/account';
import { checkPswd } from 'services/securityCommon';
import Modal from 'components/Modal';
import { strEnc } from 'utils/EncryptUtil';
import MD5 from "md5";

const FormItem = Form.Item;

const formItemLayout = {
  labelCol: { span: 4, offset: 1 },
  wrapperCol: { span: 17 },
};

let Timer = null;

class UserProfile extends React.Component{
  state = {
    editPwdVisible: false,
  }
  componentDidMount() {
    this.updateStateByProps(this.props);
  }
  componentWillReceiveProps(nextProps) {
    if (nextProps.account.profileVisible && !this.props.account.profileVisible) {
      this.updateStateByProps(nextProps);
    }
  }
  // 更新状态
  updateStateByProps(props) {
    const { account } = props;
    if (account.visitTimes < 1) {
      this.setState({
        editPwdVisible: true,
      });
    }
  }
  handleOk = () => {
    const { account,form } = this.props;
    form.validateFields({ force: true }, async (err, values) => {
      if (!err) {
        const formData = {
          id: account.id,
        };
        if (values.newPwd) {
          formData.pswd = strEnc(values.newPwd,account.loginUser.username,account.loginUser.phone,account.loginUser.email);
					const { data } = await updateUser(formData);
					const { code } = data;
          if ( code === "200") {
            Modal.success({
              content: '个人信息修改成功，需要重新登录',
              okText: '重新登录',
              zIndex: "1030",
              onOk: () => {
                window.location.replace('#/login');
              }
            });
          }
        }else{
          this.handleCancel();
        }
      }
    });
  }
  handleCancel = () => {
    const { dispatch } = this.props;
    this.setState({
      editPwdVisible: false,
    }, () => {
      dispatch({type: 'account/hideProfile'});
    });
  }
  // 显示修改密码功能
  handleEditPassword = () => {
    this.setState({
      editPwdVisible: true,
    });
  }

  // 检验旧密码
  checkOldPassword = (rule, value, callback) => {
    if(Timer){
      clearTimeout(Timer);
      Timer = null
    };
    Timer = setTimeout(function(){
      checkPswd(MD5(`#${value}`)).then((res)=>{
				const { code,data } = res.data;
        if(code === "200"){
          if(data){
            callback();
          }else{
            callback('原始密码不正确');
          }
        }
      })
    },300);
  }

  // 对比两次输入密码
  comparePassword = (rule, value, callback) => {
    const form = this.props.form;
    const newPwd = form.getFieldValue('newPwd');
    const confirmPwd = form.getFieldValue('confirmPwd');
    if (rule.field === 'newPwd' && confirmPwd) {
      form.validateFields(['confirmPwd'], { force: true });
      callback();
    } else if (confirmPwd && confirmPwd !== newPwd) {
      callback('两次输入的密码必须一致');
    } else {
      callback();
    }
  }

  render() {
    const { profileVisible,loginUser } = this.props.account;
    const { getFieldDecorator } = this.props.form;
    const { editPwdVisible } = this.state;
    const { email,phone,realName,username  } = loginUser;

    return <div>
      <Modal
        title="用户信息"
        visible={profileVisible}
        closable={false}
        maskClosable={false}
        onOk={this.handleOk.bind(this)}
        onCancel={this.handleCancel.bind(this)}
        okText="修改"
      >
        <Form>
          <FormItem {...formItemLayout} label="用户名称">
            <span className="ant-form-text">{realName}</span>
          </FormItem>
          <FormItem {...formItemLayout} label="登录账号">
            <span className="ant-form-text">{username}</span>
          </FormItem>
          {!editPwdVisible ? (
            <FormItem {...formItemLayout} label="登录密码">
              {getFieldDecorator('pswd')(
                <Input disabled type="password" placeholder="登录密码"
                  style={{width: '100%'}}
                  addonAfter={<Icon type="edit" title="修改密码" onClick={this.handleEditPassword} />}
                />
              )}
            </FormItem>
          ) : null}
          {editPwdVisible ? (
            <FormItem {...formItemLayout} label="原始密码">
              {getFieldDecorator('oldPwd', {
                validateTrigger: 'onBlur',
                rules: [
                  { required: true, message: '请输入原始密码'},
                  { validator: this.checkOldPassword }
                ]
              })(
                <Input type="password" placeholder="原始密码" />
              )}
            </FormItem>
          ) : null}
          {editPwdVisible ? (
            <FormItem {...formItemLayout} label="新密码">
              {getFieldDecorator('newPwd', {
                validateTrigger: 'onBlur',
                rules: [
                  { required: true, message: '密码长度须为6~18之间，且不允许纯字母或纯数字', pattern: /^(?!^\d+$)(?!^[a-z]+$).{6,18}$/i },
                  { validator: this.comparePassword }
                ],
              })(
                <Input type="password" placeholder="新密码" />
              )}
            </FormItem>
          ) : null}
          {editPwdVisible ? (
            <FormItem {...formItemLayout} label="确认密码">
              {getFieldDecorator('confirmPwd', {
                validateTrigger: 'onBlur',
                rules: [
                  { required: true, message: '请输入确认密码' },
                  { validator: this.comparePassword }
                ],
              })(
                <Input type="password" placeholder="确认密码" />
              )}
            </FormItem>
          ) : null}
          <FormItem {...formItemLayout} label="邮件地址">
            <span className="ant-form-text">{email}</span>
          </FormItem>
          <FormItem {...formItemLayout} label="手机号码">
            <span className="ant-form-text">{phone}</span>
          </FormItem>
        </Form>
      </Modal>
      <UpdatePassword />
    </div>;
  }
}

export default connect(({ account })=>({
  account,
}))(Form.create()(UserProfile));


// {getFieldDecorator('phone', {
//   initialValue:phone,
//   validateTrigger: 'onBlur',
//   rules: [{ required: true, message: '请输入手机号码', pattern: /^1[34578]\d{9}$/ }],
// })(
//   <Input placeholder="手机号码" />
// )}

// {getFieldDecorator('email', {
//   initialValue:email,
//   validateTrigger: 'onBlur',
//   rules: [{ type: 'email', required: true, message: '请输入正确的邮件地址' }],
// })(
//   <Input maxLength="100" placeholder="邮件地址" />
// )}