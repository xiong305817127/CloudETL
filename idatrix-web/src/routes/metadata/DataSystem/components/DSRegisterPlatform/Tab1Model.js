import React from 'react';
import {connect} from 'dva';
import { Button, Form, Input, Radio,Select,Col,Row, message } from 'antd';
import { dbUsernameIsExists } from 'services/metadataDataSystem';
import { update_database_table_fields,insert_database_table_fields,check_if_dsname_exists } from "../../../../../services/metadata"
import Modal from 'components/Modal';
import ClearInput from "components/utils/clearInput"

import { submitDecorator } from 'utils/decorator';
import {strDec, strEnc} from "utils/EncryptUtil"
import {hashHistory} from "dva/router"

const FormItem = Form.Item;
const { TextArea } = Input;

let Timer;
let Timer2;
@submitDecorator
class Model extends React.Component{
  state = {
    info:{}
  }
  handleSubmit = (e) => {
    e.preventDefault();
    this.props.form.validateFields({ force: true }, (err, values) => {
        if(err){
           return
        }
        this.props.disableSubmit();
        const {dsType,model} = this.props.tab1model;
        const {dispatch ,routing} = this.props;
      if(model === "newmodel"){
        values.dsType = dsType;
        values.sourceId = 2;
        values.creator = this.props.account.username;
        values.modifier = "";

        // 传入renterId
        const {renterId} = this.props.account;
        const dbPassword = values.dbpw ? strEnc(values.dbpw,values.dbun, values.dbDatabasename):"";
        insert_database_table_fields({
          ...values,
          renterId,
          dbUsername: values.dbun, 
          dbPassword,
          dbun: null,
          dbpw: null
        }).then((res)=>{
          this.props.enableSubmit();
          const { code,msg } = res.data;
          if(code === "200"){
            dispatch({
              type:'datasystemsegistration/changeView',
              payload:{
                actionKey:"updatemodel"
              }
            });
            this.hideModel();
            message.success(res.data.msg ?res.data.msg: "新建成功");
            hashHistory.push(routing.locationBeforeTransitions.pathname + routing.locationBeforeTransitions.search);
          }
        })
      }else if(model === "editmodel"){
        const {dsId} = this.props.tab1model.info;
        values.dsId = dsId;

        const {renterId} = this.props.account;
        const dbPassword = values.dbpw ? strEnc(values.dbpw,values.dbun, values.dbDatabasename) : "";

        update_database_table_fields({
          ...values,
          dbUsername: values.dbun, 
          renterId,
          dbPassword,
          dbun: null,
          dbpw: null
        }).then((res)=>{
          this.props.enableSubmit();
          const { code,msg } = res.data;
              if( code === "200"){
                dispatch({
                  type:'datasystemsegistration/changeView',
                  payload:{
                    actionKey:"updatemodel"
                  }
                });
                this.hideModel();
                message.success(res.data.msg ?res.data.msg: "修改成功");
                hashHistory.push(routing.locationBeforeTransitions.pathname + routing.locationBeforeTransitions.search);
              }
          })
      }
    });
  };
  hideModel(){
    const {dispatch,form} = this.props;
    dispatch({
      type:"tab1model/hide",
      visible:false
    });
    form.resetFields();
  }

  formItemLayout1 = {
    labelCol: { span:6},
    wrapperCol: { span: 16 },
  };

  formItemLayout3 = {
    labelCol: { span: 3},
    wrapperCol: { span:20},
  };
  /*检测文件名*/
  handleCheckName = (rule,value, callback) => {
    const { info,dsType} = this.props.tab1model;
    if(value && value != info.dbDatabasename){
      if(Timer){
        clearTimeout(Timer);
        Timer = null;
      }
      Timer = setTimeout(()=>{
        let obj = {};
        obj.dbDatabasename = value;
        obj.dsType = dsType;
        obj.sourceId = 2;
        obj.type = "create";

        check_if_dsname_exists(obj,{type:"dbname"}).then(( res)=>{
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
  }

  // 检测数据系统名称
  handleCheckDSName = (rule,value, callback) => {
    const { info,dsType} = this.props.tab1model;
    if(value && value != info.dsName){
      if(Timer2){
        clearTimeout(Timer2);
        Timer2 = null;
      }
      Timer2 = setTimeout(()=>{
        let obj = {};
        obj.dsName = value;
        check_if_dsname_exists(obj).then(( res)=>{
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
  }

  /* 检测数据库用户名是否重复 */
  handleGetDBUserName = async (rule, value, callback) => {
    const { data } = await dbUsernameIsExists({
      dbUsername: value,
      dsType:3
     /* sourceId: 2,*/
    });
    if (data.data) {
      callback('该用户名已存在');
    } else {
      callback();
    }
  }

  render(){
    const { tab1model,form } = this.props;
    const { getFieldDecorator } = form;
    const { visible,info,dsType } = tab1model;
    // console.log(dsType);
    const title = dsType=='3'?"平台侧MYSQL基本信息":(dsType=='4'?"平台侧HIVE基本信息":"平台侧HBASE基本信息")
    const readonly = info.status === '已生效';
    return(
      <Modal
        visible={visible}
        title={title}
        wrapClassName="vertical-center-modal"
        onOk={this.handleSubmit.bind(this)}
        onCancel={this.hideModel.bind(this)}
        maskClosable={false}
        confirmLoading={this.props.submitLoading}
      >
        <Form  className="login-form" style={{margin:'0 5%'}}>
          <ClearInput />
          <FormItem label="数据库中文名: "  {...this.formItemLayout1} >
            {getFieldDecorator('dsName', {
              validateTrigger: 'onBlur',
              initialValue:info.dsName,
              rules: [
                { required: true, message: '请输入数据库中文名称' },
                { validator:this.handleCheckDSName.bind(this),message: '数据库中文名称已存在，请更改' }
              ]
            })(
              <Input disabled={readonly} placeholder="请输入数据库中文名称" maxLength="50" />
            )}
          </FormItem>

          <FormItem label="数据库名称: "  {...this.formItemLayout1} >
            {getFieldDecorator('dbDatabasename', {
              initialValue:info.dbDatabasename,
              validateTrigger: 'onBlur',
              validateFirst: true,
              rules: [
                { required: true, message: '请输入数据库名称' },
                {
                  pattern: /^(?=[a-z])[0-9a-z_]+$/,
                  message: "只能使用小写字母、数字、下划线，且必须以字母开头"
                },                
                { validator:this.handleCheckName.bind(this),message: '数据库名称已存在，请更改' }]
            })(
              <Input disabled={readonly} placeholder="请输入需要创建的数据库名称" maxLength="50" />
            )}
          </FormItem>
          {/*<FormItem   label="数据库端口号1: "  {...this.formItemLayout1} >
            {getFieldDecorator('dbPort', {
              initialValue:info.dbPort,
              rules: [{ required: true, message: '请输入数据库端口号' }]
            })(
              <Input placeholder="请输入数据库端口号"/>
            )}
          </FormItem>*/}

          {dsType=='3'?<FormItem label="数据库用户名：" {...this.formItemLayout1}  >
            {getFieldDecorator('dbun',{
              initialValue: info.dbUsername,
              validateTrigger: 'onBlur',
              validateFirst: true,
              rules: [
                { required: true, message: '请输入数据库用户名' },
                {
                  pattern: /^(?=[a-z])[0-9a-z_-]+$/,
                  message: "只能使用小写字母、数字、下划线，且必须以字母开头"
                }
              ]
            })(<Input disabled={readonly} maxLength="20" placeholder="请输入要创建的数据库用户名称"/>)}
          </FormItem>:null}
          {dsType=='3'?<FormItem  label="数据库密码：" {...this.formItemLayout1}  >
            {getFieldDecorator('dbpw',{
              initialValue: info.dbPassword ? strDec(info.dbPassword, info.dbUsername, info.dbDatabasename) : "",
              validateTrigger: 'onBlur',
              rules: [
                 { required: true, message: '请输入数据库密码' },
                { message: '至少8位，由大写字母、小写字母、特殊字符和数字组成', pattern: /^(?=^.{8,}$)(?=.*\d)(?=.*[\W_]+)(?=.*[A-Z])(?=.*[a-z])(?!.*\n).*$/ },
              ]
            })(<Input disabled={readonly} maxLength="20" type="password" placeholder="请输入数据库密码"/>)}
          </FormItem>:null}
          <FormItem  label="备注：" {...this.formItemLayout1}  >
            {getFieldDecorator('remark',{
              initialValue: info.remark,
            })(<TextArea placeholder="请输入0-200的备注"  maxLength="200" style={{height:60}}/>)}
          </FormItem>
        </Form>
      </Modal>
    )
  }
}
const Tab1Model = Form.create()(Model);
export default connect(({ tab1model, account,routing }) => ({
  tab1model,
  account,
  routing
}))(Tab1Model)
