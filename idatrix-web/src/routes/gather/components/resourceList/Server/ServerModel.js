import React from 'react';
import { connect } from 'dva';
import { Form, Select, Input, Button,Checkbox,Table,Radio,Tabs,message,Row,Col } from 'antd';
const FormItem = Form.Item;
const TabPane = Tabs.TabPane;
import { withRouter } from 'react-router';
import Style from './Server.css';
import Empower from '../../../../../components/Empower';
import {getSaveServerlist,getcheckServerName} from '../../../../../services/gather';
import { strEnc,strDec } from 'utils/EncryptUtil';

let Timer;
class ServerDateliModel extends React.Component{
  state = {
    activeKey:"1"
  };


  formItemLayout = {
    labelCol: {
      span:4,
      lg:{span:4},
      xl:{span:3}
    },
    wrapperCol: {
      span:8
    }
  };

  handleSubmit = (e) => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if(err){
        return;
      }
      if(values.password && values.password.length>0){
        values.password = strEnc(values.password,values.name,values.hostname,values.port);
      }

      getSaveServerlist(values).then((res)=>{
        const { code } = res.data;
        if( code === "200"){
          message.success('保存成功');
          this.getHide();
        }
      });
    });
  };
  getHide(){
    const form = this.props.form;
    form.resetFields();
    const { location,router } = this.props;
    router.push({...location,query:{}});
  }

  handleConfirm = (rule, value, callback) => {
    const { config } = this.props.resourcecontent;
    if(value &&  value.trim() && value === config.name){
      callback();
    }else{
      if(value && value.trim()){
        if(Timer){
          clearTimeout(Timer);
          Timer = null;
        }
        Timer = setTimeout(()=>{
          getcheckServerName(value).then(( res)=>{
            const { code,data } = res.data;
            if(code === "200"){
              const {result} = data;
              if(result === true){
                callback(true)
              }else{
                callback()
              }
            }
          });
        },300);
      }else{
        callback()
      }
    }
  }

  handleFormLayoutChange = (e) => {
    const activeKey = e.target.value+"";
    this.setState({ activeKey });
  }

  render(){
    const { config } = this.props.resourcecontent;
    const { getFieldDecorator } = this.props.form;
    const { canEdit } = this.props;
    const { mode } = this.state;
    const { formLayout } = this.state;
    return(
      <div className={Style.ServerCenter}>
        <Form layout={formLayout}>
          <div className={Style.ServerBut}>
            <Radio.Group onChange={this.handleFormLayoutChange} style={{ marginBottom: 8,marginTop:15}}>
              {/*<Radio.Button value="1">服务器</Radio.Button>*/}
              {/* <Radio.Button value="2">代理</Radio.Button>*/}
            </Radio.Group>
          </div>

          <div className={Style.divTabs}>

            <Tabs activeKey={this.state.activeKey} defaultActiveKey="1"   animated={false} tabPosition={mode} className={Style.DataNone}>
              <TabPane tab="Tab 1" key="1">
                <FormItem label="服务器名称"   {...this.formItemLayout}>
                  {getFieldDecorator('name', {
                    initialValue:config.name,
                    rules: [{ required: true, message: '请输入服务器名称' },{validator:this.handleConfirm,message: '集群名称已存在，请更改!' }]
                  })(
                    <Input  disabled={config.name?true:false}/>
                  )}
                </FormItem>
                <FormItem label="主机名称或IP地址"  {...this.formItemLayout} >
                  {getFieldDecorator('hostname', {
                    initialValue:config.hostname,
                    rules: [{ required: true, message: '请输入服务器名称' }]
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="端口号(默认为80端口)"  {...this.formItemLayout} >
                  {getFieldDecorator('port', {
                    initialValue:config.port,
                    rules:[{ required: true,pattern:/^([1-9]|[1-9]\d|[1-9]\d{2}|[1-9]\d{3}|[1-5]\d{4}|6[0-4]\d{3}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/, message: '请输入正确的端口号' }]
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="用户名"  {...this.formItemLayout} >
                  {getFieldDecorator('username', {
                    initialValue:config.username,
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="密码"  {...this.formItemLayout} >
                  {getFieldDecorator('password', {
                    initialValue:config.password?strDec(config.password,config.name,config.hostname,config.port):""
                  })(
                    <Input  type="password"/>
                  )}
                </FormItem>
                <FormItem label="是主服务器吗？"  {...this.formItemLayout} >
                  {getFieldDecorator('master', {
                    valuePropName: 'checked',
                    initialValue:config.master
                  })(
                    <Checkbox />
                  )}
                </FormItem>
              </TabPane>
              <TabPane tab="Tab 2" key="2">
                <FormItem label="代理服务器主机名"  {...this.formItemLayout} >
                  {getFieldDecorator('name1', {
                    initialValue:config.name1
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="代理服务器端口"  {...this.formItemLayout} >
                  {getFieldDecorator('hostname1', {
                    initialValue:config.hostname1
                  })(
                    <Input />
                  )}
                </FormItem>
                <FormItem label="主机代理|分离"  {...this.formItemLayout} >
                  {getFieldDecorator('port1', {
                    initialValue:config.port1
                  })(
                    <Input />
                  )}
                </FormItem>
              </TabPane>
            </Tabs>
          </div>
          <Row className={Style.BottomRow}>
            <Col span={12}  style={{textAlign:"right"}}>
              <Empower api={canEdit?"/cloud/editServer.do":""} >
                <Button  onClick={this.handleSubmit.bind(this)} type="primary"  >保存</Button>
              </Empower>
            </Col>
            <Col span={12} style={{textAlign:"center"}}>
              <Button onClick={this.getHide.bind(this)}  >取消</Button>
            </Col>
          </Row>
        </Form>
      </div>
    )
  }
}

const ServerName = Form.create()(ServerDateliModel);

export default withRouter(connect(({ resourcecontent }) => ({
  resourcecontent
}))(ServerName));
