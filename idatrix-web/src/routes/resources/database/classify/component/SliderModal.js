import React from 'react';
import { connect } from 'dva';
import { Form,Input } from 'antd';
import Modal from 'components/Modal';
import Style from '../index.less';

const FormItem = Form.Item;

//深度校验规则

const getRules = (num)=>{
  switch(num){
    case 1 :
      return {pattern: /^\d{2}$/, message: '请输入2位阿拉伯数字'};
    case 2 :
      return {pattern: /^\d{3}$/, message: '请输入3位阿拉伯数字'}; 
    default:
      return {pattern: /^\d+$/, message: '请输入合适位数阿拉伯数字'}  
  }
}


const Index = ({databaseModel,form,dispatch})=>{

  const { visible,config,actionType } = databaseModel;

  const {getFieldDecorator } = form;

    // 表单宽高
  const formItemLayout = {
    labelCol: {
      span: 9
    },
    wrapperCol: {
      span: 10,
    },
  };

  const handleOk = (e)=>{
    e.preventDefault();
    form.validateFields((err, values) => {
      if (!err) {
        const { id,parentId,dept} = config;

        if(actionType === "new"){
          dispatch({
            type:"databaseModel/saveNode",
            actionType:"new",
            payload:{id:0,parentId:id,...values}
          })  
        }else{
          dispatch({
            type:"databaseModel/saveNode",
            actionType:"edit",
            payload:{id,parentId,...values}
          })
        }

        form.resetFields();
      }
    });
  }

  const handleCancel = ()=>{
    form.resetFields();
    dispatch({
      type:"databaseModel/save",
      payload:{
        visible:false
      }
    })
  }

  const Info = actionType === "new"?{}:config;

  const Rules = actionType === "new"?getRules(config.dept):getRules(config.dept-1);

  return(
    <Modal
      visible={visible}
      title = {actionType === "new"?"新增资源分类":"编辑资源分类"}

      onOk={handleOk}
      onCancel={handleCancel}
    >
       <Form className={Style.formPadding}>
          <FormItem {...formItemLayout} label="信息资源名称：">
            {getFieldDecorator('resourceName', {
              initialValue: Info.resourceName ? Info.resourceName : "",
              rules: [
                { required: true, whitespace: true, message: '资源名称不能为空！' },
                { pattern:/^[0-9a-zA-Z\_\u4e00-\u9fa5]+$/, message: '资源名称不能存在非法字符!' },
              ],
            })(<Input  maxLength={50} />)}
          </FormItem>

          <FormItem {...formItemLayout} label="信息资源代码：">
            {getFieldDecorator('resourceEncode', {
              initialValue: Info.resourceEncode ? Info.resourceEncode:"",
              rules: [
                { required: true, whitespace: true, message: '请输入信息资源代码' },
                Rules
              ],
            })(<Input  maxLength={20} />)}
          </FormItem>
        </Form>
    </Modal>
  )
}

export default connect(({databaseModel})=>({databaseModel}))(Form.create()(Index));