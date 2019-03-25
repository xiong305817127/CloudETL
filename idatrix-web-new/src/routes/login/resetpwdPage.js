import React from 'react';
import { Form, message, Layout } from "antd";
import Style from './resetpwdPage.css';
import ResetpwdFind from './components/ResetpwdFind';
import ResetpwdVerify from './components/ResetpwdVerify';
import ResentpwdSuccess from './components/ResentpwdSuccess';
import ResetpwdWarning from './components/ResetpwdWarning';
import {registerAccount,registerVerify, registerPassword} from '../../services/resetpwd';
import baseInfo from '../../config/baseInfo.config';
import { strEnc } from 'utils/EncryptUtil';

const { Header, Footer, Content } = Layout;

class resetpwdPage extends React.Component{
  constructor(props) {
    super(props);
    this.state = {
      current: 1,
      username: '',
      email: '',
      nextdisabled:false
    };
  }
  //跳转页面
  readPage=()=>{
    this.setState({
      current: this.state.current ===3 ? 0:this.state.current + 1
    });
  };
  //回调函数：
  next = (values) => {
    // console.log('111 ', values);
    const obj = {};
    const objCode = {};
    const objPwd = {};
    // 设置请求条件：避免重复报错
    if(values.username && values.email) {
			obj.username = values.username;
			obj.email = values.email;
      this.setState({
        username:values.username,
        email:values.email,
      });
      registerAccount(obj).then((res)=> {
				const { code,msg } = res.data;
        if (code === "200") {
          message.success(msg);
          this.readPage()
        }
      });
    }
	//2.验证码:

    if(values.identifyCode){
			objCode.username = this.state.username;
			objCode.email = this.state.email;
			objCode.identifyCode = values.identifyCode;
      registerVerify(objCode).then((res)=> {
				const { code,msg } = res.data;
        if(code === "200"){
          message.success(msg);
          this.readPage()
        }
      });
    }
    //3.重置密码：STR加密
    if(values.newPassword && values.confirmPassword){
			objPwd.username = this.state.username;
			objPwd.email = this.state.email;
			objPwd.newPassword = strEnc(values.newPassword,this.state.username,this.state.email);
			objPwd.confirmPassword =  strEnc(values.confirmPassword,this.state.username,this.state.email);
      registerPassword(objPwd).then((res)=>{
				const { code,msg } = res.data;
        if(code === "200"){
					message.success(msg);
          this.readPage();
          setTimeout(() => {
            location.href="#login"
          }, 4000);
        }
      });
    }
  };
  //切换下一步
  showStepModel(current) {
    switch (current) {
      case 0:
        return (<ResetpwdWarning/>);
      case 1:
        return (<ResetpwdFind onNext={this.next.bind(this)} nextdisabled={this.state.nextdisabled}/>);
      case 2:
        return (<ResetpwdVerify onNext={this.next.bind(this)} nextdisabled={this.state.nextdisabled}/>);
      case 3:
        return (<ResentpwdSuccess onNext={this.next.bind(this)} nextdisabled={this.state.nextdisabled}/>);
    }
  }
  render(){
    return(
      <Layout className={Style.resetpwdWrapper}>
          {/*Header*/}
          <Header className={Style.resetpwdHeader}>
            <a href="#login" >
              <img src={baseInfo.logoColour}/>
              <span>{baseInfo.siteName}</span>
            </a>
          </Header>

          {/*Content*/}
          <Content className={Style.resetpwdContent}>
            {this.showStepModel(this.state.current)}
          </Content>

          {/*Footer*/}
          <Footer className={Style.resetpwdFooter}>
            <a>{baseInfo.copyright}</a>
          </Footer>

      </Layout>
    )
  }
}

const App = Form.create()(resetpwdPage);
export default App;

