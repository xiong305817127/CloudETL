import React from 'react';
import {connect} from 'dva';
import { Button, Form, Input, Col,Row,Cascader } from 'antd';
import { new_front_server,update_front_server,check_front_server,getDepartmentTree,get_frontserver_table_fields } from '../../../services/metadata';
import { convertArrayToTree } from '../../../utils/utils';
import Modal from 'components/Modal';

const FormItem = Form.Item;
const { TextArea } = Input;

let Timer;
class ServerModel extends React.Component{
  state = {
    info:{}
  };
  handleSubmit = (e) => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (err) {
       return;
      }
      const {dispatch} = this.props;
      const { model,info } = this.props.mfservermodel;
      values.status = 0;
      if(model === "newmodel"){

          const {renterId} = this.props.account;
          new_front_server({...values,renterId}).then((res)=>{
        });
        dispatch({
          type:'frontendfesmanage/changeView',
          payload:{
            actionKey:"updatemodel"
          }
        })
      }else if(model === "editmodel"){
        values.id = info.id;

        const {renterId} = this.props.account;
        update_front_server({...values,renterId}).then((res)=>{
        });
        dispatch({
          type:'frontendfesmanage/changeView',
          payload:{
            actionKey:"updatemodel"
          }
        })
      }
      this.hideModel();
    })
  };




  // 构造
    constructor(props) {
      super(props);
      getDepartmentTree(props.account.id).then((res)=>{
        this.setState({
          department: convertArrayToTree(res.data.data || '[]', 0, 'id', 'parentId', 'children', child => ({
            value: child.id,
            label: child.deptName,
          })),
        });
      })
    }
  hideModel(){
    const {dispatch,form} = this.props;
    dispatch({
      type:"mfservermodel/hide",
      visible:false
    });
    form.resetFields();
  }
  formItemLayout1 = {
    labelCol: { span: 10, offset: 0},
    wrapperCol: { span: 14 , offset: 0},
  };
  formItemLayout2 = {
    labelCol: { span: 9},
    wrapperCol: { span: 14 },
  };
  formItemLayout3 = {
    labelCol: { span: 2},
    wrapperCol: { span:21},
  };
  formItemLayout4 = {
    labelCol: { span: 6 },
    wrapperCol: { span: 15 },
  };
  formItemLayout5 = {
    labelCol: { span: 3 },
    wrapperCol: { span: 18 },
  };
  formItemLayout6 = {
    labelCol: { span: 5, offset: 0},
    wrapperCol: { span: 19 , offset: 0},
  };
  /*检测文件名*/
  handleCheckName = (rule,value, callback) => {
    const { info} = this.props.mfservermodel;
    if(value && value !== info.serverName){
      if(Timer){
        clearTimeout(Timer);
        Timer = null;
      }
      Timer = setTimeout(()=>{
        let obj = {};
        obj.serverName = value;
        const {renterId} = this.props.account;
        check_front_server({...obj,renterId}).then(( res)=>{
          const { data } = res.data;
          if(data === true){
            callback(true)
          }else{
            callback()
          }
        });
      },300);
    }else{
      callback()
    }
  };
  transToName(value){
    const args=this.state.department;
  }
  onChange1(value) {
    this.transToName(value);
  }
   render(){

     const { mfservermodel,form } = this.props;
     const { getFieldDecorator } = form;
     const { visible,info } = mfservermodel;
     let organization = [];
     try {
        const tmp = JSON.parse(info.organization);
        organization = Array.isArray(tmp) ? tmp : [tmp];
     } catch (err) {}

      return(
        <Modal
          visible={visible}
          title="前置机基本信息"
          wrapClassName="vertical-center-modal"
          width={750}
          footer={[
            <Button key="back" size="large" onClick={()=>{this.hideModel()}}>取消</Button>,
            <Button key="submit" type="primary" size="large"  onClick={this.handleSubmit}>确定</Button>,
          ]}
          onCancel = {this.hideModel.bind(this)}
        >
            <Form onSubmit={this.handleSubmit} className="login-form" >
              <Row>
                <Col span={12}>
                  <FormItem   label="前置机名称: " {...this.formItemLayout1} style={{marginBottom:'10px'}}>
                    {getFieldDecorator('serverName', {
                      initialValue:info.serverName,
                      rules: [{ required: true, message: '请输入前置机名称' },
                        {validator:this.handleCheckName.bind(this),message: '步骤名称已存在，请更改!' }]
                    })(
                      <Input />
                    )}
                  </FormItem>
                </Col>
                <Col span={12}>
                  <FormItem   label="IP地址: " {...this.formItemLayout1} style={{marginBottom:'10px'}}>
                    {getFieldDecorator('serverIp', {
                      initialValue:info.serverIp,
                      rules: [{ required: true, message: '请输入IP地址' },
                              {pattern:/^[0-9]{0}([0-9]|[.])+$/,message:'请输入正确的IP地址'}]
                    })(
                      <Input />
                    )}
                  </FormItem>
                </Col>
              </Row>

              <Row>
                <Col span={12}>
                  <FormItem   label="端口: " {...this.formItemLayout1} style={{marginBottom:'10px'}}>
                    {getFieldDecorator('serverPort', {
                      initialValue:info.serverPort,
                      rules: [{ required: true, message: '请输入端口号' },
                              {pattern:/^([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-5]{2}[0-3][0-5])$/, message:'请输入正确的端口号' }]
                    })(
                      <Input />
                    )}
                  </FormItem>
                </Col>
                {/*<Col span={8}>
                  <FormItem   label="密码: " style={{marginLeft:"20px"}}>
                    {getFieldDecorator('serverPassword', {
                      initialValue:info.serverPassword,
                      rules: [{ required: true, message: '请输入密码' }]
                    })(
                      <Input type="password"/>
                    )}
                  </FormItem>
                </Col>
                <Col span={8}>
                  <FormItem   label="FTP端口: " style={{marginLeft:"20px"}}>
                    {getFieldDecorator('ftpPort', {
                      initialValue:info.ftpPort
                    })(
                      <Input />
                    )}
                  </FormItem>
                </Col>*/}
              </Row>
              <Row>
                <Col span={12}>
                  <FormItem   label="创建人姓名: " {...this.formItemLayout1} style={{marginBottom:'10px'}}>
                    {getFieldDecorator('manager', {
                      initialValue:this.props.account.username,
                      rules: [{ required: true, message: '请输入创建人姓名' }]
                    })(
                      <Input disabled />
                    )}
                  </FormItem>
                </Col>
                <Col span={12}>
                  <FormItem   label="创建人手机: " {...this.formItemLayout1} style={{marginBottom:'10px'}}>
                    {getFieldDecorator('phone', {
                      initialValue:this.props.account.phone,
                      rules: [{ required: true, message: '请输入创建人手机' }]
                    })(
                      <Input disabled />
                    )}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span={12}>
                  <FormItem   label="创建人邮箱: " {...this.formItemLayout1} style={{marginBottom:'10px'}}>
                    {getFieldDecorator('mail', {
                      initialValue:this.props.account.email,
                      rules: [{ required: true, message: '请输入创建人邮箱' }]
                    })(
                      <Input disabled />
                    )}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span={24}>
                  <FormItem   label="MYSQl用户名: " {...this.formItemLayout6} style={{marginBottom:'10px'}} >
                    {getFieldDecorator('dbUser', {
                      initialValue:info.dbUser,
                      rules: [{ required: true, message: '请输入前置机名称' },
                        {validator:this.handleCheckName.bind(this),message: '步骤名称已存在，请更改!' }]
                    })(
                      <Input />
                    )}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span={24}>
                  <FormItem   label="MYSQl用户密码" {...this.formItemLayout6} style={{marginBottom:'10px'}} >
                    {getFieldDecorator('dbPassword', {
                      initialValue:info.dbPassword,
                      rules: [{ required: true, message: '请输入MYSQl用户密码' }]
                    })(
                      <Input type="password" />
                    )}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span={24}>
                  <FormItem
                    label="位置信息："
                    {...this.formItemLayout6}
                  >
                    {getFieldDecorator('positionInfo', {
                      initialValue: info.positionInfo
                    })(
                      <Input placeholder="请填写机房、机柜、机架等信息"/>
                    )}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span={24}>
                  <FormItem label="对接的组织(可多选)：" {...this.formItemLayout6}>
                    {getFieldDecorator('organization', {
                      initialValue:organization,
                      rules: [{ required: true, message: '请输入对接的组织' }]
                    })(
                      <Cascader options={this.state.department} onChange={(value)=>{this.onChange1(value)}} placeholder="请选择部门" />
                    )}
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span={24}>
                  <FormItem  label="备注：" {...this.formItemLayout6} style={{marginBottom:'10px'}} >
                    {getFieldDecorator('remark',{
                      initialValue: info.remark,
                    })(<TextArea rows={4}  maxLength="200" spellCheck={false}/>)}
                  </FormItem>
                </Col>
              </Row>
          </Form>
        </Modal>
      )
   }
}
const MFServerModel = Form.create()(ServerModel);
export default connect(({ mfservermodel,account }) => ({
  mfservermodel,account
}))(MFServerModel)
