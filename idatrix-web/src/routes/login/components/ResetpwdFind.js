import React from 'react';
import { connect } from 'dva';
import { Form,  Input, Button } from "antd";
import Style from '../resetpwdPage.css';
import {withRouter} from "dva/router"

const FormItem = Form.Item;
const formItemLayout = {
  labelCol: { span: 4, offset: 5 },
  wrapperCol: { span: 6 },
};
class ResetpwdFind extends React.Component{
  constructor(props) {
    super(props);
    this.state = {
      nextdisabled: false,
    };
  }
  // 下一步
  next = (e) => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (!err) {
        this.setState({ ...values });
        this.props.onNext(values);
      }
    });
  };
  render(){
    const { getFieldDecorator} = this.props.form;
    const {router} = this.props;
    return(
      <Form onSubmit={this.next} className={Style.resetpwdForm}>
          {/*文字*/}
          <FormItem>
            <h1>忘记密码</h1>
            <p>请输入您的账号和邮箱，进行密码重置：</p>
          </FormItem>

          {/*账户:span*/}
          <FormItem
            {...formItemLayout}
            label="账户"
            style={{marginTop:100}}
          >
            {getFieldDecorator('username', {
              rules: [{required: true, pattern: /^[\u4E00-\u9FA5A-Za-z0-9]+$/,message:'账号必须由中文、英文或数字组成'}],
            })(
              <Input   placeholder='账号' maxLength="60" spellCheck={false} style={{height:40,marginBottom:10}}/>
            )}
          </FormItem>

          <FormItem
            {...formItemLayout}
            label="邮箱"
          >
            {getFieldDecorator('email',{
              rules: [{required: true, type: 'email', message:'请输入正确的邮箱'}],
            })(
              <Input   placeholder={'邮箱'} maxLength="60" spellCheck={false} style={{height:40,marginBottom:10}}/>
            )}
          </FormItem>

          <FormItem
            wrapperCol={{offset:9}}
          >
            <Button type='primary'  htmlType="submit" style={{height:40,width:'40%'}} disabled={this.props.nextdisabled}>下一步</Button>
          </FormItem>
        <p style={{textAlign:"center",cursor:"pointer"}} onClick={()=>{
          router.push("/login")
        }}>返回首页</p>
      </Form>
    )
  }
}

const App = Form.create()(ResetpwdFind);
export default connect(({ App, account }) => ({
  App,
  account,
}))(withRouter(App));
