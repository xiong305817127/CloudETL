/**
 * Created by Administrator on 2017/5/22.
 */
import React from 'react';

import {connect} from 'dva';
import { Button, Form, Input, Radio,Select,Col,Row,message } from 'antd';
const FormItem = Form.Item;
const { TextArea } = Input;
import { dbUsernameIsExists } from 'services/metadataDataSystem';
import { insert_database_table_fields,update_database_table_fields,get_front_pos,check_if_dsname_exists,search_ftp_table_fields } from '../../../../services/metadata'
import Style from './DatabaseModel.css'
import Modal from 'components/Modal';
import { submitDecorator } from 'utils/decorator';
import {strEnc} from "utils/EncryptUtil"

let Timer;

@submitDecorator
class Register extends React.Component{
  state = {
    radioValue:"1",
    data:[],
    dsType:'',
  }

  handleSubmit = (e) => {
    console.log(this.state.dsType,"注册前置机数据库基本信息");
    e.preventDefault();
    this.props.form.validateFields({ force: true }, (err,values) => {
      if (!err) {
        this.props.disableSubmit();
        values.renterId=this.props.account.renterId;
        values.sourceId=1;
        values.dsType=this.state.dsType;
        values.type ='register';
        const {dispatch} = this.props;
        const { model } = this.props.resitionemodel;

        /**
         * 数据库密码进行加密处理
         * 使用dbUserName+dbDatabasename
         * 注意和数据表的区别
         * 
         * edited by steven leo on 2018/09/25
         */
        const dbPassword = strEnc(values.dbPassword,values.dbUsername,values.dbDatabasename);
        values.status = 0;
        if(model === "newmodel"){
          insert_database_table_fields({...values,dbPassword}).then(({ data })=>{
            this.props.enableSubmit();
            if (data && data.code === '200') {
              dispatch({
                type:'datasystemsegistration/changeView',
                 payload:{
                   actionKey:"updatemodel"
                 }
              })
              message.success('新建成功');
            }else if(data && data.code === '601'){
                message.error("密码强度不符合");
            }else if(data && data.code === '602'){
                  message.error("连接失败，请检查服务器配置");
            }else if(data && data.code === '606'){
                  message.error("请求失败");
            } else {
              message.error("新增失败,请查看您所请求的前置机的IP是否存在");
            }
          })
        }
        else if(model === "editmodel"){
          const { info } = this.props.resitionemodel;
          values.dsId = info.dsId;
          let serverName = info.serverName;
          if(serverName === values.serverId){
            values.serverId = info.frontEndServer.id;
          }

          const {renterId} = this.props.account;

          update_database_table_fields({...values,renterId}).then(({ data })=>{
            this.props.enableSubmit();
            if (data && data.code === '200') {
              dispatch({
                type:'datasystemsegistration/changeView',
                payload:{
                  actionKey:"updatemodel"
                }
              })
              message.success('修改成功');
            }
          })

        }
        this.hideModel();
      }
      this.props.enableSubmit();
    });
  }

  /*检测文件名 数据库名称*/
  handleGetName = (rule,value, callback) => {
    const { info,dsType } = this.props.resitionemodel;
    if(value && value !== info.dbDatabasename){
      if(Timer){
        clearTimeout(Timer);
        Timer = null;
      }
      Timer = setTimeout(()=>{
        let obj = {};
        obj.dbDatabasename = value;
        obj.dsType = this.state.dsType;
        check_if_dsname_exists(obj).then(( res)=>{
          console.log(res,"check_get_name");
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
  /*检测文件名 数据库系统名称*/
  handleGetNameScarch = (rule,value, callback) => {
    const { info } = this.props.resitionemodel;
    if(value && value !== info.dsName){
      if(Timer){
        clearTimeout(Timer);
        Timer = null;
      }
      Timer = setTimeout(()=>{
        let obj = {};
        obj.dsName = value;
        check_if_dsname_exists(obj).then(( res)=>{
          console.log(res,"check_get_name");
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

  /* 检测数据库用户名是否重复 */
  handleGetDBUserName = async (rule, value, callback) => {
    const { form } = this.props;
    const serverId = form.getFieldValue('serverId');
     const dsType = form.getFieldValue('dsType');
    const { data } = await dbUsernameIsExists({
      dbUsername: value,
      serverId,
      dsType:this.state.dsType,
      /*sourceId: 1,*/
    });
    if (data.data) {
      callback('该用户名已存在');
    } else {
      callback();
    }
  }
    /* 检测数据库密码是否重复 */
  handleGetDBPassword = async (rule, value, callback) => {
    const { form } = this.props;
    const serverId = form.getFieldValue('serverId');
    const { data } = await dbUsernameIsExists({
      dbPassword: value,
      serverId,
      dsType:this.state.dsType,
      /*sourceId: 1,*/
    });
    if (data.data) {
      callback('该密码已存在');
    } else {
      callback();
    }
  }

  hideModel(){
    const {dispatch,form} = this.props;
    dispatch({
      type:"resitionemodel/hide",
      visible:false
    });
    this.state.data = [];
    form.resetFields();
  }

  formItemLayout1 = {
    labelCol: { span: 6},
    wrapperCol: { span: 17 },
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

  handleRadioChange(e){
      this.setState({
        radioValue:e.target.value
      })
  }


  showRadioModel(readonly){
      const { getFieldDecorator } = this.props.form;
      const { info } = this.props.resitionemodel;
     if(this.state.radioValue === "oracle"){
          return(
            <div>
              <FormItem label="数据表空间: "  {...this.formItemLayout1} >
                {getFieldDecorator('datatablespace', {
                  initialValue:info.datatablespace,
                  rules: [{ required: true, message: '请输入数据表空间' }]
                })(
                  <Input disabled={readonly} />
                )}
              </FormItem>
              <FormItem label="索引表空间: "  {...this.formItemLayout1} >
                {getFieldDecorator('indextablespace', {
                  initialValue:info.indextablespace,
                  rules: [{ required: true, message: '请输入索引表空间' }]
                })(
                  <Input disabled={readonly} placeholder="请输入索引表空间"/>
                )}
              </FormItem>
            </div>
          )
     }else if(this.state.radioValue === "sqlserver"){
       return(
         <div>
           <FormItem label="示例名称: "  {...this.formItemLayout1} >
             {getFieldDecorator('instancename', {
               initialValue:info.instancename
             })(
               <Input disabled={readonly} placeholder="请输入示例名称"/>
             )}
           </FormItem>
          {
            /**
             * *
              <FormItem label="端口号: "  {...this.formItemLayout1} >
             {getFieldDecorator('dbPort', {
               initialValue:info.dbPort,
               rules: [{ required: true, message: '请输入端口号' },
                      {pattern:/^([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-5]{2}[0-3][0-5])$/, message: '请输入正确的端口号' }]
             })(
               <Input disabled={readonly} placeholder="请输入端口号"/>
             )}
           </FormItem>
             */
          }
         </div>
       )
     }
     
  }
 
   componentDidMount() {
    this.resquest();
   }
 


 componentWillReceiveProps(nextProps){

   const { visible } = nextProps.resitionemodel;
   if(visible){
      this.resquest();
   } 
 }
 
  resquest=(value)=>{

    const {renterId} = this.props.account;
    get_front_pos({type:"create",renterId:renterId}).then(( res)=>{
      const { data } = res.data;
      const rows = data && data.rows || [];
       this.setState({
          data:rows
       })
      
    });
  }

  handleFocus = (index)=>{

      const {data} = this.state;

      for(let key of data){
          if(index === key.id){
               this.setState({
                dsType:key.dsType
               })
                 return;
          }

      }

       console.log(index,data);
      
      
  }


  render(){


    console.log("重新渲染页面",this.props);

    const { getFieldDecorator } = this.props.form;
    const { visible,info,model } = this.props.resitionemodel;
    const options = this.state.data.map(d => <Select.Option key={d.id} value={d.id+""}>{d.serverName}</Select.Option>);
    const readonly = info.status === '已生效';
    return(

      <Modal
        visible={visible}
        title="注册前置机数据库基本信息"
        wrapClassName="vertical-center-modal RegisterModel"
        onOk={this.handleSubmit}
        onCancel={()=>{this.hideModel()}}
        maskClosable={false}
        confirmLoading={this.props.submitLoading}
      >
        {/*(2017.9.11新建元数据：)*/}
        <Form onSubmit={this.handleSubmit} >
            <FormItem
              label="所在前置机: "
              {...this.formItemLayout1}
              style={{marginBottom:10}}
            >
              {getFieldDecorator('serverId', {
                initialValue:info.serverName,
                rules: [{ required: true, message: '请选择所在前置机' }]
              })(
                <Select disabled={readonly} placeholder="请选择所在前置机"  onChange={this.handleFocus}>
                  {
                    this.state.data.map((index)=>
                      <Select.Option key={index.id} value={index.id}>{index.serverName}</Select.Option>
                    )
                  }
                </Select>
              )}
            </FormItem>
            <FormItem   label="数据库中文名: "  {...this.formItemLayout1} >
              {getFieldDecorator('dsName', {
                initialValue:info.dsName,
                validateTrigger: 'onBlur',
                rules: [{ required: true, message: '请输入数据库中文名称' }]
                 
              })(
                <Input disabled={readonly || options == [] || options == null || options==0} placeholder="请输入数据库中文名称" spellCheck={false} maxLength="50" />
              )}
          </FormItem>
            {/*<FormItem   label="数据库类型: "  style={{marginLeft:"6%"}} >
              {getFieldDecorator('dsType', {
                initialValue:this.state.radioValue
              })(
                <Radio.Group  onChange={this.handleRadioChange.bind(this)}>
                  <Radio.Button value="2">MySQL</Radio.Button>
                  <Radio.Button value="1">Oracle</Radio.Button>
                  <Radio.Button value="8">PostgreSQL</Radio.Button>
                  <Radio.Button value="9">My SQL server</Radio.Button>
                  <Radio.Button value="10">DB2</Radio.Button>
                  <Radio.Button value="11">Sybase</Radio.Button>
                  <Radio.Button value="12">Access</Radio.Button>
                </Radio.Group>
              )}
            </FormItem>*/}

            <FormItem   label="数据库名称: "  {...this.formItemLayout1} >
              {getFieldDecorator('dbDatabasename', {
              initialValue:info.dbDatabasename,
              validateFirst: true,
              validateTrigger: 'onBlur',
                
              })(
                <Input placeholder="请输入需要创建的数据库名称" disabled={!!info.dsId || options == [] || options == null || options==0} maxLength="50" spellCheck={false}/>
              )}
            </FormItem>
              {
                this.showRadioModel(readonly)
              }

          <FormItem   label="数据库用户名: "  {...this.formItemLayout1} >
            {getFieldDecorator('dbUsername', {
              initialValue:info.dbUsername,
              validateFirst: true,
              validateTrigger: 'onBlur',
             
            })(
              <Input disabled={readonly || options == [] || options == null || options==0} placeholder="请输入要创建的数据库用户名称" spellCheck={false} maxLength="20" />
            )}
          </FormItem>
          <FormItem   label="数据库密码: "  {...this.formItemLayout1} >
            {getFieldDecorator('dbPassword', {
              initialValue:info.dbPassword,
              validateTrigger: 'onBlur',
             
            })(
              <Input disabled={readonly || options == [] || options == null || options==0} maxLength="20" placeholder="请输入数据库密码" type="password"/>
            )}
          </FormItem>
          <FormItem label="备注：" {...this.formItemLayout1} style={{marginBottom:"8px"}} >
            {getFieldDecorator('remark',{
              initialValue: info.remark,
            })(<TextArea placeholder="请输入0-200的备注"  maxLength="200" rows={4} spellCheck={false}/>)}
          </FormItem>
        </Form>
      </Modal>
    )
  }
}

const RegisterModel = Form.create()(Register);
export default connect(({ resitionemodel,account }) => ({
  resitionemodel,account
}))(RegisterModel)
