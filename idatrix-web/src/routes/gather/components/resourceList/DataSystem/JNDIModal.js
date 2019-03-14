/**
 * Created by Administrator on 2018/1/29.
 */
import { Form,Modal,Input,Button } from "antd";
import { connect } from 'dva';
import { strEnc,strDec } from 'utils/EncryptUtil';
const FormItem = Form.Item;
import {checkJndiName } from '../../../../../services/gather';

let Timer = null;

const Index = ({ resourcecontent,dispatch,form })=>{
    const {visible,JNDIconfig} = resourcecontent;
    const {getFieldDecorator} = form;

    const handleModalOk = ()=>{
        form.validateFields((err,values)=>{
            if(err){
               return;
            }
            values.password = strEnc(values.password,values.name,JNDIconfig.type,values.username);
            dispatch({
                type:"resourcecontent/createJndi",
                payload:{
                  type:JNDIconfig.type,
                  ...values
                },
                actionType:JNDIconfig.name?"save":"new"
            });

            form.resetFields();
        })
    };

    const handleModalCancel = ()=>{
      dispatch({
        type:"resourcecontent/changeStatus",
        payload:{
          JNDIconfig:{},
          visible:false
        }
      });
      form.resetFields();
    };

    const handleModalDelete = ()=>{
      dispatch({
        type:"resourcecontent/deleteJndi",
        payload:{
          type:JNDIconfig.type,
          name:JNDIconfig.name
        }
      });
      form.resetFields();
    };

  const handleCheckName = (rule, value, callback) => {
    console.log(value,"名字检查进入");
      if(value &&value.trim()){
        if(Timer){
          clearTimeout(Timer);
          Timer = null;
        }
        Timer = setTimeout(()=>{
          checkJndiName({
             type:JNDIconfig.type,
              name:value
          }).then(( res)=>{
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
  };


  const   formItemLayout1 = {
    labelCol: { span: 6 },
    wrapperCol: { span: 14 }
  };

    return(
      <Modal
        title={JNDIconfig.name?"编辑JNDI源":"新建JNDI源"}
        visible={visible}
        onOk={handleModalOk}
        onCancel={handleModalCancel}
        width={650}
        footer={[
            <div key="model">
                {JNDIconfig.name?<Button key="delete" style={{float:"left"}} onClick={handleModalDelete}>删除</Button>:null}
            </div>,
            <Button key="cancel" onClick={handleModalCancel}>关闭</Button>,
            <Button key="sure" type="primary" onClick={handleModalOk}>{JNDIconfig.name?"保存":"创建"}</Button>
        ]}

      >
        <Form>
            <FormItem label="JNDI源名称"  {...formItemLayout1}>
            {getFieldDecorator('name', {
              initialValue:JNDIconfig.name?JNDIconfig.name:"",
              rules: JNDIconfig.name?[{ required: true, message: '请输入JNDI源名称' }]:[{ required: true, message: '请输入JNDI源名称' },
                {validator:handleCheckName,message: 'JNDI源名称已存在，请更改!' }]
            })(
              <Input  disabled={JNDIconfig.name?true:false} />
            )}
            </FormItem>
          <FormItem label="数据库类型"   style={{marginBottom:"8px"}} {...formItemLayout1} >
            <span className="ant-form-text">{JNDIconfig.type}</span>
          </FormItem>
          <FormItem label="驱动" style={{marginBottom:"8px"}}   {...formItemLayout1}>
            {getFieldDecorator('driver', {
              initialValue:JNDIconfig.driver
            })(
              <Input  />
            )}
          </FormItem>
          <FormItem label="URL"    {...formItemLayout1}>
            {getFieldDecorator('url', {
              initialValue:JNDIconfig.url,
              rules: [{ required: true, message: '请输入URL' },
              ]})(
              <Input  />
            )}
          </FormItem>

          <FormItem label="用户名"    {...formItemLayout1} >
            {getFieldDecorator("username", {
              initialValue:JNDIconfig.username,
              rules: [{ required: true, message: '请输入用户名' }]
            })(
              <Input />
            )}
          </FormItem>
          <FormItem label="密码"    {...formItemLayout1} >
            {getFieldDecorator('password', {
              initialValue:JNDIconfig.password?strDec(JNDIconfig.password,JNDIconfig.name,JNDIconfig.type,JNDIconfig.username):"",
              rules: [{ required: true, message: '请输入密码' }]
            })(
              <Input type="password"/>
            )}
          </FormItem>
        </Form>
      </Modal>
    )
};

const JNDIModal = Form.create()(Index);

export default  connect(({resourcecontent})=>({resourcecontent}))(JNDIModal);
