import React from 'react';
import { Form } from "antd";
const FormItem = Form.Item;

let timer = null;

class ResentpwdSuccess extends React.Component{
  //动态显示秒：
  constructor(props){
    super(props);
    this.state={
      value: 3,
    }
  }
  componentWillMount() {
    timer = setInterval(() => {
      this.setState({
        value: this.state.value === 0 ? 0: this.state.value-1,
      });
    }, 1000);
  }
  componentWillUnmount() {
    clearInterval(timer);
  }

  render(){
    return(
      <div style={{paddingTop:250}}>
        <Form>
          <FormItem wrapperCol={{offset:5}}>
            <span style={{fontSize:28}}>恭喜您重置管理员密码成功，系统将在&nbsp;<span style={{color:'red'}}>{this.state.value}</span>&nbsp;秒后自动跳转到登录页面！</span>
          </FormItem>
        </Form>
      </div>
    )
  }
}
const App = Form.create()(ResentpwdSuccess);
export default App;
