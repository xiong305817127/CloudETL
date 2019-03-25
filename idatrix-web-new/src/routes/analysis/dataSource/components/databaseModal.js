import { connect } from "dva";
import { Button, Form, Input, Radio,Select,Col,Row,message } from 'antd';
import Modal from 'components/Modal';
import { dbUsernameIsExists } from 'services/metadataDataSystem';

const FormItem = Form.Item;
const TextArea = Input.TextArea;

const titleObj = {
  new:"新建",
  edit:"编辑",
  register:"注册"
}

let Timer = null;

const index = ({datasource,form,dispatch})=>{
    const { visible,frontList,action,info,type } = datasource;
    const { getFieldDecorator } = form;

    console.log(info,"信息");

    //确定提交
    const handleSubmit = (e)=>{
      e.preventDefault();
      form.validateFields({ force: true }, (err,values) => {
      if (!err) {
        if(action === "new"){
          dispatch({
             type:"datasource/handleSubmit",
             payload:{
                sourceId:1,
                status:0,
                dsType:type,
                type: "create",
                ...values
             }
          })  
        }else if(action === "register"){
          dispatch({
             type:"datasource/handleSubmit",
             payload:{
                sourceId:1,status:0,dsType:type,type:"register",
                ...values
             }
          })
        }else if(action === "edit"){
          values.dsId = info.dsId;
          if(info.serverName === values.serverId){
            values.serverId = info.frontEndServer.id;
          }
          dispatch({
             type:"datasource/handleUpdata",
             payload:{
                ...values
             }
          })
        }
        handleCancel();
      }
    });
    }

    //取消
    const handleCancel = ()=>{
        form.resetFields();
        dispatch({
           type:"datasource/save",
           payload:{
             visible:false
           }
        })
    }

    //切换前置机
    const handleChange = (e,option)=>{
      const { type } = option.props;  
      dispatch({
          type:"datasource/save",
          payload:{
            type
          }
      })
    }

    //密码
    const handleGetDBPassword = (rule, value, callback)=>{
      if(Timer){
          clearTimeout(Timer);
          Timer = null;
      }
      const serverId = form.getFieldValue('serverId');
      Timer = setTimeout(()=>{
          dbUsernameIsExists({ dbPassword: value,
            serverId,dsType:3,
          }).then(data=>{
             if(data.data.data){
                callback('该密码已存在');
             }else{
                callback();
             }
          })
      },1000);
    };

    //布局
    const  formItemLayout = {
      labelCol: { span: 6},
      wrapperCol: { span: 17 },
    };

    const  formItemLayout1 = {
      labelCol: { span: 6},
      wrapperCol: { span: 17 },
    };

    //选择显示
    const  showRadioModel = (readonly)=>{
     if(readonly === "oracle"){
          return(
            <div>
              <FormItem label="数据表空间: "  {...formItemLayout1} >
                {getFieldDecorator('datatablespace', {
                  initialValue:info.datatablespace,
                  rules: [{ required: true, message: '请输入数据表空间' }]
                })(
                  <Input disabled={readonly} />
                )}
              </FormItem>
              <FormItem label="索引表空间: "  {...formItemLayout1} >
                {getFieldDecorator('indextablespace', {
                  initialValue:info.indextablespace,
                  rules: [{ required: true, message: '请输入索引表空间' }]
                })(
                  <Input disabled={readonly} placeholder="请输入索引表空间"/>
                )}
              </FormItem>
            </div>
          )
     }else if(readonly === "sqlserver"){
       return(
         <FormItem label="示例名称: "  {...formItemLayout1} >
           {getFieldDecorator('instancename', {
             initialValue:info.instancename
           })(
             <Input disabled={readonly} placeholder="请输入示例名称"/>
           )}
         </FormItem>
       )
     }
  }

    const disabled = action !== "edit" || info.type === "register"?false:true;

    return(
      <Modal
        visible={visible}
        title={`${titleObj[action]}前置机数据库基本信息`}
        wrapClassName="vertical-center-modal DatabaseModel"
        onOk={handleSubmit}
        onCancel={handleCancel}
        maskClosable={false}
      >
        <Form>
          <FormItem
            label="所在前置机: "
            {...formItemLayout}
            style={{marginBottom:10}}
          >
            {getFieldDecorator('serverId', {
              initialValue:info.serverName,
              rules: [{ required: true, message: '请选择所在前置机' }]
            })(
              <Select  placeholder="请选择所在前置机" onChange={handleChange} disabled={disabled} >
                {
                  frontList.map((index)=>
                    <Select.Option key={index.id} type={index.dsType}  value={index.id+""}>{index.serverName}</Select.Option>
                  )
                }
              </Select>
            )}
          </FormItem>
          <FormItem   label="数据库中文名: "  {...formItemLayout1} >
            {getFieldDecorator('dsName', {
              initialValue:info.dsName,
              rules: [{ required: true, message: '请输入数据库中文名称' }]
            })(
              <Input  placeholder="请输入数据库中文名称" disabled={disabled} spellCheck={false} maxLength="50" />
            )}
          </FormItem>
          {action === "new"?(
            <FormItem   label="数据库名称: "  {...formItemLayout1} >
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
                <Input placeholder="请输入需要创建的数据库名称" disabled={disabled} maxLength="50" spellCheck={false}/>
              )}
            </FormItem>
          ):(
            <FormItem   label="数据库名称: "  {...formItemLayout1} >
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
                <Input placeholder="请输入需要创建的数据库名称" disabled={disabled} maxLength="50" spellCheck={false}/>
              )}
            </FormItem>    
          )}
          {
            showRadioModel("1111")
          }
          <FormItem   label="数据库用户名: "  {...formItemLayout} >
            {getFieldDecorator('dbUsername', {
              initialValue:info.dbUsername,
              rules: [
                { required: true, message: '请输入数据库用户名' },
              ]
            })(
              <Input placeholder="请输入要创建的数据库用户名称" disabled={disabled} spellCheck={false} maxLength="20" />
            )}
          </FormItem>
          {action === "new"?( 
            <FormItem   label="数据库密码: "  {...formItemLayout1} >
              {getFieldDecorator('dbPassword', {
                initialValue:info.dbPassword,
                validateTrigger: 'onBlur',
                rules: [
                  { required: true, message: '请输入数据库密码' },
                  { message: '至少8位，由大写字母、小写字母、特殊字符和数字组成', pattern: /^(?=^.{8,}$)(?=.*\d)(?=.*[\W_]+)(?=.*[A-Z])(?=.*[a-z])(?!.*\n).*$/ },
                   { validator:handleGetDBPassword }
                ]
              })(
                <Input  maxLength="20" placeholder="请输入数据库密码" disabled={disabled} type="password"/>
              )}
            </FormItem>
           ):(
            <FormItem   label="数据库密码: "  {...formItemLayout1} >
              {getFieldDecorator('dbPassword', {
                initialValue:info.dbPassword,
                validateTrigger: 'onBlur',
                rules: [
                  { required: true, message: '请输入数据库密码' }
                ]
              })(
                <Input  maxLength="20" placeholder="请输入数据库密码" disabled={disabled} type="password"/>
              )}
            </FormItem>
           )}
          <FormItem label="备注：" {...formItemLayout} style={{marginBottom:"8px"}} >
            {getFieldDecorator('remark',{
              initialValue: info.remark,
            })(<TextArea placeholder="请输入0-200的备注"  maxLength="200" rows={4} spellCheck={false}/>)}
          </FormItem>
        </Form>
      </Modal>
    )
}

export default connect(({
  datasource
})=>({ datasource }))(Form.create()(index));