/**
 * Created by Administrator on 2017/3/13.
 */
import React from 'react';
import { Button, Modal,Form,Input,Select} from 'antd';
import { connect } from 'dva';
const FormItem = Form.Item;
const Option = Select.Option;

class Tip1 extends React.Component{

  setModelHide(){
    const { dispatch } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    })
  };

  handleCancel(){
    const { dispatch } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    })
  };

  handleSelectChange(value){
    const { validateFields } = this.props.form;

    validateFields({
      Note:{
        errors:"字段错误"
      }
    });

     console.log(value);
  }


    render(){
      const { visible } = this.props;
      const { getFieldDecorator } = this.props.form;


       return(
         <Modal
           visible={visible}
           title="温馨提醒"
           wrapClassName="vertical-center-modal"
           okText="Create"
           style={{zIndex:50}}
           maskClosable={false}
           footer={[
                  <Button key="submit" type="primary" size="large"  onClick={()=>{this.setModelHide()}}>
                    确定
                  </Button>,
                  <Button key="back" size="large" onClick={()=>{this.handleCancel();}}>取消</Button>,
                ]}
         >
           <Form >
             <FormItem
                 label="Note"
                 labelCol={{ span: 5 }}
                 wrapperCol={{ span: 12 }}
             >
               {getFieldDecorator('note', {
                 rules: [{
                   type: 'email', message: 'The input is not valid E-mail!'
                 }],
               })(
                   <Input />
               )}
             </FormItem>
             <FormItem
                 label="Gender"
                 labelCol={{ span: 5 }}
                 wrapperCol={{ span: 12 }}
             >
               {getFieldDecorator('gender', {
                 rules: [{ required: true, message: 'Please select your gender!' }],
                 onChange:this.handleSelectChange.bind(this)
               })(
                   <Select
                       placeholder="Select a option and change input text above"
                   >
                     <Option value="male">male</Option>
                     <Option value="female">female</Option>
                   </Select>
               )}
             </FormItem>
             <FormItem
                 wrapperCol={{ span: 12, offset: 5 }}
             >
               <Button type="primary" htmlType="submit">
                 Submit
               </Button>
             </FormItem>
           </Form>
         </Modal>
       )
    }
}


  const Others = Form.create()(Tip1);
export default connect()(Others);
