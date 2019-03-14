// 仅修改密码
import React from 'react';
import { connect } from 'dva';
import { Form, Input } from 'antd';
import MD5 from 'md5';
import { updateUser } from 'services/account';
import Modal from 'components/Modal';
import { checkPswd } from 'services/securityCommon';

const FormItem = Form.Item;
let Timer = null;

const formItemLayout = {
  labelCol: { span: 4, offset: 1 },
  wrapperCol: { span: 17 },
};

class UpdatePassword extends React.Component{
  state = {
  }

  handleOk = () => {
    const { account } = this.props;
    this.props.form.validateFields({ force: true }, async (err, values) => {
      if (!err) {
        const formData = {
          id: account.id,
          pswd: MD5(`#${values.newPwd}`),
        };
				const { data } = await updateUser(formData);
				const { code } = data;
        if ( code === "200") {
          Modal.success({
            content: '密码修改成功，需要重新登录',
            okText: '重新登录',
            onOk: () => {
              window.location.replace('#/login');
            }
          });
        } else {
         // data && data.message && message.error(data.message);
        }
      }
    });
  }

  handleCancel = () => {
    const { dispatch } = this.props;
    dispatch({type: 'account/hidePasswordEditor'});
  }

  // 检验旧密码
  checkOldPassword = (rule, value, callback) => {
    if(Timer){
      clearTimeout(Timer);
      Timer = null
    };
    Timer = setTimeout(()=>{
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
    const oldPwd = form.getFieldValue('oldPwd');
    const newPwd = form.getFieldValue('newPwd');
    const confirmPwd = form.getFieldValue('confirmPwd');
    if (rule.field === 'newPwd' && confirmPwd) {
      form.validateFields(['confirmPwd'], { force: true });
      callback();
    } else if (oldPwd === newPwd) {
      callback('新密码不能和旧密码相同');
    } else if (confirmPwd && confirmPwd !== newPwd) {
      callback('两次输入的密码必须一致');
    } else {
      callback();
    }
  }

  render() {
    const { passwordEditorVisible, visitTimes } = this.props.account;
    const { getFieldDecorator } = this.props.form;
    return <Modal
      title="修改密码"
      visible={passwordEditorVisible}
      closable={false}
      maskClosable={false}
      onOk={this.handleOk.bind(this)}
      onCancel={this.handleCancel.bind(this)}
      okText="修改"
    >
      {visitTimes < 1 ? <div style={{textAlign: 'center', color:'#f00', marginBottom: 20}}>
        欢迎！系统检测到您为首次登录系统，建议修改初始密码以保证账户安全
      </div> : null}
      <Form>
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
        <FormItem {...formItemLayout} label="新密码">
          {getFieldDecorator('newPwd', {
            validateTrigger: 'onBlur',
            validateFirst: true,
            rules: [
              { required: true, message: '密码长度须为6~18之间，且不允许纯字母或纯数字', pattern: /^(?!^\d+$)(?!^[a-z]+$).{6,18}$/i },
              { validator: this.comparePassword }
            ],
          })(
            <Input type="password" placeholder="新密码" />
          )}
        </FormItem>
        <FormItem {...formItemLayout} label="确认密码">
          {getFieldDecorator('confirmPwd', {
            validateTrigger: 'onBlur',
            validateFirst: true,
            rules: [
              { required: true, message: '请输入确认密码' },
              { validator: this.comparePassword }
            ],
          })(
            <Input type="password" placeholder="确认密码" />
          )}
        </FormItem>
      </Form>
    </Modal>;
  }
}

export default connect(({ account })=>({
  account,
}))(Form.create()(UpdatePassword));
