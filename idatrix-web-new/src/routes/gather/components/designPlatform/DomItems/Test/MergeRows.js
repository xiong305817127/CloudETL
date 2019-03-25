import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col,Card,Icon } from 'antd';
import Modal from "components/Modal.js";
const Option = Select.Option;
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
const confirm = Modal.confirm;
import EditTable from '../../../common/EditTable'

class MergeRows extends React.Component {

  constructor(props){
    super(props);
    const { visible } = props.model;
    this.state={
       visibleS:false  
    };
    if(visible === true) {
      const {compareFields} = props.model.config;
      let data = [];
      if (compareFields) {
        let count = 0;
        for (let index of compareFields) {
          data.push({
            "key": count,
            ...index
          });
          count++;
        }
      }
      this.state = {
        dataSource:data,
        InputData:[]
      }
    }
  }

  componentDidMount(){
    
  }

	


 hideModal = () => {
    const { dispatch } = this.props;
    dispatch({
      type:'items/hide',
      visible:false
    });
  };

  handleCreate = () => {

    const form = this.props.form;
    const { panel,transname,description,key,saveStep,text,config,formatTable } = this.props.model;
    const { compareFields } = config;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let obj = {};
      let sendFields = [];
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
        ...values
      };

      saveStep(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });

    });
  }
   formItemLayout3 = {
      labelCol: { span: 7 },
      wrapperCol: { span: 14 },
    };

  /*success() {
  	  const { getFieldDecorator } = this.props.form;
      const { condition } = this.props.model.config;
      console.log(condition,"condition");
	   confirm({
	    title: '输入一个值',
	    iconType:'form',
	     onOk() {
	      console.log('OK');
	    },
	    onCancel() {
	      console.log('Cancel');
	    },
	    content: (
	      <div>
	      <Form >
	          <FormItem label="类型" {...this.formItemLayout3}>
	            {getFieldDecorator('rightExactType', {
	              initialValue:condition.rightExactType
	            })(
	              <Select style={{ width: 170 }}>
				      <Option value="Number">Number</Option>
				      <Option value="String">String</Option>
				      <Option value="Data">Data</Option>
				      <Option value="Boolean">Boolean</Option>
				      <Option value="Integer">Integer</Option>
				      <Option value="BigNumber">BigNumber</Option>
				      <Option value="Binary">Binary</Option>
				      <Option value="Timestamp">Timestamp</Option>
				      <Option value="Internet Address">Internet Address</Option>
			      </Select>
	            )}
	          </FormItem>
	          <FormItem label="值" {...this.formItemLayout3}>
	            {getFieldDecorator('rightExactName', {
	              initialValue:condition.rightExactName
	            })(
	               <Input />
	            )}
	          </FormItem>
	          <FormItem label="装换格式" {...this.formItemLayout3}>
	            {getFieldDecorator('rightExactText', {
	              initialValue:condition.rightExactText
	            })(
	              <Select style={{ width: 170 }}>
				      <Option value="#">#</Option>
			      </Select>
	            )}
	          </FormItem>
	          <FormItem label="长度" {...this.formItemLayout3}>
	            {getFieldDecorator('rightExactLength', {
	              initialValue:condition.rightExactLength
	            })(
	               <Input />
	            )}
	          </FormItem>
	          <FormItem label="精度" {...this.formItemLayout3}>
	            {getFieldDecorator('rightExactPrecision', {
	              initialValue:condition.rightExactPrecision
	            })(
	               <Input />
	            )}
	          </FormItem>
            </Form >
	      </div>
	    ),
	    onOk() {},
	  });
	}*/
  onChangeVis(){
  	 this.setState({
  	 	visibleS:false
  	 })
  }
	

  render() {
    const { getFieldDecorator,getFieldValue } = this.props.form;
    const { text,config,visible,handleCheckName } = this.props.model;
      
    const formItemLayout1 = { 
      labelCol: { span: 7 },
      wrapperCol: { span: 14 },
    };
    const formItemLayout2 = {
      labelCol: { span: 4 },
      wrapperCol: { span: 21 },
    };
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 18 },
    };

    const setDisabled1 = ()=>{
      if(getFieldValue("countRows") === undefined){
        return !config.countRows;
      }else{
        if(getFieldValue("countRows")){
          return !getFieldValue("countRows");
        }else {
          return true;
        }
      }
    }

    return (

      <Modal
        visible={visible}
        title="合并记录"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        width={650}
        footer={[
                  <Button key="submit" type="primary" size="large" onClick={this.handleCreate.bind(this)} >
                    确定
                  </Button>,
                  <Button key="back" size="large" onClick={this.hideModal.bind(this)}>取消</Button>,
                ]}
        onCancel = {this.hideModal}>
        <Form >
          <FormItem label="步骤名称"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
            {getFieldDecorator('text', {
              initialValue:text,
              rules: [{ whitespace:true, required: true, message: '请输入步骤名称' },
                {validator:handleCheckName,message: '步骤名称已存在，请更改!' }]
            })(
              <Input />
            )}
          </FormItem>

          <FormItem label="发送true数据给步骤"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
            {getFieldDecorator('sendTrueTo', {
              initialValue:config.sendTrueTo,
            })(
              <Input />
            )}
          </FormItem>

          <FormItem label="发送false数据给步骤"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
            {getFieldDecorator('dFalseTo', {
              initialValue:config.dFalseTo,
            })(
              <Input />
            )}
          </FormItem>

          {/** ^[A-Za-z]+$  <Button style={{marginLeft:"95%"}} onClick={this.onVisibe.bind(this)}><Icon type="plus" /></Button> **/}
          <div style={{margin:"0 5%"}}>
             <Row>
                <Col span={10}>
                   <FormItem {...formItemLayout2}>
			            {getFieldDecorator('leftvalue', {
			              initialValue:config.leftvalue,
			              rules: [{ whitespace:true, required: true, message: '请输入字母' },
                                  { pattern: /^[A-Za-z]+$/, message: '请输入正确的字母' }]
			            })(
			              <Input placeholder="字段一"/>
			            )}
			          </FormItem>
                </Col>
                <Col span={4}>
                   <FormItem {...formItemLayout}>
			           {getFieldDecorator('function', {
			              initialValue:config.function,
			              rules: [{ whitespace:true, required: true, message: '请输入分隔符' } ]
			            })(
			               <Input placeholder="分隔符"/>
			            )}
			        </FormItem>
                </Col>
                <Col span={10}>
                    <FormItem {...formItemLayout2}>
			            {getFieldDecorator('rightvalue', {
			              initialValue:config.rightvalue,
			              rules: [{ whitespace:true, required: true, message: '请输入字母' },
                                  { pattern: /^[A-Za-z]+$/, message: '请输入正确的字母' }]
			            })(
			              <Input placeholder="字段二"/>
			            )}
			         </FormItem>
                </Col>
             </Row>


            
               
               {this.state.visibleS === "0" ?(
                <div>
                  <div><Button style={{marginLeft:"80%"}} onClick={this.onChangeVis.bind(this)}>Values</Button></div>
                   <FormItem label="类型" {...this.formItemLayout3}>
			            {getFieldDecorator('rightExactType', {
			              initialValue:config.condition.rightExactType
			            })(
			              <Select style={{ width: 170 }}>
						      <Option value="Number">Number</Option>
						      <Option value="String">String</Option>
						      <Option value="Data">Data</Option>
						      <Option value="Boolean">Boolean</Option>
						      <Option value="Integer">Integer</Option>
						      <Option value="BigNumber">BigNumber</Option>
						      <Option value="Binary">Binary</Option>
						      <Option value="Timestamp">Timestamp</Option>
						      <Option value="Internet Address">Internet Address</Option>
					      </Select>
			            )}
			          </FormItem>
			          <FormItem label="值" {...this.formItemLayout3}>
			            {getFieldDecorator('rightExactName', {
			              initialValue:config.condition.rightExactName
			            })(
			               <Input />
			            )}
			          </FormItem>
			          <FormItem label="装换格式" {...this.formItemLayout3}>
			            {getFieldDecorator('rightExactText', {
			              initialValue:config.condition.rightExactText
			            })(
			              <Select style={{ width: 170 }}>
						      <Option value="#">#</Option>
					      </Select>
			            )}
			          </FormItem>
			          <FormItem label="长度" {...this.formItemLayout3}>
			            {getFieldDecorator('rightExactLength', {
			              initialValue:config.condition.rightExactLength
			            })(
			               <Input />
			            )}
			          </FormItem>
			          <FormItem label="精度" {...this.formItemLayout3}>
			            {getFieldDecorator('rightExactPrecision', {
			              initialValue:config.condition.rightExactPrecision
			            })(
			               <Input />
			            )}
			          </FormItem>
              </div>):null
          }

          </div>
        </Form>
      </Modal>
    );
  }
}
const Merge = Form.create()(MergeRows);

export default connect()(Merge);
