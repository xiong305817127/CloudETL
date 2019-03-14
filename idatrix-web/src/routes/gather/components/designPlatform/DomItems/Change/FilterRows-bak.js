import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col,Card,Icon,Switch  } from 'antd';
import Modal from "components/Modal.js";
const Option = Select.Option;
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
const confirm = Modal.confirm;
import EditTable from '../../../common/EditTable'
import Condition from '../../../common/Condition'
class FilterRows extends React.Component {
   constructor(props){
    super(props);
    const { visible } = props.model;
    const { condition } = props.model.config;
      let data = [];

          for(var key in condition){  
             data.push(condition);   
          }  
      this.state = {
        visibleS:false,  
        dataSource:condition,
        InputData:[],
        InputDataS:'',
        oldStepList:[],
        StateStuts:[],
        data:{}
      }
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
     const { keyFieldsS  } = config;
    
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      const { data } = this.refs.editInput.props;
      console.log(values,"editInput");
  
      let obj = {};
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
         "sendFalseTo":values.sendFalseTo,
         "sendTrueTo": values.sendTrueTo,
         "condition": data,
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

  onChangeVis(){
     this.setState({
      visibleS:false
     })
  }
 componentDidMount(){
    this.Request();
  };
Request(){
    const { getInputFields,transname,text,getOutFields } = this.props.model;
    let obj = {};
    obj.transname = transname;
    obj.stepname = text;
    getOutFields(obj, data => {
      this.setState({
         InputData:data,
      })
    })
  };
  showModal1 = () => {
    this.setState({
      visibleS: true,
    });
  }
  handleOk1 = (e) => {
    this.setState({
      visibleS: false,
    });
  }
  handleCancel1 = (e) => {
    this.setState({
      visibleS: false,
    });
  }

   formItemLayout5 = {
       labelCol: { span: 7 },
      wrapperCol: { span: 14 },
  };

  plusOnchange= (e) =>{
    let count = 0;
    let arge = [];
      arge.push(count++);
         this.setState({
          StateStuts:arge
           })
        }

    SelectOnChange(value){
        this.setState({
           InputDataS:value
        })
    }

  render() {
    const { getFieldDecorator,getFieldValue } = this.props.form;
    const { text,config,visible,handleCheckName,prevStepNames,transname } = this.props.model;

    const {data,InputData} = this.state;
   const conditionProps = {
        data:this.state.dataSource,
        InputData:InputData
      }
    const formItemLayout1 = { 
      labelCol: { span: 7 },
      wrapperCol: { span: 14 },
    };
    const formItemLayout2 = {
      labelCol: { span: 10 },
      wrapperCol: { span: 14 },
    };
    const formItemLayout3 ={
      labelCol: { span: 6 },
      wrapperCol: { span: 10 },
    };
    return (
      <Modal
        visible={visible}
        title="过滤记录"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        width={850}
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
              <Select allowClear>
                    {
                     this.props.model.nextStepNames? this.props.model.nextStepNames.map((index,key)=>(<Select.Option key={index}>{index}</Select.Option>)):''
                    }
              </Select>
            )}
          </FormItem>
          <FormItem label="发送false数据给步骤"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
            {getFieldDecorator('sendFalseTo', {
              initialValue:config.sendFalseTo,
            })(
              <Select allowClear>
                    {
                     this.props.model.nextStepNames? this.props.model.nextStepNames.map((index,key)=>(<Select.Option key={index}>{index}</Select.Option>)):''
                    }
              </Select>
            )}
          </FormItem>
          

          <div style={{margin:"0 5%"}}>
           <Condition  {...conditionProps} ref="editInput" onChange={this.onChange}/>
          <Row>
             
             <Col>
                {/*  <Button onClick={this.handleOk1.bind(this)}><Icon type="plus"/></Button>*/}
             </Col>
          </Row>
          </div>

            <Modal
            title="输入一个值"
            visible={this.state.visibleS}
            onOk={this.handleOk1}
            onCancel={this.handleCancel1}
             wrapClassName="vertical-center-modal"
               maskClosable={false}
          >
            
              <FormItem label="类型" {...formItemLayout3} style={{marginBottom:"8px"}}>
                {getFieldDecorator('rightExactType', {
                  initialValue: config.condition.rightExactType,
                })(
                    <Select style={{ width: 200 }} onChange={this.SelectOnChange.bind(this)}>
                      <Option value="Number">Number</Option>
                      <Option value="String">String</Option>
                      <Option value="Date">Date</Option>
                      <Option value="Boolean">Boolean</Option>
                      <Option value="Integer">Integer</Option>
                      <Option value="BigNumber">BigNumber</Option>
                      <Option value="Binary">Binary</Option>
                      <Option value="Timestamp">Timestamp</Option>
                      <Option value="Internet Address">Internet Address</Option>
                   </Select>
                )}
              </FormItem>
              <FormItem label="值" {...formItemLayout3} style={{marginBottom:"8px"}}>
                {getFieldDecorator('rightExactName', {
                  initialValue: config.condition.rightExactName,
                })(
                    <Input />
                )}
              </FormItem>
              <FormItem label="转换格式" {...formItemLayout3} style={{marginBottom:"8px"}}>
                {getFieldDecorator('rightExactText', {
                  initialValue: config.condition.rightExactText,
                })(
                       <div>
                            {this.state.InputDataS === "Number" ?(
                             <Select allowClear style={{ width: 200 }}>
                                 <Option value="#,##0.###">#,##0.###</Option>
                                 <Option value="0.00">0.00</Option>
                                 <Option value="0000000000000">0000000000000</Option>
                                 <Option value="#.#">#.#</Option>
                                 <Option value="#">#</Option>
                                 <Option value="###,###,###.#">###,###,###.#</Option>
                                 <Option value="#######.###">#######.###</Option>
                                 <Option value="#####.###%">#####.###%</Option>
                                 <Option value="yyyy/MM/dd HH:mm:ss">yyyy/MM/dd HH:mm:ss</Option>
                                 <Option value="#,##0.###">#,##0.###</Option>
                              </Select>
                          ):null}
                          {this.state.InputDataS === "String" ?(
                             <Select allowClear style={{ width: 200 }}>
                                 <Option value="#,##0.###">#,##0.###</Option>
                              </Select>
                          ):null}
                          {this.state.InputDataS === "Date" ?(
                             <Select allowClear style={{ width: 200 }}>
                                 <Option value="yyyy/MM/dd HH:mm:ss.SSS">yyyy/MM/dd HH:mm:ss.SSS</Option>
                                 <Option value="yyyy/MM/dd HH:mm:ss.SSS XXX">yyyy/MM/dd HH:mm:ss.SSS XXX</Option>
                                 <Option value="yyyy/MM/dd HH:mm:ss">yyyy/MM/dd HH:mm:ss</Option>
                                 <Option value="yyyy/MM/dd HH:mm:ss XXX">yyyy/MM/dd HH:mm:ss XXX</Option>
                                 <Option value="yyyyMMddHHmmss">yyyyMMddHHmmss</Option>
                                 <Option value="yyyy/MM/dd">yyyy/MM/dd</Option>
                                 <Option value="yyyy-MM-dd">yyyy-MM-dd</Option>
                                 <Option value="yyyy-MM-dd HH:mm:ss">yyyy-MM-dd HH:mm:ss</Option>
                                 <Option value="yyyy-MM-dd HH:mm:ss XXX">yyyy-MM-dd HH:mm:ss XXX</Option>
                                 <Option value="yyyyMMdd">yyyyMMdd</Option>
                                 <Option value="MM/dd/yyyy">MM/dd/yyyy</Option>
                                 <Option value="MM/dd/yyyy HH:mm:ss">MM/dd/yyyy HH:mm:ss</Option>
                                 <Option value="MM-dd-yyyy">MM-dd-yyyy</Option>
                                 <Option value="MM-dd-yyyy HH:mm:ss">MM-dd-yyyy HH:mm:ss</Option>
                                 <Option value="MM/dd/yy">MM/dd/yy</Option>
                                 <Option value="MM-dd-yy">MM-dd-yy</Option>
                                 <Option value="dd/MM/yyyy">dd/MM/yyyy</Option>
                                 <Option value="dd-MM-yyyy">dd-MM-yyyy</Option>
                                 <Option value="yyyy-MM-dd'T'HH:mm:ss.SSSXXX">yyyy-MM-dd'T'HH:mm:ss.SSSXXX</Option>
                              </Select>
                          ):null}
                           {this.state.InputDataS === "Boolean" ?(
                             <Select allowClear style={{ width: 200 }}>
                                 <Option value="yyyy-MM-dd'T'HH:mm:ss.SSSXXX">yyyy-MM-dd'T'HH:mm:ss.SSSXXX</Option>
                              </Select>
                          ):null}

                       </div>
                       
                    
                )}
              </FormItem>
              <FormItem label="长度" {...formItemLayout3} style={{marginBottom:"8px"}}>
                {getFieldDecorator('rightExactLength', {
                  initialValue: config.condition.rightExactLength,
                })(
                    <Input />
                )}
              </FormItem>
              <FormItem label="精度" {...formItemLayout3} style={{marginBottom:"8px"}}>
                {getFieldDecorator('rightExactPrecision', {
                  initialValue: config.condition.rightExactPrecision,
                })(
                    <Input />
                )}
              </FormItem>
        </Modal>


        </Form>
      </Modal>
    );
  }
}
const Filter = Form.create()(FilterRows);

export default connect()(Filter);
