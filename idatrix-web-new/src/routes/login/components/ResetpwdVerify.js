import React from 'react';
import { connect } from 'dva';
import { Form,  Input, Button } from "antd";
const FormItem = Form.Item;
import Style from '../resetpwdPage.css';
const formItemLayout = {
  labelCol: { span: 4, offset: 5 },
  wrapperCol: { span: 6 },
};
class ResetpwdVerify extends React.Component{
  // 下一步
  next = (e) => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (!err) {
        // console.log('222输入的验证码: ', values);
        this.setState({ ...values });
        this.props.onNext(values);
      }
    });
  };
  render(){
    const { getFieldDecorator } = this.props.form;
    return(
        <Form  onSubmit={this.next} className={Style.resetpwdForm} style={{paddingTop:120}}>

          {/*账户:*/}
          <FormItem
            wrapperCol={{offset:8}}
          >
              <h2>验证码已发送到您的邮箱中，请在5分钟内完成验证：</h2>
          </FormItem>

          <FormItem
            {...formItemLayout}
            label="验证码"
            style={{marginTop:100}}
          >
            <div className={Style.resetpwdFormCode}>
              {getFieldDecorator('identifyCode', {
                rules: [{type:'string', required: true, message: '验证码不能为空' }],
              })(
                <Input style={{height:40,marginBottom:10}} placeholder="请输入正确的验证码" maxLength="4" spellCheck={false}/>
              )}
            </div>
          </FormItem>

          <FormItem
            wrapperCol={{offset:9}}
          >
            <Button type='primary'  htmlType="submit" style={{height:40,width:'40%'}} disabled={this.props.nextdisabled}>下一步</Button>
          </FormItem>

        </Form>
    )
  }
}

const App = Form.create()(ResetpwdVerify);
export default connect(({ App, account }) => ({
  App,
  account,
}))(App);
