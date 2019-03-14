import React from 'react';
import {connect} from 'dva';
import { Button, Form, Input, Col,Row, TreeSelect,message,Radio  } from 'antd';
import { new_front_server,update_front_server,check_front_server,getDepartmentTree,get_frontserver_table_fields,SJGXGTestLink,SJGXGisDuplicateIp } from '../../../services/metadata';
import { convertArrayToTree } from '../../../utils/utils';
import { strEnc,strDec } from 'utils/EncryptUtil';
import Modal from 'components/Modal';
const RadioGroup = Radio.Group;
import { submitDecorator } from 'utils/decorator';

const FormItem = Form.Item;
const { TextArea } = Input;
let Timer;

@submitDecorator
class ServerModel extends React.Component{
    state = {
      info:{},
      visibleTs:true,
      visibleTest:false,
      visibles:false,
      dstype: 1,
    };

  componentWillMount() {
    const { dispatch } = this.props;
    dispatch({ type: 'metadataCommon/getDepartments' });
  }

  handleSubmit = (e) => {
    this.props.disableSubmit();
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (err) {
       return;
      }
      
      values.dbPassword = strEnc(values.dbPassword,values.dbPort,values.serverIp);
       
      this.props.disableSubmit();
      const {dispatch} = this.props;
      const { model,info } = this.props.mfservermodel;
      values.status = 0;
      if(model === "newmodel"){

        const {renterId} = this.props.account;
        new_front_server({...values,renterId}).then(({ data })=>{
          this.props.enableSubmit();
          if (data && data.code === '200') {
            dispatch({
              type:'frontendfesmanage/changeView',
              payload:{
                actionKey:"updatemodel"
              }
            });
            this.setState({
              visibleTs:true
            })
          }
        });
      }else if(model === "editmodel"){
        values.id = info.id;

        const {renterId} = this.props.account;
        update_front_server({...values,renterId}).then(({ data })=>{
          if (data && data.code === '200') {
              dispatch({
                type:'frontendfesmanage/changeView',
                payload:{
                  actionKey:"updatemodel"
                }
              });
              this.setState({
              visibleTs:true
            })
            }
        });
      }
      this.props.enableSubmit();
      this.hideModel();
    })
  };

  hideModel(){
    const {dispatch,form} = this.props;
       this.setState({
          visibleTs: true,
           visibleTest:false,
        });
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

   /*IP地址的校验*/
      SJGXGisDuplicateIp = (rule,value, callback) => {
        const { info} = this.props.mfservermodel;
        const { renterId } = this.props.account;
        if(value && value !== info.serverIp){
          if(Timer){
            clearTimeout(Timer);
            Timer = null;
          }
          Timer = setTimeout(()=>{
            /*renterId serverIp*/
            let obj = {};
            obj.serverIp = value;
            obj.renterId = renterId;
            SJGXGisDuplicateIp(obj).then(( res)=>{
              console.log(res,"res");
              if(res.data.code === "200"){
                /* message.error("该名称已存在,请重新输入");*/
                callback()
              }else if(res.data.code === "630"){

                callback("该IP地址已经创建了前置机，不能再次创建");
              }
            });
          },300);
        }else{
          callback()
        }
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

        const {renterId} = this.props.account
        check_front_server({...obj,renterId}).then(( res)=>{
          if(res.data.data === true){
            /* message.error("该名称已存在,请重新输入");*/
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

  handleClickHost = (rule,value, callback) => {
     const {dispatch} = this.props;
       this.props.form.validateFields((err, value) => {
        console.log(value,"lianjie  j");
          if (err) {
           return;
          }
           value.dbPassword = strEnc(value.dbPassword,value.dbPort,value.serverIp);
           this.props.disableSubmit();
           let arge = {};
           arge.serverIp = value.serverIp;
           arge.dbPort = value.dbPort;
           arge.dbUser = value.dbUser;
           arge.dbPassword = value.dbPassword;

           SJGXGTestLink(arge).then(({ data })=>{
            if (data && data.code === '200') {
                  message.success("测试连接成功");
                  this.setState({
                    visibleTs: false,
                    visibleTest:true,
                  });
            }else {
                  this.setState({
                      visibleTest:true,
                    })
            }
          });
           this.props.enableSubmit();
        })
   }
onChangeClike(){
       this.setState({
          visibleTs: true,
           visibleTest:false,
        });
  }

  onChange = (e) => {
    console.log('radio checked', e.target.value);
    this.setState({
      dstype: e.target.value,
    });
  }

/*{validator:this.SJGXGisDuplicateIp.bind(this),message: '该IP地址已经创建了前置机' }
 {validator:this.SJGXGisDuplicateIp.bind(this),message: '该IP地址已经创建了前置机' }*/

   render(){
     const { mfservermodel,form } = this.props;
     const { getFieldDecorator } = form;
     const { visible,info,model } = mfservermodel;
     const { visibleTs ,visibleTest,visibles} = this.state;
     const { departmentsTree } = this.props.metadataCommon;
     let organization = [];
     try {
        const tmp = JSON.parse(info.organization);
        organization = Array.isArray(tmp) ? tmp : [tmp];
     } catch (err) {}

      return(
        <Modal
          visible={visibles}
          title="2前置机基本信息"
          wrapClassName="vertical-center-modal"
          width={750}
          maskClosable={false}
          footer={[
            <Button key="back" size="large" onClick={()=>{this.hideModel()}}>取消</Button>,
            <Button key="submit"  type="primary" size="large" onClick={this.handleSubmit} loading={this.props.submitLoading}>确定</Button>,
            // <Button size="large" disabled={visibleTest || model === "showclickmodel"} style={{float: "left"}} onClick={this.handleClickHost} loading={this.props.submitLoading}>测试连接</Button>,
          ]}
          onCancel = {this.hideModel.bind(this)}
        >
            <Form >

             <RadioGroup style={{margin: '1% 0% 4% 36%'}} onChange={this.onChange} value={this.state.dstype}>
                <Radio key={2} value={2}>Oracle</Radio>
                <Radio key={3} value={3}>MySQL</Radio>
              </RadioGroup>
          
           {this.state.value === 2 ? (
              <Row>
                <Col span={12}>
                  <FormItem   label="前置机名称: " {...this.formItemLayout1} style={{marginBottom:'10px'}}>
                    {getFieldDecorator('serverName', {
                      initialValue:info.serverName,
                      validateTrigger: 'onBlur',
                      rules: [{ required: true, message: '请输入前置机名称' },
                        {validator:this.handleCheckName.bind(this),message: '前置机名称已存在' }]
                    })(
                      <Input placeholder="请输入前置机名称" maxLength="50" disabled={model === "showclickmodel"}/>
                    )}
                  </FormItem>
                </Col>
                <Col span={12}>
                  <FormItem   label="IP地址: " {...this.formItemLayout1} style={{marginBottom:'10px'}}>
                    {getFieldDecorator('serverIp', {
                      initialValue:info.serverIp,
                      validateTrigger: 'onBlur',
                      rules: [{ required: true, message: '请输入IP地址' },
                              {pattern:/^(\d{1,3}\.){3}\d{1,3}$/,message:'请输入正确的IP地址'}]
                    })(
                      <Input placeholder="请输入IP地址" maxLength="20" onChange={this.onChangeClike.bind(this)} disabled={model === "showclickmodel"}/>
                    )}

                  </FormItem>
                </Col>
             
                <Col span={12}>
                  <FormItem   label="Oracle端口: " {...this.formItemLayout1} style={{marginBottom:'10px'}}>
                    {getFieldDecorator('dbPort', {
                      initialValue:info.dbPort || 3306,
                      rules: [{ required: true, message: '请输入端口号' },
                              {pattern:/^([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-5]{2}[0-3][0-5])$/, message:'请输入正确的端口号' }]
                    })(
                      <Input placeholder="请输入端口号"  onChange={this.onChangeClike.bind(this)} disabled={model === "showclickmodel"}/>
                    )}
                  </FormItem>
                </Col>
                {/*<Col span={8}>
                  <FormItem   label="密码: " style={{marginLeft:"20px"}}>
                    {getFieldDecorator('serverPassword', {
                      initialValue:info.serverPassword,
                      rules: [{ required: true, message: '请输入密码' }]
                    })(
                      <Input type="password"  onChange={this.onChangeClike.bind(this)} disabled={model === "showclickmodel"}/>
                    )}
                  </FormItem>
                </Col>
                <Col span={8}>
                  <FormItem   label="FTP端口: " style={{marginLeft:"20px"}}>
                    {getFieldDecorator('ftpPort', {
                      initialValue:info.ftpPort
                    })(
                      <Input  onChange={this.onChangeClike.bind(this)} disabled={model === "showclickmodel"}/>
                    )}
                  </FormItem>
                </Col>*/}
             
                <Col span={24}>
                  <FormItem   label="Oracle用户名: " {...this.formItemLayout6} style={{marginBottom:'10px'}} >
                    {getFieldDecorator('dbUser', {
                      initialValue:info.dbUser,
                      rules: [{ required: true, message: '请输入Oracle用户名' },
                        {validator:this.handleCheckName.bind(this),message: '已存在该名称' }]
                    })(
                      <Input disabled={model === "showclickmodel"} placeholder="填写的Oracle用户名必须要有权限创建Oracle用户、数据库和表"  onChange={this.onChangeClike.bind(this)}/>
                    )}
                  </FormItem>
                </Col>
             
                <Col span={24}>
                  <FormItem   label="Oracle用户密码" {...this.formItemLayout6} style={{marginBottom:'10px'}} >
                    {getFieldDecorator('dbPassword', {
                      initialValue:info.dbPassword?strDec(info.dbPassword,info.dbPort,info.serverIp) : "",
                      rules: [{ required: true, message: '请输入Oracle用户密码' }]
                    })(
                      <Input disabled={model === "showclickmodel"} placeholder="请输入Oracle用户密码" type="password"  onChange={this.onChangeClike.bind(this)}/>
                    )}
                  </FormItem>
                </Col>
             
                <Col span={24}>
                  <FormItem label="对接的组织(可多选)：" {...this.formItemLayout6}>
                    {getFieldDecorator('organization', {
                      initialValue:organization,
                      rules: [{ required: true, message: '请输入对接的组织' }]
                    })(
                      <TreeSelect
                        placeholder="请选择组织"
                        treeData={departmentsTree}
                        onChange={(value)=>{this.onChange1(value)}}
                        disabled={model === "showclickmodel"}
                        treeDefaultExpandAll={false}
                        multiple
                        allowClear
                      />
                    )}
                  </FormItem>
                </Col>
              
                <Col span={24}>
                  <FormItem
                    label="位置信息："
                    {...this.formItemLayout6}
                  >
                    {getFieldDecorator('positionInfo', {
                      initialValue: info.positionInfo
                    })(
                      <Input maxLength="200" placeholder="请填写机房、机柜、机架等信息" disabled={model === "showclickmodel"}/>
                    )}
                  </FormItem>
                </Col>
              
                <Col span={12}>
                  <FormItem   label="创建者: " {...this.formItemLayout1} style={{marginBottom:'10px'}}>
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
             
                <Col span={24}>
                  <FormItem  label="备注：" {...this.formItemLayout6} style={{marginBottom:'10px'}} >
                    {getFieldDecorator('remark',{
                      initialValue: info.remark,
                    })(<TextArea rows={4}  maxLength="200" spellCheck={false} disabled={model === "showclickmodel"}/>)}
                  </FormItem>
                </Col>
              </Row>
            ):null}

            {this.state.value === 3 ? (
               <Row>
                <Col span={12}>
                  <FormItem   label="前置机名称: " {...this.formItemLayout1} style={{marginBottom:'10px'}}>
                    {getFieldDecorator('serverName', {
                      initialValue:info.serverName,
                      validateTrigger: 'onBlur',
                      rules: [{ required: true, message: '请输入前置机名称' },
                        {validator:this.handleCheckName.bind(this),message: '前置机名称已存在' }]
                    })(
                      <Input placeholder="请输入前置机名称" maxLength="50" disabled={model === "showclickmodel"}/>
                    )}
                  </FormItem>
                </Col>
                <Col span={12}>
                  <FormItem   label="IP地址: " {...this.formItemLayout1} style={{marginBottom:'10px'}}>
                    {getFieldDecorator('serverIp', {
                      initialValue:info.serverIp,
                      validateTrigger: 'onBlur',
                      rules: [{ required: true, message: '请输入IP地址' },
                              {pattern:/^(\d{1,3}\.){3}\d{1,3}$/,message:'请输入正确的IP地址'},
                             ]
                    })(
                      <Input placeholder="请输入IP地址" maxLength="20" onChange={this.onChangeClike.bind(this)} disabled={model === "showclickmodel"}/>
                    )}

                  </FormItem>
                </Col>
              
                <Col span={12}>
                  <FormItem   label="MYSQL端口: " {...this.formItemLayout1} style={{marginBottom:'10px'}}>
                    {getFieldDecorator('dbPort', {
                      initialValue:info.dbPort || 3306,
                      rules: [{ required: true, message: '请输入端口号' },
                              {pattern:/^([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-5]{2}[0-3][0-5])$/, message:'请输入正确的端口号' }]
                    })(
                      <Input placeholder="请输入端口号"  onChange={this.onChangeClike.bind(this)} disabled={model === "showclickmodel"}/>
                    )}
                  </FormItem>
                </Col>
                {/*<Col span={8}>
                  <FormItem   label="密码: " style={{marginLeft:"20px"}}>
                    {getFieldDecorator('serverPassword', {
                      initialValue:info.serverPassword,
                      rules: [{ required: true, message: '请输入密码' }]
                    })(
                      <Input type="password"  onChange={this.onChangeClike.bind(this)} disabled={model === "showclickmodel"}/>
                    )}
                  </FormItem>
                </Col>
                <Col span={8}>
                  <FormItem   label="FTP端口: " style={{marginLeft:"20px"}}>
                    {getFieldDecorator('ftpPort', {
                      initialValue:info.ftpPort
                    })(
                      <Input  onChange={this.onChangeClike.bind(this)} disabled={model === "showclickmodel"}/>
                    )}
                  </FormItem>
                </Col>*/}
              
                <Col span={24}>
                  <FormItem   label="MYSQL用户名: " {...this.formItemLayout6} style={{marginBottom:'10px'}} >
                    {getFieldDecorator('dbUser', {
                      initialValue:info.dbUser,
                      rules: [{ required: true, message: '请输入MYSQL用户名' },
                        {validator:this.handleCheckName.bind(this),message: '已存在该名称' }]
                    })(
                      <Input disabled={model === "showclickmodel"} placeholder="填写的MYSQL用户名必须要有权限创建MYSQL用户、数据库和表"  onChange={this.onChangeClike.bind(this)}/>
                    )}
                  </FormItem>
                </Col>
              
                <Col span={24}>
                  <FormItem   label="MYSQL用户密码" {...this.formItemLayout6} style={{marginBottom:'10px'}} >
                    {getFieldDecorator('dbPassword', {
                      initialValue:info.dbPassword?strDec(info.dbPassword,info.dbPort,info.serverIp) : "",
                      rules: [{ required: true, message: '请输入MYSQL用户密码' }]
                    })(
                      <Input disabled={model === "showclickmodel"} placeholder="请输入MYSQL用户密码" type="password"  onChange={this.onChangeClike.bind(this)}/>
                    )}
                  </FormItem>
                </Col>
             
                <Col span={24}>
                  <FormItem label="对接的组织(可多选)：" {...this.formItemLayout6}>
                    {getFieldDecorator('organization', {
                      initialValue:organization,
                      rules: [{ required: true, message: '请输入对接的组织' }]
                    })(
                      <TreeSelect
                        placeholder="请选择组织"
                        treeData={departmentsTree}
                        onChange={(value)=>{this.onChange1(value)}}
                        disabled={model === "showclickmodel"}
                        treeDefaultExpandAll={false}
                        multiple
                        allowClear
                      />
                    )}
                  </FormItem>
                </Col>
              
                <Col span={24}>
                  <FormItem
                    label="位置信息："
                    {...this.formItemLayout6}
                  >
                    {getFieldDecorator('positionInfo', {
                      initialValue: info.positionInfo
                    })(
                      <Input maxLength="200" placeholder="请填写机房、机柜、机架等信息" disabled={model === "showclickmodel"}/>
                    )}
                  </FormItem>
                </Col>
             
                <Col span={12}>
                  <FormItem   label="创建者: " {...this.formItemLayout1} style={{marginBottom:'10px'}}>
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
             
                <Col span={24}>
                  <FormItem  label="备注：" {...this.formItemLayout6} style={{marginBottom:'10px'}} >
                    {getFieldDecorator('remark',{
                      initialValue: info.remark,
                    })(<TextArea rows={4}  maxLength="200" spellCheck={false} disabled={model === "showclickmodel"}/>)}
                  </FormItem>
                </Col>
              </Row>

            ):null}
              
          </Form>
        </Modal>
      )
   }
}
const MFServerModel = Form.create()(ServerModel);
export default connect(({ mfservermodel, account, metadataCommon }) => ({
  mfservermodel, account, metadataCommon,
}))(MFServerModel)
