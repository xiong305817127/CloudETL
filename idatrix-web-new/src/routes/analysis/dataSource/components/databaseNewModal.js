/**
 * Created by Administrator on 2017/5/22.
 */
import React from 'react';
import {connect} from 'dva';
import { Button, Form, Input, Radio,Select,Col,Row,message } from 'antd';
import { dbUsernameIsExists } from 'services/metadataDataSystem';
import { insert_database_table_fields,update_database_table_fields,get_front_pos,check_get_name,search_ftp_table_fields } from '../../../../services/metadata'
import Style from './DatabaseModel.css'
import Modal from 'components/Modal';
import { submitDecorator } from 'utils/decorator';

const FormItem = Form.Item;
const { TextArea } = Input;

let Timer;

@submitDecorator
class DataModel extends React.Component{
  state = {
    radioValue:"1",
    data:[],
    dsType:''
  }

  handleSubmit = (e) => {
    e.preventDefault();
    this.props.form.validateFields({ force: true }, (err,values) => {
      if (!err) {
        this.props.disableSubmit();
        const {dispatch} = this.props;
        const { model } = this.props.databasemodel;
        let arr={};
        arr.renterId=this.props.account.renterId;
        arr.sourceId=1;
        arr.dsType=this.state.dsType;
        arr.status = 0;
        arr.dbDatabasename=values.dbDatabasename;
        arr.dbPassword=values.dbPassword;
        arr.dbUsername=values.dbUsername;
        arr.dsName=values.dsName;
        arr.remark=values.remark;
        arr.serverId=values.serverId;
        if(model === "newmodel"){
                    
          insert_database_table_fields(arr).then(({ data })=>{
            this.props.enableSubmit();
            if (data && data.code === '200') {
              dispatch({
                type:'datasystemsegistration/changeView',
                 payload:{
                   actionKey:"updatemodel"
                 }
              })
              message.success('新建成功');
            }

            //   else if(data && data.code === '601'){
            //     message.error("密码强度不符合");
            // }else if(data && data.code === '602'){
            //       message.error("连接失败，请检查服务器配置");
            // }else if(data && data.code === '606'){
            //       message.error("请求失败");
            // } else {
            //   message.error("新增失败,请查看您所请求的前置机的IP是否存在");
            // }
          })
              
        }else if(model === "newmodelres"){
          let arr1={};
          arr1.renterId=this.props.account.renterId;
          arr1.sourceId=1;
          arr1.dsType=this.state.dsType;
          arr1.status = 0;
          arr1.dbDatabasename=values.dbDatabasename;
          arr1.dbPassword=values.dbPassword;
          arr1.dbUsername=values.dbUsername;
          arr1.dsName=values.dsName;
          arr1.remark=values.remark;
          arr1.serverId=values.serverId;
          arr1.type ='register';
           insert_database_table_fields(arr1).then(({ data })=>{
            this.props.enableSubmit();
            if (data && data.code === '200') {
              dispatch({
                type:'datasystemsegistration/changeView',
                 payload:{
                   actionKey:"updatemodel"
                 }
              })
              message.success('新建成功');
            }
            // else if(data && data.code === '601'){
            //     message.error("密码强度不符合");
            // }else if(data && data.code === '602'){
            //       message.error("连接失败，请检查服务器配置");
            // }else if(data && data.code === '606'){
            //       message.error("请求失败");
            // } else {
            //   message.error("新增失败,请查看您所请求的前置机的IP是否存在");
            // }
          })
        }
        else if(model === "editmodel"){
          const { info } = this.props.databasemodel;
          values.dsId = info.dsId;
          let serverName = info.serverName;
          if(serverName === values.serverId){
            values.serverId = info.frontEndServer.id;
          }

          // 新增renterId，直接传入
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
            // else {
            //   message.error(data && data.msg || '修改失败');
            // }
          })

        }
        this.hideModel();
      }
      this.props.enableSubmit();
    });
  }

    /* 检测数据库密码是否重复 */
  handleGetDBPassword = async (rule, value, callback) => {
    const { form } = this.props;
    const serverId = form.getFieldValue('serverId');
    const { data } = await dbUsernameIsExists({
      dbPassword: value,
      serverId,
      dsType:3,
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
      type:"databasemodel/hide",
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
      const { info } = this.props.databasemodel;
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
     /**
      *
       else{
        return (
          <FormItem   label="端口号: "  {...this.formItemLayout1} >
            {getFieldDecorator('dbPort', {
              initialValue:info.dbPort || '3306',
              rules: [{ required: true, message: '请输入端口号' },
                       {pattern:/^([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-5]{2}[0-3][0-5])$/, message: '请输入正确的端口号' }]
            })(
              <Input disabled={readonly} placeholder="请输入端口号"/>
            )}
          </FormItem>
        )
     }
      *
      */

  }
 handleFocus=(value)=>{
   const { model } = this.props.databasemodel;
    if(model === "newmodelres"){
      let obj = {};
        obj.type = '';

        // 新增renter直接传入
        // edited by steven leo
        const {renterId} = this.props.account;
        obj.renterId = renterId;
        get_front_pos(obj).then(( res)=>{
          const { data } = res.data;
          const rows = data && data.rows || [];
           for(let index of rows){
            if(value === index.serverId){
              this.setState({
                dsType:index.dsType,
                 data: rows,
              })
             return;
            }
          }
        });
    }else{
      let obj = {};
      obj.type = 'create';

      // 新增renter直接传入
      // edited by steven leo
      const {renterId} = this.props.account;
      obj.renterId = renterId;
      obj.type = "create";
      get_front_pos(obj).then(( res)=>{
        const { data } = res.data;
        const rows = data && data.rows || [];
         for(let index of rows){
          if(value === index.serverId){
            this.setState({
              dsType:index.dsType,
               data: rows,
            })
           return;
          }
        }
      });
    }
  
  }
  /*  { message: '用户名必须由3-20个字母或数字组成，且必须以字母开头', pattern: /^[a-z][a-z\d]{2,19}$/i},
    rules: [
            { required: true, message: '请输入数据库名称' },
            { pattern: /^(?=[a-z])[0-9a-z_-]+$/, message: '只能使用小写字母、数字、下划线，且必须以字母开头' },
          ]

            */
  render(){
  
    const { getFieldDecorator } = this.props.form;
    const { visible,info,model,modelDle } = this.props.databasemodel;

    const options = this.state.data.map(d => <Select.Option key={d.id} value={d.id+""}>{d.serverName}</Select.Option>);
    const readonly = info.status === '已生效' || modelDle!=="last";
  
    console.log(model !== "editmodel");
    console.log(model,"弹框类型");
    const str = model === "newmodelres"?"注册":"" || model === "editmodel"?"编辑":"" || model === "newmodel"?"新建":"";

    console.log((readonly || options == [] || options == null || options==0) && model!=="editmodel");
    console.log(this.props,"model信息",model,options,modelDle);

    return(

      <Modal
        visible={visible}
        title={`${str}前置机数据库基本信息`}
        wrapClassName="vertical-center-modal DatabaseModel"
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
                <Select disabled={modelDle =="last"} placeholder="请选择所在前置机" onFocus={this.handleFocus}>
                  {
                    this.state.data.map((index)=>
                      <Select.Option key={index.id} value={index.id+""}>{index.serverName}</Select.Option>
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
                <Input disabled={modelDle == "last"} placeholder="请输入数据库中文名称" spellCheck={false} maxLength="50" />
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
            {model === "newmodelres" || model === "editmodel"?(
                <FormItem   label="数据库名称: "  {...this.formItemLayout1} >
                  {getFieldDecorator('dbDatabasename', {
                  initialValue:info.dbDatabasename,
                  validateFirst: true,
                  validateTrigger: 'onBlur',
                    rules: [
                      { required: true, message: '请输入数据库名称' },
                      {
                        pattern: /^(?=[a-z])[0-9a-z_-]+$/,
                        message: "只能使用小写字母、数字、下划线，且必须以字母开头"
                      }
                    ]
                  })(
                    <Input placeholder="请输入需要创建的数据库名称" disabled={modelDle=="last" } maxLength="50" spellCheck={false}/>
                  )}
                </FormItem>
            ):(
                <FormItem   label="数据库名称: "  {...this.formItemLayout1} >
                  {getFieldDecorator('dbDatabasename', {
                  initialValue:info.dbDatabasename,
                  validateFirst: true,
                  validateTrigger: 'onBlur',
                    rules: [
                      { required: true, message: '请输入数据库名称' },
                      {
                        pattern: /^(?=[a-z])[0-9a-z_-]+$/,
                        message: "只能使用小写字母、数字、下划线，且必须以字母开头"
                      }                    ]
                  })(
                    <Input placeholder="请输入需要创建的数据库名称" disabled={modelDle=="last" } maxLength="50" spellCheck={false}/>
                  )}
                </FormItem>
            )
          }
            
              {
                this.showRadioModel(readonly)
              }

          <FormItem   label="数据库用户名: "  {...this.formItemLayout1} >
            {getFieldDecorator('dbUsername', {
              initialValue:info.dbUsername,
              validateFirst: true,
              validateTrigger: 'onBlur',
              rules: [
                { required: true, message: '请输入数据库用户名' },
              ]
            })(
              <Input disabled={(readonly || options == [] || options == null || options==0) && modelDle=="last"} placeholder="请输入要创建的数据库用户名称" spellCheck={false} maxLength="20" />
            )}
          </FormItem>
          
          {model === "newmodelres" || model === "editmodel"?(
              <FormItem   label="数据库密码: "  {...this.formItemLayout1} >
                {getFieldDecorator('dbPassword', {
                  initialValue:info.dbPassword,
                  validateTrigger: 'onBlur',
                  rules: [
                    { required: true, message: '请输入数据库密码' }
                  ]
                })(
                  <Input disabled={(readonly || options == [] || options == null || options==0) && modelDle=="last"} maxLength="20" placeholder="请输入数据库密码" type="password"/>
                )}
              </FormItem>
           ):(
              <FormItem   label="数据库密码: "  {...this.formItemLayout1} >
                {getFieldDecorator('dbPassword', {
                  initialValue:info.dbPassword,
                  validateTrigger: 'onBlur',
                  rules: [
                    { required: true, message: '请输入数据库密码' },
                    { message: '至少8位，由大写字母、小写字母、特殊字符和数字组成', pattern: /^(?=^.{8,}$)(?=.*\d)(?=.*[\W_]+)(?=.*[A-Z])(?=.*[a-z])(?!.*\n).*$/ },
                     { validator:this.handleGetDBPassword.bind(this) }
                  ]
                })(
                  <Input disabled={(readonly || options == [] || options == null || options==0) && modelDle=="last"} maxLength="20" placeholder="请输入数据库密码" type="password"/>
                )}
              </FormItem>
           )}
          
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

const DatabaseModel = Form.create()(DataModel);
export default connect(({ databasemodel,account }) => ({
  databasemodel,account
}))(DatabaseModel)
