import  React  from 'react';
import { Form, Input, Button, message } from 'antd';
import { checkRepeated } from '../../../services/securityTenant';
const FormItem = Form.Item;
//表单间距
const formItemLayout = {
  //标题：span表示长度，offset表示间距
  labelCol: {
    sm: { span: 7, offset:2 }
  },
  //输入框：xm表示x轴布局
  wrapperCol: {
    sm: { span: 7, offset:0 }
  },
};
class FirstFlowTable extends React.Component{
  state = {
  };

  componentWillMount() {
    this.setState({ ...this.props.data });
  }

  componentWillReceiveProps(nextProps) {
    this.setState({ ...nextProps.data });
  }

  // 下一步
  next = (e) => {
    const { form } = this.props;
    e.preventDefault();
    form.validateFields(async (err, values) => {
      if (!err) {
        const formData = {
          id: this.state.id,
          renterName: values.renterName,
          adminAccount: values.adminAccount,
          adminPhone: values.adminPhone,
          adminEmail: values.adminEmail,
        };
        const { data } = await checkRepeated(formData);
        if (data.data) {
          if (data.msg.indexOf('租户名称') > -1) {
            form.setFields({
              renterName: {
                value: form.getFieldValue('renterName'),
                errors: [new Error(data.msg)],
              }
            });
          } else if (data.msg.indexOf('账号') > -1) {
            form.setFields({
              adminAccount: {
                value: form.getFieldValue('adminAccount'),
                errors: [new Error(data.msg)],
              }
            });
          } else if (data.msg.indexOf('手机') > -1) {
            form.setFields({
              adminPhone: {
                value: form.getFieldValue('adminPhone'),
                errors: [new Error(data.msg)],
              }
            });
          } else if (data.msg.indexOf('邮箱') > -1) {
            form.setFields({
              adminEmail: {
                value: form.getFieldValue('adminEmail'),
                errors: [new Error(data.msg)],
              }
            });
          } else {
            message.error(data.msg);
          }
          return;
        }
        this.setState({ ...values });
        this.props.onNext(values);
      }
    });
  }
  render() {
    const { getFieldDecorator } = this.props.form;
    const { readonly } = this.props;

    return (
      <div>
        <Form onSubmit={this.next} style={{margin:'50px 0px'}}>
          {/*租户名称*/}
          <FormItem
            {...formItemLayout}
            label="租户名称:"
          >
            {getFieldDecorator('renterName', {
              initialValue: this.state.renterName,
              rules: [{ required: true, whitespace: true, message: '请输入租户名称' }],
            })(
              <Input  placeholder="租用该系统的使用方名称" maxLength={20} spellCheck={false}/>
            )}
          </FormItem>
          {/*管理员账号*/}
          <FormItem
            {...formItemLayout}
            label="管理员账号："
          >
            {getFieldDecorator('adminAccount', {
              initialValue: this.state.adminAccount,
              rules: [
                { required: true, whitespace: true, message: '请输入管理员账号' },
                { message: '管理员账号必须由3-20个字母或数字组成', pattern: /^[a-zA-Z\d]{3,20}$/},
              ],
            })(
              <Input disabled={readonly} placeholder="租用方管理员账号" maxLength={20} spellCheck={false}/>
            )}
          </FormItem>
          {/*管理员名称*/}
          <FormItem
            {...formItemLayout}
            label="姓名："
          >
            {getFieldDecorator('adminName', {
              initialValue: this.state.adminName,
              rules: [{ required: false, message: '请输入姓名' }],
            })(
              <Input placeholder="姓名" maxLength={20} spellCheck={false}/>
            )}
          </FormItem>
          {/*手机*/}
          <FormItem
            {...formItemLayout}
            label="手机："
          >
            {getFieldDecorator('adminPhone', {
              initialValue: this.state.adminPhone,
              validateTrigger: 'onBlur',
              rules: [
                { required: true, message: '请输入手机号码' },
                { pattern: /^1[345678]\d{9}$/, message: '请输入正确的手机号' },
              ],
            })(
              <Input placeholder="管理员的联系手机" maxLength={11} spellCheck={false}/>
            )}
          </FormItem>
          <FormItem
            {...formItemLayout}
            label="邮箱："
          >
            {getFieldDecorator('adminEmail', {
              initialValue: this.state.adminEmail,
              validateTrigger: 'onBlur',
              rules: [
                { required: true, message: '请输入邮箱' },
                { type: 'email', message: '请输入正确的邮箱' }
              ],
            })(
              <Input placeholder="新建租户成功后将给此邮箱发送管理员账号的密码" maxLength={100} spellCheck={false}/>
            )}
          </FormItem>
          <div style={{marginTop: 50, textAlign: 'center'}}>
            <Button type="primary" htmlType="submit">下一步</Button>
          </div>
        </Form>
      </div>
    )
  }
};
const WrappedApp = Form.create()(FirstFlowTable);
export default WrappedApp;
