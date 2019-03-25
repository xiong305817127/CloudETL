import React from "react";
import { connect } from 'dva';
import { Form,Select,Button,Input,Checkbox, notification,Row,Col,Card } from 'antd';
import Modal from "components/Modal.js";
const FormItem = Form.Item;
const ButtonGroup = Button.Group;
import EditTable from '../../../common/EditTable'

class FilterRecords extends React.Component {

  constructor(props){
    super(props);
    const { visible } = props.model;
    if(visible === true) {
      const {caseTargets} = props.model.config;
      let data = [];
      if (caseTargets) {
        let count = 0;
        for (let index of caseTargets) {
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
  };

  componentDidMount(){
    this.Request();
  }

  Request(){
    const { getInputFields,transname,text } = this.props.model;
    let obj = {};
    obj.transname = transname;
    obj.stepname = text;
    getInputFields(obj, data => {
      this.setState({
        InputData:data
      })
    })
  };

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
     const {caseTargets} = config;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }
      let sendFields = [];
      if(this.refs.caseTargets){
        if(this.refs.caseTargets.state.dataSource.length>0){
          let args = [ "caseValue", "caseTargetStep"];
          sendFields = formatTable(this.refs.caseTargets.state.dataSource,args);
        }
      }else{
        if(caseTargets){
          sendFields = caseTargets
        }
      }

      let obj = {};
      obj.transname = transname;
      obj.newname = (text === values.text?"":values.text);
      obj.stepname = text;
      obj.type = panel;
      obj.description = description;
      obj.config = {
         caseTargets:sendFields,
         ...values
      };
      saveStep(obj,key,data=>{
        if(data.code === "200"){
          this.hideModal();
        }
      });

    });
  }

  /*增加字段 keys*/
  handleAdd = ()=>{
    const data = {
      "caseValue":"",
      "caseTargetStep":""
    };
    this.refs.caseTargets.handleAdd(data);
  };

  /*删除字段*/
  handleDeleteFields = ()=>{
    this.refs.caseTargets.handleDelete();
  };

render() {
    const { getFieldDecorator } = this.props.form;
    const { text,config,visible,handleCheckName,nextStepNames,getInputSelect } = this.props.model;

    const formItemLayout1 = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 },
    };

    const columns =  [
      {
      title: '值',
      dataIndex: 'caseValue',
      width:"50%",
      key: 'caseValue',
      editable:true
    },{
      title: '目标步骤',
      dataIndex: 'caseTargetStep',
      width:"50%",
      key: 'caseTargetStep',
      selectable:true,
      selectArgs:getInputSelect(nextStepNames)
    }];

    return (
      <Modal
        visible={visible}
        title="Switch / Case"
        wrapClassName="vertical-center-modal"
        maskClosable={false}
        width={600}
        footer={[
                  <Button key="submit" type="primary" size="large" onClick={this.handleCreate} >
                    确定
                  </Button>,
                  <Button key="back" size="large" onClick={this.hideModal}>取消</Button>,
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
          <FormItem label="switch字段"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
            {getFieldDecorator('fieldname', {
              initialValue:config.fieldname
            })(
              <Select>
                {
                   this.state.InputData.map(index => <Select.Option value={index.name}>{index.name}</Select.Option>)
                }
              </Select>
            )}
          </FormItem>
           <FormItem label=""   style={{marginBottom:"8px",marginLeft:"25%"}}  {...formItemLayout1}>
               {getFieldDecorator('contains', {
                  valuePropName: 'checked',
                  initialValue:config.contains
                  })(
                 <Checkbox >使用字符串包含比较</Checkbox>
               )}
           </FormItem>
           <FormItem label="Case值数据类型"   style={{marginBottom:"8px"}} {...formItemLayout1}>
                {getFieldDecorator('caseValueType', {
                  initialValue:config.caseValueType+""
                })(
                  <Select>
                    <Select.Option value="0">None</Select.Option>
                    <Select.Option value="1">Number</Select.Option>
                    <Select.Option value="2">String</Select.Option>
                    <Select.Option value="3">Date</Select.Option>
                    <Select.Option value="4">Boolean</Select.Option>
                    <Select.Option value="5">Integer</Select.Option>
                    <Select.Option value="6">BigNumber</Select.Option>
                    <Select.Option value="7">Binary</Select.Option>
                    <Select.Option value="8">Timestamp</Select.Option>
                    <Select.Option value="9">Internet Address</Select.Option>
                  </Select>
                )}
          </FormItem>
           <FormItem label="Case值转换掩码"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
            {getFieldDecorator('caseValueFormat', {
              initialValue:config.caseValueFormat
            })(
              <Input />
            )}
          </FormItem>
          <FormItem label="Case值小数点符号"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
            {getFieldDecorator('caseValueDecimal', {
              initialValue:config.caseValueDecimal
            })(
              <Input />
            )}
          </FormItem>
          <FormItem label="Case值分组标志"   style={{marginBottom:"8px"}}  {...formItemLayout1}>
            {getFieldDecorator('caseValueGroup', {
              initialValue:config.caseValueGroup
            })(
              <Input />
            )}
          </FormItem>
          <div style={{margin:"10px 5% 0"}}>
            <Row style={{marginBottom:"5px"}}>
              <Col span={12}>
                <p style={{marginLeft:"5px"}}>Case值:</p>
              </Col>
              <Col span={12}>
                <ButtonGroup size={"small"} style={{float:"right"}} >
                  <Button onClick={this.handleAdd.bind(this)}>添加字段</Button>

                  <Button onClick={this.handleDeleteFields.bind(this)} >删除字段</Button>
                </ButtonGroup>
              </Col>
            </Row>
            <EditTable columns={columns} tableStyle="editTableStyle5" ref="caseTargets" scroll={{y: 140}} rowSelection={true} size={"small"} count={1} dataSource={this.state.dataSource}/>
          </div>
          <FormItem label="默认目标步骤"   style={{marginBottom:"8px",marginTop:"8px"}} {...formItemLayout1}>
                {getFieldDecorator('defaultTargetStepname', {
                  initialValue:config.defaultTargetStepname
                })(
                  <Select allowClear={true} >
                    {
                      nextStepNames.map(index=>{
                         return (
                           <Select.Option value={index}>{index}</Select.Option>
                         )
                      })
                    }

                  </Select>
                )}
          </FormItem>
        </Form>
      </Modal>
    );
  }
}
const FilterRecordsForm = Form.create()(FilterRecords);
export default connect()(FilterRecordsForm);
